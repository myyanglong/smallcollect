package com.wecan.activity;

import com.wecan.service.UartService;
import com.wecan.smallcollect.R;
import com.wecan.big.BigMain;
import com.wecan.domain.MyImageView;
import com.wecan.install.InstallMain;
import com.wecan.login.HelpMain;
import com.wecan.login.LoginMain;
import com.wecan.small.SmallMain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	//private static final int TOAST_TIME = 1000;
	MyImageView main_small,main_big,main_install,main_other;
	
	private LinearLayout m_s_layout,m_b_layout,m_d_layout,m_h_layout,m_i_layout;
	private TextView cpright;
	//private int type = 5,flag = 5;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.m_main);
		initView();
		
		startService(new Intent(this, UartService.class));
	}
	
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
		stopService(new Intent(this, UartService.class));
        super.onDestroy();
    }

	public static boolean isWifiConnect(Context context) {//判断WiFi是否打开并连接成功
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(info.isConnected() == false){
			Toast toast = Toast.makeText(context, "Wifi is no Connect",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);//选择Toast出现的位置
			toast.show();
		}
		return info.isConnected();
	}
	
//	private void getData() {
//		isWifiConnect(this);//判断wifi是否连接
//		m_i_layout.setClickable(true);
//		m_b_layout.setClickable(true);
//		m_s_layout.setClickable(true);
//		
//		PreferencesService preferencesService = new PreferencesService(this);
//		type = preferencesService.getUserType();
//		System.out.println(type);
//		flag = preferencesService.getMeterType();
//		System.out.println(flag);
//		if(type == 1 || type == 0){
//			m_i_layout.setBackgroundResource(R.drawable.m_install_uv);
//			m_i_layout.setClickable(false);
//			if(flag == 0){
//				m_b_layout.setBackgroundResource(R.drawable.m_big_uv);
//				m_b_layout.setClickable(false);
//			}
//			else if(flag == 1){
//				m_s_layout.setBackgroundResource(R.drawable.m_small_uv);
//				m_s_layout.setClickable(false);
//			}
//		}
//		else if(type == 3 ||type == 4){
//			if(flag == 0){
//				m_b_layout.setBackgroundResource(R.drawable.m_big_uv);
//				m_b_layout.setClickable(false);
//			}else if(flag == 1){
//				m_s_layout.setBackgroundResource(R.drawable.m_small_uv);
//				m_s_layout.setClickable(false);
//			}
//		}
//		else {
//			Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
//		}
//	}
	
	private void initView(){
		//得到布局组件对象并设置监听事件
		m_s_layout = (LinearLayout)findViewById(R.id.m_s_layout);
		m_b_layout = (LinearLayout)findViewById(R.id.m_b_layout);
		m_d_layout = (LinearLayout)findViewById(R.id.m_d_layout);
		m_h_layout = (LinearLayout)findViewById(R.id.m_h_layout);
		m_i_layout = (LinearLayout)findViewById(R.id.m_i_layout);
		cpright = (TextView)findViewById(R.id.cpright);
		
		m_i_layout.setOnClickListener(this);
		m_h_layout.setOnClickListener(this);
		m_d_layout.setOnClickListener(this);
		m_b_layout.setOnClickListener(this);
		m_s_layout.setOnClickListener(this);
		
		Spannable superSpan = new SpannableString("®测器  Copyright 2015-2019");
		superSpan.setSpan(new SuperscriptSpan(), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		cpright.setText(superSpan);
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.m_s_layout:				
				startActivity(new Intent(MainActivity.this,SmallMain.class)); 
				break;
			case R.id.m_b_layout:
				startActivity(new Intent(MainActivity.this,BigMain.class)); 
				break;
			case R.id.m_d_layout:
				startActivity(new Intent(MainActivity.this,LoginMain.class)); 
				break;
			case R.id.m_h_layout:
				startActivity(new Intent(MainActivity.this,HelpMain.class));
				break;
			case R.id.m_i_layout:
				startActivity(new Intent(MainActivity.this,InstallMain.class));
				break;
			//case R.id.m_l_layout:
				//startActivity(new Intent(MainActivity.this,BigActivity.class)); 
				//break;
			default:
				break;
		}
		
	}
}