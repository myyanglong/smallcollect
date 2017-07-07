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
import android.widget.ProgressBar;

import com.wecan.Utils.ByteUtil;
import com.wecan.Utils.PbswUtils;
import com.wecan.Utils.ToastUtils;
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
    private byte[] deviceid;
    private ProgressBar progressBar;
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
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        meter.setOnItemClickListener(this);
        btn_action.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        this.findViewById(R.id.btn_soketok).setOnClickListener(this);
        this.findViewById(R.id.btn_txt).setOnClickListener(this);

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
            case R.id.btn_txt:
                byte[] css = {0, 1, (byte) 200, (byte) 0xC8, (byte) 200, 0, 0, 8};
                byte cccs = cs(css);
                break;
            case R.id.btn_soketok:
                //上传数据到服务器
                progressBar.setVisibility(View.VISIBLE);
                List<WaterMeter> waterMeterList = new ArrayList<WaterMeter>();
                ServiceSmall serviceSmall = new ServiceSmall(this);
                waterMeterList = serviceSmall.selectWaterMeterFromID(prservice.getActionId(), false);
                //  deviceid=ByteUtil.getInt(Integer.getInteger(prservice.getActionId()));//设备ID
                deviceid = ByteUtil.getInt(154085);
                List<byte[]> soketblist = new ArrayList<byte[]>();
                for (int i = 0; i < waterMeterList.size(); ) {
                    //水表id
                    byte[] id = ByteUtil.getInt(Integer.getInteger(waterMeterList.get(i).id));
                    soketblist.add(id);
                    //水表状态 1个字节
                    byte[] sbzt = (waterMeterList.get(i).address).getBytes();
                    soketblist.add(sbzt);
                    //瞬时流量 1个字节
                    byte rf = (byte) waterMeterList.get(i).rf;
                    byte[] brf = {rf};
                    soketblist.add(brf);
                    //累计流量
                    byte[] btotal = ByteUtil.putDouble(Double.parseDouble(waterMeterList.get(i).total));
                    soketblist.add(btotal);
                    i++;
                }
                //byte[] data = ByteUtil.putbyte(soketblist);
                byte[] data;
                data = new byte[2042];
                for (int d = 0; d < 2042; d++) {
                    data[d] = 6;
                }
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // TODO Auto-generated method stub
                        //更新提示
                        Bundle b = msg.getData();
                        if(b.getInt("yes")==1)
                        {
                            progressBar.setVisibility(View.GONE);
                            ToastUtils.showLongToast(SmallTabArea.this,"上传数据成功");
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            ToastUtils.showLongToast(SmallTabArea.this,"上传数据失败");
                        }
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

    /**
     * @param csbyte 需要求CS集合
     * @return CS校验值
     */
    public byte cs(byte[] csbyte) {
        byte cs = 0;
        for (int i = 0; i < csbyte.length; i++) {
            cs = (byte) (cs + csbyte[i]);
        }
        return cs;
    }

    /**
     * @param packlength int长度
     * @return byte长度
     */
    public byte[] packagelength(int packlength) {
        byte[] length = new byte[2];
        length[0] = (byte) (packlength >> 8);
        length[1] = (byte) (packlength & 0xFF);
        return length;
    }

    /**
     * 帧序号
     *
     * @param num
     * @return
     */
    public byte[] FrameNumber(int num) {
        byte[] fnum = new byte[2];
        fnum[0] = 0;
        if (num > 255) {
            fnum[0] = (byte) ((num & 0xFF00) >> 8);
        }
        fnum[1] = (byte) (num & 0xff);
        return fnum;
    }

    class MyThread extends Thread {
        private byte[] data;

        public MyThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            int num = 0;//帧序号
            //上传数据到服务器
            //包头 起始字符（68H）,长度L(L0 L1),起始字符（68H）,协议版本VER(主版本号,次版本号),AFN功能码,包控制域（帧控制符,帧序号） 9个字符
            // + 设备ID+TAG值+数据长度  deviceid:设备id TAG值：0x02
            try {
                Socket clientSocket = new Socket("183.230.182.141", 11500);
                clientSocket.setSoTimeout(5000);
                if (data.length < 1024 - (9 + 2 + 4 + 1 + 2)) {
                    int dataL = data.length;//数据长度
                    byte[] dataL1 = packagelength(dataL);
                    byte[] tagtitle = {0x02, dataL1[0], dataL1[1]};//Tag值 水表数据长度
                    byte[] csdata = new byte[dataL + 7];
                    System.arraycopy(deviceid, 0, csdata, 0, 4);
                    System.arraycopy(tagtitle, 0, csdata, 4, 3);
                    System.arraycopy(data, 0, csdata, 7, dataL);
                    byte[] socketdata = PbswUtils.encode(true, true, num, csdata);
                    OutputStream osSend = clientSocket.getOutputStream();
                    osSend.write(socketdata);
                    osSend.flush();

                } else if (data.length > 1024 - (9 + 2 + 4 + 1 + 2)) {
                    int datarum = data.length / 994;
                    int dataRemainder = data.length % 994;//余下的不够1024-(9+2+4+1+2)的包长度
                    if (dataRemainder > 0) {
                        dataRemainder = dataRemainder + 7;
                        datarum++;
                    }
                    for (int i = 0; i < datarum; i++) {
                        if (i + 1 == datarum) {
                            //最后一包
                            byte[] dataL1 = packagelength(dataRemainder);
                            byte[] tagtitle = {0x02, dataL1[0], dataL1[1]};//Tag值 水表数据长度
                            byte[] datai = new byte[dataRemainder-7];
                            System.arraycopy(data, i*994, datai, 0, dataRemainder-7);
                            byte[] csdata = new byte[dataRemainder];
                            System.arraycopy(deviceid, 0, csdata, 0, 4);
                            System.arraycopy(tagtitle, 0, csdata, 4, 3);
                            System.arraycopy(datai, 0, csdata, 7, dataRemainder-7);
                            byte[] socketdata = PbswUtils.encode(false, true, num, csdata);
                            OutputStream osSend = clientSocket.getOutputStream();
                            osSend.write(socketdata);
                            osSend.flush();

                        } else {
                            if (i == 0) {
//                                //第一包
                                byte[] dataL1 = packagelength(994);
                                byte[] tagtitle = {0x02, dataL1[0], dataL1[1]};//Tag值 水表数据长度
                                byte[] datai = new byte[994];
                                System.arraycopy(data, i*994, datai, 0, 994);

                                byte[] csdata = new byte[1001];
                                System.arraycopy(deviceid, 0, csdata, 0, 4);
                                System.arraycopy(tagtitle, 0, csdata, 4, 3);
                                System.arraycopy(datai, 0, csdata, 7, 994);
                                byte[] socketdata = PbswUtils.encode(true, false, num, csdata);
                                OutputStream osSend = clientSocket.getOutputStream();
                                osSend.write(socketdata);
                                osSend.flush();

                                num++;

                            } else {
                                //中间包
                                byte[] dataL1 = packagelength(994);
                                byte[] tagtitle = {0x02, dataL1[0], dataL1[1]};//Tag值 水表数据长度
                                byte[] datai = new byte[994];
                                System.arraycopy(data, i*994, datai, 0, 994);
                                byte[] csdata = new byte[1001];
                                System.arraycopy(deviceid, 0, csdata, 0, 4);
                                System.arraycopy(tagtitle, 0, csdata, 4, 3);
                                System.arraycopy(datai, 0, csdata, 7, 994);
                                byte[] socketdata = PbswUtils.encode(false, false, num, csdata);
                                OutputStream osSend = clientSocket.getOutputStream();
                                osSend.write(socketdata);
                                osSend.flush();

                                num++;
                            }
                        }
                    }
                }
                Message message = new Message();
                clientSocket.close();
                Bundle bundle=new Bundle();
                bundle.putInt("yes",1);
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("yes",0);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            super.run();
        }
    }
}

