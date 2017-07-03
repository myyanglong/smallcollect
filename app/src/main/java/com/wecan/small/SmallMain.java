package com.wecan.small;

import com.wecan.smallcollect.R;
import com.wecan.domain.PreferencesService;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressWarnings("deprecation")
public class SmallMain extends TabActivity implements OnCheckedChangeListener,OnClickListener {
	//定义Tab选项卡标示符
	private static final String DATA_TAB = "data_tab";
	private static final String AREA_TAB = "area_tab";
	private static final String COMMAND_TAB = "command_tab";
	private static final String SYSTEM_TAB = "system_tab";
	//定义Intent对象
	private Intent mDataIntent,mAreaIntent,mCommandIntent,mSystemIntent;

	//定义TabHost对象
	private TabHost mTabHost;

	//定义单选按钮对象
	private RadioButton dataRb,areaRb,totalRb,systemRb;
	private RelativeLayout title_btn;
	private PreferencesService prservice =null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prservice = new PreferencesService(this);
		if(prservice.getUserType() == 1)
			setContentView(R.layout.small_main_all);
		else
			setContentView(R.layout.small_main);

		initView();
		initData();
	}
	
	/**
	 * 初始化组件
	 */
	private void initView(){
		//得到TabHost
		mTabHost = getTabHost();
		
		//得到Intent对象
		mDataIntent = new Intent(this, SmallTabData.class);
		mAreaIntent = new Intent(this, SmallTabArea.class);
		mCommandIntent = new Intent(this, SmallTabCommand.class);
		if(prservice.getUserType() == 1)
			mSystemIntent = new Intent(this, SmallTabError.class);
		
		//得到单选按钮对象
		dataRb = ((RadioButton) findViewById(R.id.radio_home));
		areaRb = ((RadioButton) findViewById(R.id.radio_mention));
		totalRb = ((RadioButton) findViewById(R.id.radio_person_info));
		if(prservice.getUserType() == 1)
			systemRb = ((RadioButton) findViewById(R.id.radio_more));
		
		title_btn = ((RelativeLayout) findViewById(R.id.title_btn));
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
		dataRb.setOnCheckedChangeListener(this);
		areaRb.setOnCheckedChangeListener(this);
		totalRb.setOnCheckedChangeListener(this);
		if(prservice.getUserType() == 1)
			systemRb.setOnCheckedChangeListener(this);
		
		title_btn.setOnClickListener(this);		
		//添加进Tab选项卡
		mTabHost.addTab(buildTabSpec(DATA_TAB, mDataIntent));
		mTabHost.addTab(buildTabSpec(AREA_TAB, mAreaIntent));
		mTabHost.addTab(buildTabSpec(COMMAND_TAB, mCommandIntent));
		if(prservice.getUserType() == 1)
			mTabHost.addTab(buildTabSpec(SYSTEM_TAB, mSystemIntent));
		
		
		//设置当前默认的Tab选项卡页面
		dataRb.setChecked(true);
		mTabHost.setCurrentTabByTag(DATA_TAB);	
	}
	private TabHost.TabSpec buildTabSpec(String tag, Intent intent) {
		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setContent(intent).setIndicator("");
		
		return tabSpec;
	}
	
	/**
	 * Tab按钮选中监听事件
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_home:
				mTabHost.setCurrentTabByTag(DATA_TAB);
				
				dataRb.setBackgroundResource(R.drawable.small_tabs_btn_bg_d1);
				areaRb.setBackgroundDrawable(null);
				totalRb.setBackgroundDrawable(null);
				if(prservice.getUserType() == 1)
					systemRb.setBackgroundDrawable(null);
				break;
			case R.id.radio_mention:
				//dataRb.setBackgroundColor(0);
				dataRb.setBackgroundDrawable(null);
				mTabHost.setCurrentTabByTag(AREA_TAB);
				//VISIBLE:0  意思是可见的;INVISIBILITY:4 意思是不可见的，但还占着原来的空间;GONE:8  意思是不可见的，不占用原来的布局空间 
				//mMessageTipsArea.setVisibility(8);
				areaRb.setBackgroundResource(R.drawable.small_tabs_btn_bg_d1);
				dataRb.setBackgroundDrawable(null);
				totalRb.setBackgroundDrawable(null);
				if(prservice.getUserType() == 1)
					systemRb.setBackgroundDrawable(null);
				break;
			case R.id.radio_person_info:
				mTabHost.setCurrentTabByTag(COMMAND_TAB);
				//mMessageTipsTotal.setVisibility(8);
				totalRb.setBackgroundResource(R.drawable.small_tabs_btn_bg_d1);
				areaRb.setBackgroundDrawable(null);
				dataRb.setBackgroundDrawable(null);
				if(prservice.getUserType() == 1)
					systemRb.setBackgroundDrawable(null);
				break;
			default:
				if(prservice.getUserType() == 1){
					mTabHost.setCurrentTabByTag(SYSTEM_TAB);
					systemRb.setBackgroundResource(R.drawable.small_tabs_btn_bg_d1);
					areaRb.setBackgroundDrawable(null);
					totalRb.setBackgroundDrawable(null);
					dataRb.setBackgroundDrawable(null);
				}
				break;
			}
		}
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
