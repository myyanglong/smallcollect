package com.wecan.small;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wecan.Utils.ByteUtil;
import com.wecan.domain.PreferencesService;
import com.wecan.domain.SmallConfigs;
import com.wecan.domain.WaterMeter;
import com.wecan.service.RfServ;
import com.wecan.smallcollect.R;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 小表采集/区域选择界面
 */

public class SmallTabArea extends Activity implements OnClickListener, OnItemClickListener {
    private SmallAdapter meterAdapter = null;
    private ListView meter;
    private ServiceSmall sev_cg;
    private UartReceiver receiver;
    protected PreferencesService prservice = null;
    private Button btn_action, btn_clear;
    public ProgressDialog pialog;
    private Handler handler;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Receiver not registered")) {
                } else {
                    throw e;
                }
            }
        }

        super.onDestroy();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_tab_area);

        findView();
        initData();
        initView();

        receiver = new UartReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.weian.service.BROAD_CAST");
        registerReceiver(receiver, filter);
    }

    private void findView() {
        // TODO Auto-generated method stub
        meter = (ListView) this.findViewById(R.id.small_water_list);
        btn_action = (Button) this.findViewById(R.id.btn_action);
        btn_clear = (Button) this.findViewById(R.id.btn_clear);
    }

    private void initData() {
        // TODO Auto-generated method stub
        sev_cg = new ServiceSmall(this);
        prservice = new PreferencesService(this);
    }

    private void initView() {
        meterAdapter = new SmallAdapter(this, prservice.getActionId(), sev_cg.selectDevFromID());
        meter.setAdapter(meterAdapter);

        meter.setOnItemClickListener(this);
        btn_action.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
    }

    public class UartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bd = intent.getExtras();
            switch (bd.getInt("cmd")) {
                case 0:
                    sev_cg.update_water(bd);
                    UpdateProgressMsg("当前水表号:" + bd.getString("id_0"));
                    break;
                case 1:
                case 2:
                    ShowProgressDialogEnd("采集过程完毕:\n" +
                            "总数-" + Integer.toString(bd.getInt("all")) +
                            "，成功-" + Integer.toString(bd.getInt("sucess")));
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        int cmd = v.getId();

        switch (cmd) {
            /**
             * 点击事件
             */
            case R.id.btn_action:
                //读取水表数据
                ShowProgressDialog("采集中...");
                RfServ.startCollect(this, prservice.getActionId());
                break;
            case R.id.btn_clear:
                //清除数据
                sev_cg.clear_water(prservice.getActionId());
                ShowProgressClearEnd("数据清除完成");
                break;
            case R.id.btn_soketok:
                //上传数据到服务器
                List<WaterMeter> waterMeterList = new ArrayList<WaterMeter>();
                ServiceSmall serviceSmall = new ServiceSmall(this);
                waterMeterList = serviceSmall.selectWaterMeterFromID(prservice.getActionId(), false);
                List<byte[]> soketblist = new ArrayList<byte[]>();

                for (int i = 0; i < waterMeterList.size(); ) {
                    //水表id
                    byte[] id = ByteUtil.getInt(Integer.getInteger(waterMeterList.get(i).id));
                    soketblist.add(id);
                    //水表状态
                    byte[] sbzt = (waterMeterList.get(i).address).getBytes();
                    soketblist.add(sbzt);
                    //瞬时流量
                    byte[] rf = ByteUtil.getInt(waterMeterList.get(i).rf);
                    soketblist.add(rf);
                    //累计流量
                    byte[] btotal = ByteUtil.putDouble(Double.parseDouble(waterMeterList.get(i).total));
                    soketblist.add(btotal);
                    i++;
                }

                byte[] data = ByteUtil.putbyte(soketblist);
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // TODO Auto-generated method stub
                        //更新提示
                        Bundle b = msg.getData();
                        //	ToastUtils.showLongToast(SoketActivity.this, b.getString("prompt"));
                    }
                };

                new Thread(new MyThread(data)).start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View v, int index, long id) {
        // TODO Auto-generated method stub
        SmallConfigs cg = (SmallConfigs) adapter.getAdapter().getItem(index);
        prservice.save_actionId(cg.id);
        meterAdapter.action_id = cg.id;
        meterAdapter.notifyDataSetChanged();
    }

    public void UpdateProgressMsg(String str) {
        if (pialog != null) {
            if (pialog.isShowing())
                pialog.setMessage(str);
        } else
            ShowProgressDialog(str);
    }

    public void ShowProgressClearEnd(String str) {
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(str)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public void ShowProgressDialogEnd(String str) {
        RfServ.stopRfModule();
        pialog.dismiss();
        if (!isFinishing()) {
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setCancelable(false)
                    .setTitle("提示")
                    .setMessage(str)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
        }
    }

    public void ShowProgressDialog(String str) {
        pialog = new ProgressDialog(this);
        pialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pialog.setMessage(str);
        pialog.setCancelable(true);
        pialog.setCanceledOnTouchOutside(false);

        pialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    RfServ.stopRfModule();
                    pialog.dismiss();
                }
                return false;
            }
        });

        pialog.show();
    }

    class MyThread extends Thread {
        private byte[] data;

        public MyThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {

            try {
                int bytesum = 10 * 1024;
                int datasum = 9 + 2 + 4 + 1 + 2 + 15;
                int soketsum = 1024 - datasum;
                for (int i = 0; bytesum / soketsum < i; i++) {
                    //设置服务器地址
                    Socket clientSocket = new Socket("183.230.182.141", 11600);
                    clientSocket.setSoTimeout(5000);

//					ByteBuffer buffer = ByteBu3ffer.allocate(1024);
//					buffer.order(ByteOrder.LITTLE_ENDIAN);
                    byte[] title = {0x68, 0x0, 0x35, 0x68, 0x07, 0x01, 0x01, 0x08, 0x00};//包头
                    byte[] tail = {0, 0x16};//包尾
                    //计算CS校验和 title+data+tail 上传的byte
                    OutputStream osSend = clientSocket.getOutputStream();
                    osSend.write(data);
                    osSend.flush();
                }
            } catch (Exception e) {


            }

            super.run();
        }
    }
}

