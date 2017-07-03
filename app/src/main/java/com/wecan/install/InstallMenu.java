package com.wecan.install;

import com.wecan.smallcollect.R;
import com.wecan.domain.ConfigAdapter;
import com.wecan.domain.Configs;
import com.wecan.service.WaterMeterService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InstallMenu extends Activity implements OnClickListener{
	private LinearLayout layout01,layout02,layout03,layout04;
	private Bundle bundle;
	
	private WaterMeterService service = new WaterMeterService(this);
	private Configs cgtemp = new Configs();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_menu);
		initView();
		initData();
	}
	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		bundle = intent.getExtras();
		cgtemp.f_id = bundle.getString("id");
		cgtemp.type = bundle.getInt("type") + 1;
	}
	private void initView(){
		//得到布局组件对象并设置监听事件
		layout01 = (LinearLayout)findViewById(R.id.llayout01);
		layout02 = (LinearLayout)findViewById(R.id.llayout02);
		layout03 = (LinearLayout)findViewById(R.id.llayout03);
		layout04 = (LinearLayout)findViewById(R.id.llayout04);
		layout01.setOnClickListener(this);
		layout02.setOnClickListener(this);
		layout03.setOnClickListener(this);
		layout04.setOnClickListener(this);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()){
			case R.id.llayout01:
				intent = new Intent(this, InstallSelectWater.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
		    	intent.putExtras(bundle);
		    	startActivity(intent);
				this.finish();
				break;
			case R.id.llayout02:
				//intent = new Intent(this, InstallSelectMeter.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
		    	//intent.putExtras(bundle);
		    	//startActivity(intent);
				
				break;
			case R.id.llayout03:
//		    	startActivity(new Intent(this, InstallSetting.class));
		    	this.finish();
				break;
			default:
				break;
		}
	}
	public void displaystr(boolean flag){
		if(flag)
			Toast.makeText(this,"添加成功", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(this,"添加失败", Toast.LENGTH_LONG).show();
	}
}
