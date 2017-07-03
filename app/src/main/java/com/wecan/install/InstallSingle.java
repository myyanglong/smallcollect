package com.wecan.install;

import java.util.ArrayList;

import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.domain.Configs;
import com.wecan.domain.IntentInfo;
import com.wecan.service.RfServ;
import com.wecan.service.WaterMeterService;

public class InstallSingle extends Activity implements OnClickListener,OnItemClickListener,OnItemLongClickListener{

	private ListView lv ;
	private ListViewAdapter waterAdapter = null,waterMissAdapter = null,meterAdapter = null;
//	List<WaterMeter> meterList = new ArrayList<WaterMeter>();
	
	private Button s_btn1,s_btn2,s_btn3;
	private RelativeLayout s_back,s_add;
	private LinearLayout s_img;
	private TextView s_txt,s_info;
	
	private PopupWindow popupMater ;
	private LinearLayout s_config,s_exit,s_meter,s_read,s_debug,s_concentrator,s_connet,s_gprs,s_ver,s_update;
	private ImageView img_id;
	private ServiceInstall svinstall;
	private int listtype = 0;
	public Bundle bundle;
	public UartReceiver receiver;
	public ProgressDialog pBar;
	
	private WaterMeterService sv = new WaterMeterService(this);
	private Configs cgtemp = new Configs();
	public  int double_cmd = 0;
	private List<InstallListView> sel_list;
	private List<InstallListView> oth_list;
//	private String f_id;
	public int f_flag;

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		if(receiver != null) {
			try {
			    unregisterReceiver(receiver);
			} catch (IllegalArgumentException e) {			   
			}
		}
		
