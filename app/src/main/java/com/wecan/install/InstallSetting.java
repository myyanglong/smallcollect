package com.wecan.install;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.wecan.smallcollect.R;
import com.wecan.domain.PreferencesService;
import com.wecan.small.SmallTabError;

public class InstallSetting extends TabActivity implements OnClickListener {
	//定义Tab选项卡标示符
	private static final String SYSTEM_TAB = "system_tab";
	
	//定义Intent对象
	private Intent mSystemIntent;

	//定义TabHost对象
	private TabHost mTabHost;

	//定义单选按钮对象
	private RelativeLayout title_btn;
	PreferencesService service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_setting_grpchnl);

		initView();
		initData();
	}
	
	/**
	 * 初始化组件
	 */
	private void initView(){
		//得到TabHost
		mTabHost = getTabHost();
		mSystemIntent = new Intent(this, SmallTabError.class);
		title_btn = ((RelativeLayout) findViewById(R.id.title_btn));
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
		service = new PreferencesService(this);
		//给单选按钮设置监听
		title_btn.setOnClickListener(this);		
		//添加进Tab选项卡
		mTabHost.addTab(buildTabSpec(SYSTEM_TAB, mSystemIntent));
		
		
		mTabHost.setCurrentTabByTag(SYSTEM_TAB);		

	}
	private TabHost.TabSpec buildTabSpec(String tag, Intent intent) {
		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setContent(intent).setIndicator("");
		return tabSpec;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.title_btn:
				//startActivity(new Intent(SmallMain.this,EntryMenuActivity.class));
				this.finish();
			default:
				break;
		}
	}
	
}

