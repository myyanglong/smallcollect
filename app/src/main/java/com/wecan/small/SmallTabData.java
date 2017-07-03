package com.wecan.small;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wecan.check.CheckMain;
import com.wecan.domain.PreferencesService;
import com.wecan.domain.WaterMeter;
import com.wecan.domain.WaterMeterAdapter;
import com.wecan.smallcollect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 小表采集/数据采集界面
 */
public class SmallTabData extends Activity implements OnClickListener,OnItemClickListener,OnItemLongClickListener{
	private Button onButton,offButton;
	
	private ListView listView;
    public	String rev_str;
    private PreferencesService service;
    
	public WaterMeterAdapter userAdapter = null;
	public WaterMeterAdapter missedAdapter = null;
	
	List<WaterMeter> userList = new ArrayList<WaterMeter>();
	List<WaterMeter> missedList = new ArrayList<WaterMeter>();
	private boolean bflag = true;
	
	private static final int ACTIVITY_RETURN_DATA = 3023;
    //private UartReceiver receiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_tab_data);
		
		findView();
		initdata();
		initview();
	}

	private void findView() {
		// TODO Auto-generated method stub
		onButton = (Button) findViewById(R.id.meter_data);
		offButton = (Button) findViewById(R.id.missed_data);
		listView = (ListView) this.findViewById(R.id.datalistView);
		
		onButton.setOnClickListener(this);
		offButton.setOnClickListener(this);
	}

	private void initview() {
		// TODO Auto-generated method stub
		onButton.setBackgroundResource(R.drawable.small_topbar_front1);
		onButton.setText(String.format(getResources().getString(R.string.data_meter),userAdapter.getCount()));
		
		offButton.setBackgroundResource(R.drawable.small_topbar_ater);
		offButton.setText(String.format(getResources().getString(R.string.data_missed),missedAdapter.getCount()));
		
		if(service.getUserType() == 1)
			listView.setOnItemLongClickListener(this);
		
	}
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		userList = (new ServiceSmall(this)).selectWaterMeterForDev(service.getActionId(),true);
		userAdapter = new WaterMeterAdapter(this,service.getActionId(), userList,false);
		onButton.setText(String.format(getResources().getString(R.string.data_meter),userList.size()));
		
		missedList = (new ServiceSmall(this)).selectWaterMeterForDev(service.getActionId(),false);
		missedAdapter = new WaterMeterAdapter(this,service.getActionId(), missedList,false);
		offButton.setText(String.format(getResources().getString(R.string.data_missed),missedList.size()));
	
		if(bflag)
			listView.setAdapter(userAdapter);
		else
			listView.setAdapter(missedAdapter);
	}
	private void initdata() {
		
		service = new PreferencesService(this);
		userList = (new ServiceSmall(this)).selectWaterMeterForDev(service.getActionId(),true);
		userAdapter = new WaterMeterAdapter(this,service.getActionId(), userList,false);
		userAdapter.adapter_sort();
		
		missedList = (new ServiceSmall(this)).selectWaterMeterForDev(service.getActionId(),false);
		missedAdapter = new WaterMeterAdapter(this,service.getActionId(), missedList,false);
		missedAdapter.adapter_sort();
		
		listView.setAdapter(userAdapter);
        //启动服务
        //startService(new Intent(SmallTabData.this, UartService.class));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.meter_data:
				bflag = true;
				listView.setAdapter(userAdapter);
				onButton.setBackgroundResource(R.drawable.small_topbar_front1);
				offButton.setBackgroundResource(R.drawable.small_topbar_ater);
				break;
			case R.id.missed_data:
				bflag = false;
				listView.setAdapter(missedAdapter);
				offButton.setBackgroundResource(R.drawable.small_topbar_ater1);
				onButton.setBackgroundResource(R.drawable.small_topbar_front);
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		if (resultCode != RESULT_OK){
			return;
		}
		switch (requestCode) {
			case ACTIVITY_RETURN_DATA:
				Toast.makeText(this,"添加记录成功", Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		WaterMeter wm =(WaterMeter) adapter.getAdapter().getItem(position);
		Intent intent = new Intent(this, CheckMain.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
    	Bundle bundle = new Bundle();
    	bundle.putString("id", wm.id);
    	intent.putExtras(bundle);
    	startActivityForResult(intent, ACTIVITY_RETURN_DATA);
		return false;
	}
}