		super.onDestroy();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_setting);
		
		findView();
		initView();
		initData();
		initPopwindow();
		
		receiver = new UartReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.weian.service.BROAD_CAST");
		registerReceiver(receiver, filter);
	}

	private void findView() {
		// TODO Auto-generated method stub
		lv = (ListView) findViewById(R.id.list_tbset);
		s_txt = (TextView) findViewById(R.id.s_txt);
		s_info = (TextView) findViewById(R.id.s_info);
		s_img = (LinearLayout) findViewById(R.id.s_img);
		s_btn1 = (Button) findViewById(R.id.s_btn1);
		s_btn2 = (Button) findViewById(R.id.s_btn2);
		s_btn3 = (Button) findViewById(R.id.s_btn3);
		s_back = (RelativeLayout) findViewById(R.id.s_back);
		s_add = (RelativeLayout) findViewById(R.id.s_add);
		img_id = (ImageView) findViewById(R.id.img_id);
		
	}

	private void initPopwindow() {
		// TODO Auto-generated method stub
		View contentView ;
		contentView = getLayoutInflater().inflate(R.layout.s_setting_popwindow, null);
		
		popupMater = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		popupMater.setFocusable(true);//取得焦点
		popupMater.setBackgroundDrawable(new BitmapDrawable());
		popupMater.setAnimationStyle(R.style.animation);
		
		//s_wakeup = (LinearLayout)contentView.findViewById(R.id.s_wakeup);
		//s_wakeup.setOnClickListener(this);
		
		s_meter = (LinearLayout)contentView.findViewById(R.id.s_meter);
		s_meter.setOnClickListener(this);
		
		s_config = (LinearLayout)contentView.findViewById(R.id.s_config);
		s_config.setOnClickListener(this);
		
		
		s_read = (LinearLayout)contentView.findViewById(R.id.s_read);
		s_read.setOnClickListener(this);
		
		s_debug = (LinearLayout)contentView.findViewById(R.id.s_debug);
		s_debug.setOnClickListener(this);
		
		s_exit = (LinearLayout)contentView.findViewById(R.id.s_exit);
		s_exit.setOnClickListener(this);
		
		s_concentrator = (LinearLayout)contentView.findViewById(R.id.s_concentrator);
		s_connet = (LinearLayout)contentView.findViewById(R.id.s_connet);
		s_gprs = (LinearLayout)contentView.findViewById(R.id.s_gprs);
		s_update = (LinearLayout)contentView.findViewById(R.id.s_update);
		s_ver = (LinearLayout)contentView.findViewById(R.id.s_ver);
		s_concentrator.setOnClickListener(this);
		s_connet.setOnClickListener(this);
		s_gprs.setOnClickListener(this);
		s_update.setOnClickListener(this);
		s_ver.setOnClickListener(this);
		
		int type = bundle.getInt("type");
		if(type == 0){
			s_concentrator.setVisibility(0);
			s_connet.setVisibility(0);
			s_gprs.setVisibility(0);
		}
		else{
			s_concentrator.setVisibility(8);
			s_connet.setVisibility(8);
			s_gprs.setVisibility(8);
			if (type == 3) {		// 虚拟设备
				s_meter.setVisibility(8);
				s_read.setVisibility(8);
				s_update.setVisibility(8);
				s_ver.setVisibility(8);
			}
		}
		//s_update.setVisibility(8);	
	}

	private void initView() {
		// TODO Auto-generated method stub
		lv.setOnItemClickListener(this);
		s_txt.setOnClickListener(this);
		s_info.setOnClickListener(this);
		s_img.setOnClickListener(this);
		s_btn1.setOnClickListener(this);
		s_btn2.setOnClickListener(this);
		s_btn3.setOnClickListener(this);
		s_add.setOnClickListener(this);
		s_back.setOnClickListener(this);
		s_btn2.setBackgroundResource(R.drawable.install_tab_bg_n);
		s_btn3.setBackgroundResource(R.drawable.install_tab_bg_n);
		lv.setOnItemLongClickListener(this);
	}
	private void initData() {
		// TODO Auto-generated method stub
		//WaterMeter(String id,int type,String remarke, String floor,String door,boolean bflag)
		svinstall = new ServiceInstall(this);
		Intent intent = getIntent();
		bundle = intent.getExtras();
//		f_id = bundle.getString("f_id");
		cgtemp.f_id = bundle.getString("id");
		cgtemp.address = bundle.getString("address");
		cgtemp.type = bundle.getInt("type") + 1;
		if(cgtemp.type > 2)
			cgtemp.type = 2;
		switch(bundle.getInt("type")){
			case 0:
				img_id.setImageResource(R.drawable.s_setting_j);
				break;
			case 1:
				img_id.setImageResource(R.drawable.s_setting_c);
				break;
			default:
				img_id.setImageResource(R.drawable.s_setting_z);
				break;
		}
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		meterAdapter = new ListViewAdapter(this,svinstall.selectMeterForDev(bundle.getString("id")),bundle.getString("id"));

		lv.setAdapter(waterAdapter);
		
        if(bundle.getInt("type") == 0)
        {
        	s_info.setText("集中器:" + bundle.getString("id"));
        	s_txt.setText("地址:" + bundle.getString("address"));
        }
        else if(bundle.getInt("type") == 1)
        {
        	s_info.setText("采集器:" + bundle.getString("id"));
        	s_txt.setText("地址:" + bundle.getString("address"));
        }
        else if (bundle.getInt("type") == 2)
        {
        	s_info.setText("中继器:" + bundle.getString("id"));
        	s_txt.setText("地址:" + bundle.getString("address"));
        }
        else {
        	s_info.setText("抄表区域:" + bundle.getString("id"));
        	s_txt.setText("地址:" + bundle.getString("address"));
        }
        s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
        s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
        
        pBar = new ProgressDialog(this);
		pBar.setMessage("设备配置中...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圈圈状
		pBar.setCancelable(false);
     
	}
	private void initAddDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null); 
        final EditText editTextAddress = (EditText) textEntryView.findViewById(R.id.editTextName); 
        final EditText editTextNum = (EditText)textEntryView.findViewById(R.id.editTextNum);
        editTextNum.setKeyListener(new DigitsKeyListener(false,true));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入设备信息").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               cgtemp.id = editTextNum.getText().toString();
               cgtemp.address = editTextAddress.getText().toString();
               displaystr(sv.addConfigs(cgtemp));
               UpdateMeterList();
               //displaystr(true);
             }
        });
        builder.show();
	}
	public void CompareFileCheckNum(String str){
		if(sv.GetFileCheckNum().equals(str))
			setDoubleActionCommand(10);
		else
			Toast.makeText(this,"升级密码错误", Toast.LENGTH_LONG).show();
	}
	private void ConfigDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.configdialog, null); 
        final EditText editTextAddress = (EditText) textEntryView.findViewById(R.id.editTextName); 
        //editTextAddress.setKeyListener(new DigitsKeyListener(false,true));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入设备升级密码").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	CompareFileCheckNum(editTextAddress.getText().toString());
            	//displaystr(true);
                 //;
               //displaystr(true);
             }
        });
        builder.show();
	}
	private void initUpdateDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null); 
        final EditText editTextAddress = (EditText) textEntryView.findViewById(R.id.editTextName); 
        final EditText editTextNum = (EditText)textEntryView.findViewById(R.id.editTextNum);
        final TextView s_id = (TextView)textEntryView.findViewById(R.id.s_id);
        s_id.setVisibility(8);
        editTextNum.setVisibility(8);
        //editTextNum.setKeyListener(new DigitsKeyListener(false,true));
        editTextAddress.setText(cgtemp.address);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入设备地址").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
               //cgtemp.id = editTextNum.getText().toString();
            	cgtemp.id = bundle.getString("id");
            	cgtemp.address = editTextAddress.getText().toString();
            	displaystr(sv.updateConfigs(cgtemp));
            	UpdateMeterList();
            	s_txt.setText(cgtemp.address);
             }
        });
        builder.show();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.s_btn1:
				lv.setAdapter(waterAdapter);
				s_btn1.setBackgroundResource(R.drawable.install_tab_bg_f);
				s_btn1.setTextColor(0xfffc9a00);
				s_btn2.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn2.setTextColor(0xff484444);
				s_btn3.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn3.setTextColor(0xff484444);
				listtype = 0;
				break;
			case R.id.s_btn2:
				lv.setAdapter(waterMissAdapter);
				s_btn1.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn1.setTextColor(0xff484444);
				s_btn2.setBackgroundResource(R.drawable.install_tab_bg_f);
				s_btn2.setTextColor(0xfffc9a00);
				s_btn3.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn3.setTextColor(0xff484444);
				listtype = 1;
				break;
			case R.id.s_btn3:
				lv.setAdapter(meterAdapter);
				s_btn1.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn1.setTextColor(0xff484444);
				s_btn2.setBackgroundResource(R.drawable.install_tab_bg_n);
				s_btn2.setTextColor(0xff484444);
				s_btn3.setBackgroundResource(R.drawable.install_tab_bg_f);
				s_btn3.setTextColor(0xfffc9a00);
				listtype = 2;
				break;
			case R.id.s_img:
				popupMater.showAtLocation(this.findViewById(R.id.s_setting), Gravity.BOTTOM, 0, 0);
				break;
			case R.id.s_txt:
			case R.id.s_info:
				initUpdateDialog();
				break;
			case R.id.s_back:
				this.finish();
				break;
			case R.id.s_add:
				Intent intent = new Intent(this, InstallSelectWater.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
		    	intent.putExtras(bundle);
//		    	intent.putExtra("f_id", f_id);
		    	startActivity(intent);
				break;
			case R.id.s_meter:
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				initAddDialog();
				break;
			case R.id.s_config://发送配置表号 E1
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				setDoubleActionCommand(1);
				break;
			case R.id.s_read://唤醒测试
				setDoubleActionCommand(2);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_debug:
				setDoubleActionCommand(3);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_exit:
				setDoubleActionCommand(4);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_concentrator:
				setDoubleActionCommand(5);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_connet:
				setDoubleActionCommand(6);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_gprs:
				setDoubleActionCommand(7);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_ver:
				setDoubleActionCommand(11);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			case R.id.s_update:
				ConfigDialog();
				//setDoubleActionCommand(10);
				Key_Lock();
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				break;
			default:
				break;
		}
	}
	public void Key_Lock() {
		s_config.setClickable(false);
		s_read.setClickable(false);
		s_debug.setClickable(false);
		s_exit.setClickable(false);
		s_concentrator.setClickable(false);
	}
	public void unKey_Lock() {
		s_config.setClickable(true);
		s_read.setClickable(true);
		s_debug.setClickable(true);
		s_exit.setClickable(true);
		s_concentrator.setClickable(true);
	}
	public void displaystr(boolean flag){
		if(flag)
			Toast.makeText(this,"操作成功", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(this,"操作失败", Toast.LENGTH_LONG).show();
	}


	@Override
	public void onItemClick(AdapterView<?> listView, View itemLayout, int position, long id) {
		// TODO Auto-generated method stub
		if(listView.getId() == R.id.list_tbset){
			if(listtype == 2){
				
				il =(InstallListView) lv.getAdapter().getItem(position);
				if(il.id.length() == 10 && Integer.parseInt(il.id.substring(4, 5)) > 3 && Integer.parseInt(il.id.substring(4, 5)) < 10){
					new AlertDialog.Builder(this)
					.setTitle("采集数据")
					.setIcon(null)
					.setCancelable(false)
					.setMessage("采集大表  "+ il.id + " ?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									setDoubleActionCommand(9);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									dialog.dismiss();
								}
							}).create().show();
				}
				else{
					Intent intent = new Intent(this, InstallSingle.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
			    	Bundle bundle = new Bundle();
			    	bundle.putInt("flag", 0);
			    	bundle.putString("f_id", cgtemp.f_id);
			    	bundle.putString("id", il.id);
			    	bundle.putString("address", il.address);
			    	bundle.putInt("type", il.action_type);
			    	intent.putExtras(bundle);
			    	startActivity(intent);
		    	}
			}
			else{
				ListViewAdapter adp = (ListViewAdapter)lv.getAdapter();
			
				adp.update(position);
				adp.notifyDataSetChanged();
				/*
				for (int i = 0; i <= position; i++) {
					adp.update(i);
					adp.notifyDataSetChanged();
				}*/
			}
		}
	}
	public void UpdateMeterList(){
		meterAdapter = new ListViewAdapter(this,svinstall.selectMeterForDev(bundle.getString("id")),bundle.getString("id"));
		s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
		if(listtype == 2){
        	lv.setAdapter(meterAdapter);
		}
	}
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		meterAdapter = new ListViewAdapter(this,svinstall.selectMeterForDev(bundle.getString("id")),bundle.getString("id"));
		s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
        s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
       
        Log.i("debug", "onPostResume:" + meterAdapter.getCount());
        
        waterAdapter.notifyDataSetChanged();
        waterMissAdapter.notifyDataSetChanged();
        meterAdapter.notifyDataSetChanged();
        
        switch(listtype){
	        case 0:
	        	lv.setAdapter(waterAdapter);
	        	break;
	        case 1:
	        	lv.setAdapter(waterMissAdapter);
	        	break;
	        default:
	        	lv.setAdapter(meterAdapter);
	        	break;
        }
	}
	
	protected void onStart() {
		
        super.onStart();
	}
	public void display(String str){
		Toast.makeText(this,str, Toast.LENGTH_LONG).show();
	}
	public void updateListView(String id,int type){
		InstallListView il = null;
		
		if(type == 0){
			il = waterMissAdapter.CheckMeter(id);
			if(il != null){
				if(listtype != 2){
					il.type = type + 1 ;
					if(waterAdapter.add(il))
						svinstall.updateWaterMeterTag(id, 1);
				}
				switch(listtype){
			        case 0:
			        	lv.setAdapter(waterAdapter);
			        	s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
			        	break;
			        case 1:
			        	lv.setAdapter(waterMissAdapter);
			        	s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
			        	break;
			        default:
			        	meterAdapter.updateTag(id, 1);
			        	svinstall.updateMeterTag(id, 1);
			        	lv.setAdapter(meterAdapter);
			        	break;
				}
			}
		}
		else{
			if(listtype == 2){
				meterAdapter.updateTag(id, 2);
				svinstall.updateMeterTag(id, 2);
			}
			else{
				waterMissAdapter.updateTag(id, 2);
				svinstall.updateWaterMeterTag(id, 2);
			}
		}
	}
	public void deleteListView(String id,int type){
		switch(type){
	        case 0:
	        	svinstall.updateWaterMeterDel(id, bundle.getInt("type"));
	        	il = waterAdapter.CheckMeter(id);
	        	lv.setAdapter(waterAdapter);
	        	s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
	        	break;
	        case 1:
	        	svinstall.updateWaterMeterDel(id, bundle.getInt("type"));
	        	il = waterMissAdapter.CheckMeter(id);
	        	lv.setAdapter(waterMissAdapter);
	        	s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
	        	break;
	        default:
	        	svinstall.DeleteMeter(id);
	        	il = meterAdapter.CheckMeter(id);
	        	lv.setAdapter(meterAdapter);
	        	s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
	        	break;
		}
		
	}
	public void updateListViewDis(){
		List<InstallListView> datas;
		if(listtype == 1)
			datas = waterMissAdapter.getDatas();
		else
			datas = waterAdapter.getDatas();
		for(int i=0;i < datas.size();i++){
			if(datas.get(i).flag == true){
				svinstall.updateWaterMeterSec(datas.get(i).id);
				if(listtype == 0)
		        	waterAdapter.CheckMeter(datas.get(i).id);
				else
					waterMissAdapter.CheckMeter(datas.get(i).id);
				i = i - 1;
			}
		} 
		if(listtype == 1){
			lv.setAdapter(waterMissAdapter);
//			s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
		}
		else{
			lv.setAdapter(waterAdapter);
//			s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getCount()));
		}
		s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
		s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
		
	}
	public class UartReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
		    Bundle bd = intent.getExtras();
		    	ActionReceiverDouble(bd);
	    }
	}
	public void ActionReceiverDouble(Bundle bd){
		Log.i("debug", "ActionReceiverDouble:" + bd.getInt("cmd"));
		switch(bd.getInt("cmd")){
			case 0:
				UpdateProgressMsg(String.format("组网中：%s", bd.getString("id")));
				svinstall.updateWaterMeterTag(bd.getString("id"), 2);
				break;
			case 1:
				ShowProgressDialogEnd(String.format("水表数:%d, 成功:%d", bd.getInt("all"), bd.getInt("sucess")));
				waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
				waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
				waterMissAdapter.notifyDataSetChanged();
				waterAdapter.notifyDataSetChanged();
				s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
		        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
		        switch(listtype){
		        case 0:
		        	lv.setAdapter(waterAdapter);
		        	break;
		        case 1:
		        	lv.setAdapter(waterMissAdapter);
		        	break;
		        default:
		        	lv.setAdapter(meterAdapter);
		        	break;
		        }		        
		        unKey_Lock();
				break;
			case 2:
				ShowProgressDialogEnd("操作失败");
				unKey_Lock();
				break;
	    }
	}
	
	private int list_water_num = 0,list_miss_num = 0,list_meter_num = 0;
	private ProgressDialog pialog;
	public void UpdateProgressMsg(String str){
		if(pialog.isShowing())
			pialog.setMessage(str);
    }
	public void ShowProgressDialogEnd(String str){
		RfServ.stopRfModule();
		pialog.dismiss();
		new AlertDialog.Builder(this)
		.setIcon(null)
		.setCancelable(false)
		.setTitle("提示")
		.setMessage(str)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
	public void ShowProgressDialog(String str){
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

    public void DoubleUpdateListViewWrite(String id,int type,boolean flag){
		
		if(flag){
			waterMissAdapter.doubleWriteMeter(id,type);
		}
		else{
			meterAdapter.doubleWriteMeter(id,type);
		}
		
	}
	public void initSendList(){
		sel_list = new ArrayList<InstallListView>();
		oth_list = new ArrayList<InstallListView>();
		/*
		for(int i = 0;i < waterAdapter.getCount();i++){
			svinstall.updateWaterMeterTag(waterAdapter.getDatas().get(i).id,0);
		}*/
		svinstall.resetWaterMeterForDev(bundle.getString("id"));
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		meterAdapter = new ListViewAdapter(this,svinstall.selectMeterForDev(bundle.getString("id")),bundle.getString("id"));
		s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
        s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
		sel_list.addAll(waterMissAdapter.doubleGetSelfWater(true));
		oth_list.addAll(waterMissAdapter.doubleGetSelfWater(false));
		
		list_water_num = 0;
		list_miss_num = 0;
		list_meter_num = 0;
		
		if(sel_list != null)
			list_water_num = sel_list.size();
		if(oth_list != null)
			list_miss_num = oth_list.size();
		if(meterAdapter != null)
			list_meter_num = meterAdapter.getDatas().size();
		
		waterAdapter.notifyDataSetChanged();
		waterMissAdapter.notifyDataSetChanged();
		meterAdapter.notifyDataSetChanged();
	}

	public void debugtest(){
		initSendList();
		sendBroadcast(new IntentInfo(bundle.getString("id"),bundle.getInt("type"),sel_list,oth_list,meterAdapter.getDatas()));
		
	}
	public void setDoubleActionCommand(int type){
		switch(type){
			case 0:
				break;
			case 1:
				ShowProgressDialog("配置中...");
				configWater();
				ShowProgressDialogEnd("配置成功！");
				unKey_Lock();
				break;
			case 3:
				svinstall.resetWaterMeterTag(bundle.getString("id"),1);
				waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
				waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
				ShowProgressDialog("设备唤醒，组网准备中... ...");
				innetWater();
				break;
			case 4:
				ShowProgressDialog("设备唤醒，组网准备中... ...");
				innetWater();
				break;

		}
	}
	
	
	public void SendDelMsg(String str){
		if(listtype == 2)
			sendBroadcast(new IntentInfo(this,str,bundle.getString("id"),12,false));
		else
			sendBroadcast(new IntentInfo(this,str,bundle.getString("id"),2,false));
	}
	
	public void updateDoubleListView(String id,int type){
		InstallListView il = null;
		if(type == 0){
			il = waterMissAdapter.CheckMeter(id);
			if(il != null){
				if(listtype != 2){
					il.type = type + 1 ;
					if(waterAdapter.add(il))
						svinstall.updateWaterMeterTag(id, 1);
				}
				switch(listtype){
			        case 0:
			        	lv.setAdapter(waterAdapter);
			        	s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
			        	break;
			        case 1:
			        	lv.setAdapter(waterMissAdapter);
			        	s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
			        	break;
			        default:
			        	meterAdapter.updateTag(id, 1);
			        	svinstall.updateMeterTag(id, 1);
			        	lv.setAdapter(meterAdapter);
			        	break;
				}
			}
		}
		else{
			if(listtype == 2){
				meterAdapter.updateTag(id, 2);
				svinstall.updateMeterTag(id, 2);
			}
			else{
				waterMissAdapter.updateTag(id, 2);
				svinstall.updateWaterMeterTag(id, 2);
			}
		}
	}
	InstallListView il;
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int index,long id) {
		// TODO Auto-generated method stub
		il =(InstallListView) adapter.getAdapter().getItem(index);
		//Log.i("debug","onItemLongClick "+ cgtemp.f_id);
		if(listtype == 2){
			new AlertDialog.Builder(this)
				.setTitle("删除设备")
				.setIcon(null)
				.setCancelable(false)
				.setMessage("确定删除 "+ il.id + " ?")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								//if(il.type == 0)
									deleteListView(il.id,2);
								//else
									//SendDelMsg(il.id);
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								dialog.dismiss();
							}
						}).create().show();
		}
		else{
			new AlertDialog.Builder(this)
			.setTitle("删除水表号")
			.setIcon(null)
			.setCancelable(false)
			.setMessage("确定删除  ?")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							updateListViewDis();
						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							dialog.dismiss();
						}
					}).create().show();
		}
		return true;
	}
	public void readupdateLisViewToDB(){
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
		lv.setAdapter(waterMissAdapter);
	}
	public void updateLisViewToDB(){
		int i;
		for(i = 0;i < waterMissAdapter.getCount();i++){
			svinstall.updateWaterMeterTag(waterMissAdapter.getDatas().get(i).id,waterMissAdapter.getDatas().get(i).type);
		}
		for(i = 0;i < meterAdapter.getCount();i++){
			svinstall.updateMeterTag(meterAdapter.getDatas().get(i).id,meterAdapter.getDatas().get(i).type);
		}
		waterMissAdapter.notifyDataSetChanged();
		meterAdapter.notifyDataSetChanged();
		waterAdapter.notifyDataSetChanged();
		
		
        switch(listtype){
	        case 0:
	        	lv.setAdapter(waterAdapter);
	        	break;
	        case 1:
	        	lv.setAdapter(waterMissAdapter);
	        	break;
	        default:
	        	lv.setAdapter(meterAdapter);
	        	break;
        }
		
	}

	public void updateBuildLisViewToDB(String fail_meter){
		for(int i = 0;i < waterMissAdapter.getCount();i++){
			if(waterMissAdapter.getDatas().get(i).type == 1 && bundle.getString("id").equals(waterMissAdapter.getDatas().get(i).action_id))
			{
				waterMissAdapter.getDatas().get(i).type = 2 ;
			}
			svinstall.updateWaterMeterTag(waterMissAdapter.getDatas().get(i).id,waterMissAdapter.getDatas().get(i).type);
		}
		/*
		for(int i = 0;i < meterAdapter.getCount();i++){
			if(meterAdapter.getDatas().get(i).type == 1)
				meterAdapter.getDatas().get(i).type = 2 ;
			svinstall.updateMeterTag(meterAdapter.getDatas().get(i).id,meterAdapter.getDatas().get(i).type);
		}*/
		Log.i("debug","updateBuildLisViewToDB:" +waterMissAdapter.getCount());
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		//meterAdapter = new ListViewAdapter(this,svinstall.selectMeterForDev(bundle.getString("id")),bundle.getString("id"));
		s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
        //s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
        
        waterMissAdapter.notifyDataSetChanged();
        waterAdapter.notifyDataSetChanged();
        
        switch(listtype){
	        case 0:
	        	lv.setAdapter(waterAdapter);
	        	break;
	        case 1:
	        	lv.setAdapter(waterMissAdapter);
	        	break;
	        default:
	        	lv.setAdapter(meterAdapter);
	        	break;
	    }
	}
	public void updateMeterLisViewToDB(String fail_meter){
		for(int i = 0;i < meterAdapter.getCount();i++){
			if(meterAdapter.getDatas().get(i).type == 1 && bundle.getString("id").equals(meterAdapter.getDatas().get(i).f_id))
			{
				meterAdapter.getDatas().get(i).type = 2 ;
			}
			svinstall.updateMeterTag(meterAdapter.getDatas().get(i).id,meterAdapter.getDatas().get(i).type);
		}
		
		Log.i("debug","updateBuildLisViewToDB:" +waterMissAdapter.getCount());
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		s_btn3.setText(String.format(getResources().getString(R.string.setting_m),meterAdapter.getCount()));
        
        waterAdapter.notifyDataSetChanged();
        
        switch(listtype){
	        case 0:
	        	lv.setAdapter(waterAdapter);
	        	break;
	        case 1:
	        	lv.setAdapter(waterMissAdapter);
	        	break;
	        default:
	        	lv.setAdapter(meterAdapter);
	        	break;
	    }
	}
	
	/**
	 * add by jtao
	 */
	private void configWater() {
		svinstall.resetWaterMeterTag(bundle.getString("id"),1);
		waterAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),true));
		waterMissAdapter = new ListViewAdapter(this,bundle.getString("id"),svinstall.selectWaterMeterFromID(bundle.getString("id"),false));
		
		waterMissAdapter.notifyDataSetChanged();
		waterAdapter.notifyDataSetChanged();		
		
		s_btn1.setText(String.format(getResources().getString(R.string.setting_w_id),waterAdapter.getCount()));
        s_btn2.setText(String.format(getResources().getString(R.string.setting_w_miss),waterMissAdapter.getInitCount() + "/" + waterMissAdapter.getCount()));
        
		
        switch(listtype){
	        case 0:
	        	lv.setAdapter(waterAdapter);
	        	break;
	        case 1:
	        	lv.setAdapter(waterMissAdapter);
	        	break;
	        default:
	        	lv.setAdapter(meterAdapter);
	        	break;
        }
	}
	
	private void innetWater() {
		RfServ.startInnet(this, bundle.getString("id"));
	}
}


