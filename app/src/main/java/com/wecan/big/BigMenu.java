package com.wecan.big;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.domain.Configs;
import com.wecan.domain.IntentInfo;
import com.wecan.install.InstallSelectWater;
import com.wecan.install.InstallSetting;
import com.wecan.service.WaterMeterService;

public class BigMenu extends Activity implements OnClickListener{
	private LinearLayout layout01,layout02,layout03;
	private Configs cgtemp;
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_menu);
		initView();
		initData();
	}
	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		bundle = intent.getExtras();
	}
	private void initView(){
		//得到布局组件对象并设置监听事件
		layout01 = (LinearLayout)findViewById(R.id.llayout01);
		layout02 = (LinearLayout)findViewById(R.id.llayout02);
		layout03 = (LinearLayout)findViewById(R.id.llayout03);
		//layout04 = (LinearLayout)findViewById(R.id.llayout03);
		layout01.setOnClickListener(this);
		layout02.setOnClickListener(this);
		layout03.setOnClickListener(this);
		//layout04.setOnClickListener(this);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	public static boolean isWifiConnect(Context context) {//判断WiFi是否打开并连接成功
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(info.isConnected() == false){
			Toast toast = Toast.makeText(context, "Wifi is no Connect",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);//选择Toast出现的位置
			toast.show();
		}
		return info.isConnected();
	}
	@Override
	public void onClick(View v) {
		WaterMeterService service = new WaterMeterService(this);
		switch(v.getId()){
			case R.id.llayout01:
				//isWifiConnect(this);
				//Log.i("debug", "eror");
				sendBroadcast(new IntentInfo("2150400001",8));
				//2147483647
				//2150400001
				break;
			case R.id.llayout03:
				service.clearWaterMeter();
				switch(service.initXMLData(0)){
					case 0:
						Toast.makeText(this,
					    		"初始化成功"
				    			, Toast.LENGTH_LONG).show();
						break;
					case 1:
						Toast.makeText(this,
					    		"打开文件失败，请先下载数据"
				    			, Toast.LENGTH_LONG).show();
						break;
					default:
						Toast.makeText(this,
					    		"读取数据失败，请重新下载数据"
				    			, Toast.LENGTH_LONG).show();
						break;
				}
				finish();
				break;
			case R.id.llayout02:
				isWifiConnect(this);
				break;
			default:
				break;
		}
	}
}
