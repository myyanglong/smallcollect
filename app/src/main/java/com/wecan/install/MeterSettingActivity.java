package com.wecan.install;


import com.wecan.smallcollect.R;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MeterSettingActivity extends TabActivity implements OnCheckedChangeListener {
	//定义Tab选项卡标示符
	private static final String DATA_TAB = "data_tab";
	private static final String SEL_TAB = "select_tab";
	private static final String SYS_TAB = "system_tab";
	
	//private static final int[] strId ={R.string.command_sleep,R.string.command_rev,R.string.command_send,R.string.command_wakeup};

	//定义Intent对象
	private Intent mDataIntent,mSelIntent,mSysIntent;

	//定义TabHost对象
	private TabHost mTabHost;

	//定义单选按钮对象
	private RadioButton dataRb,selRb,sysRb,backRb;
	
	//private int nMessage;
	//private UartReceiver receiver;
	
	//PreferencesService service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter);

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
		mDataIntent = new Intent(this, InstallSingle.class);
		mSelIntent = new Intent(this, InstallSelectWater.class);
		//mSysIntent = new Intent(this, TabSystem.class);
		
		//得到单选按钮对象
		dataRb = ((RadioButton) findViewById(R.id.rd_1));
		selRb = ((RadioButton) findViewById(R.id.rd_2));
		sysRb = ((RadioButton) findViewById(R.id.rd_3));
		backRb = ((RadioButton) findViewById(R.id.rd_4));
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
		//service = new PreferencesService(this);
		//给单选按钮设置监听
		dataRb.setOnCheckedChangeListener(this);
		selRb.setOnCheckedChangeListener(this);
		sysRb.setOnCheckedChangeListener(this);
		backRb.setOnCheckedChangeListener(this);
		
		//启动服务
        //startService(new Intent(EntryActivity.this, UartService.class));
         
	
		//添加进Tab选项卡
		mTabHost.addTab(buildTabSpec(DATA_TAB, mSelIntent));
		mTabHost.addTab(buildTabSpec(SEL_TAB, mSelIntent));
		mTabHost.addTab(buildTabSpec(SYS_TAB, mSysIntent));
		
		
		//设置当前默认的Tab选项卡页面
		dataRb.setChecked(true);
		mTabHost.setCurrentTabByTag(SEL_TAB);		

	}
	
	protected void onStart() {//重写onStart方法
		/*
		receiver = new UartReceiver();
		IntentFilter filter = new IntentFilter();//创建IntentFilter对象
		filter.addAction("com.weian.service.BROAD_CAST");
		registerReceiver(receiver, filter);//注册Broadcast Receiver*/
        super.onStart();
	}
	public class UartReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
		    Bundle bundle=intent.getExtras();
		    if(bundle.getInt("command") == 0){
		    	System.out.println( "onReceive---" + bundle.getInt("command_id") );
		    	switch(bundle.getInt("command_id")){
			    	case 0:
			    		//Toast.makeText(context,
				    			// context.getString(strId[0]) + context.getString(R.string.settingdone)
				    			//, Toast.LENGTH_LONG).show();
			    		break;
			    	case 1:
			    		break;
			    	case 2:
			    		break;
			    	default:
			    		break;
		    	}
		    	
		    }
	    }
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
				case R.id.rd_1:
					mTabHost.setCurrentTabByTag(DATA_TAB);
					break;
				case R.id.rd_2:
					mTabHost.setCurrentTabByTag(SEL_TAB);
					//VISIBLE:0  意思是可见的;INVISIBILITY:4 意思是不可见的，但还占着原来的空间;GONE:8  意思是不可见的，不占用原来的布局空间 
					//mMessageTipsArea.setVisibility(8);
					//service.save_area(3);
					break;
				case R.id.rd_3:
					mTabHost.setCurrentTabByTag(SYS_TAB);
					//mMessageTipsTotal.setVisibility(8);
					
					break;
				case R.id.rd_4:
					//service.save_area(2);
					MeterSettingActivity.this.finish();
					break;
				default:
					break;
			}
		}
	}
/*
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
			quitDialog();
		}
		return super.dispatchKeyEvent(event);
	}
*/
	/**
	 * 退出对话框
	 */
	/*
	private void quitDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.hello_world)
				.setIcon(null)
				.setCancelable(false)
				.setMessage(R.string.hello_world)
				.setPositiveButton(R.string.hello_world,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								MeterSettingActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.hello_world,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}
	*/
}
