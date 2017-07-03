package com.wecan.big;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.domain.BigAdapter;
import com.wecan.domain.DMAMeter;
import com.wecan.domain.WaterMeter;
import com.wecan.install.InstallListView;

import com.wecan.service.RfServ;
import com.wecan.service.WaterMeterService;

public class BigMain extends Activity implements OnClickListener,OnItemClickListener, OnItemLongClickListener {
	//定义Tab选项卡标示符
	private	RelativeLayout	s_back,s_add;
	private Button meter_data,missed_data;
	private BigAdapter bigAdapter,bigmissAdapter;
	private ListView listView;
	List<DMAMeter> tempList = new ArrayList<DMAMeter>();
	private boolean flag = true;
	
	private	UartReceiver receiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_entry);
		
		findView();
		initData();
		initView();
		
		receiver = new UartReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.weian.service.BROAD_CAST");
		registerReceiver(receiver, filter);
		
		RfServ.startCollectBig(this);
	}
	private void findView() {
		// TODO Auto-generated method stub
		s_back = ((RelativeLayout) findViewById(R.id.s_back));
		s_add = ((RelativeLayout) findViewById(R.id.s_add));
		meter_data = ((Button) findViewById(R.id.meter_data));
		missed_data = ((Button) findViewById(R.id.missed_data));
		listView = (ListView) this.findViewById(R.id.big_list);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		//给单选按钮设置监听
		bigAdapter = new BigAdapter(this, new ArrayList<DMAMeter>());
		bigmissAdapter = new BigAdapter(this, new WaterMeterService(this).getListBigMeter());
	}
	
	/**
	 * 初始化组件
	 */
	private void initView(){
		//得到TabHost
		meter_data.setBackgroundResource(R.drawable.small_topbar_front1);
		missed_data.setBackgroundResource(R.drawable.small_topbar_ater);
		meter_data.setText(String.format(getResources().getString(R.string.big_get),bigAdapter.getCount()));
		missed_data.setText(String.format(getResources().getString(R.string.big_miss),bigmissAdapter.getCount()));
		
		s_back.setOnClickListener(this);
		s_add.setOnClickListener(this);
		meter_data.setOnClickListener(this);
		missed_data.setOnClickListener(this);
		listView.setAdapter(bigAdapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.s_back:
				this.finish();
				break;
			case R.id.s_add:
		    	startActivity(new Intent(this, BigMenu.class));
				break;
			case R.id.meter_data:
				flag = true;
				listView.setAdapter(bigAdapter);
				meter_data.setBackgroundResource(R.drawable.small_topbar_front1);
				missed_data.setBackgroundResource(R.drawable.small_topbar_ater);
				break;
			case R.id.missed_data:
				flag = false;
				listView.setAdapter(bigmissAdapter);
				meter_data.setBackgroundResource(R.drawable.small_topbar_front);
				missed_data.setBackgroundResource(R.drawable.small_topbar_ater1);
				break;
			default:
				break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(listView.getId() == R.id.big_list){
			DMAMeter bm =(DMAMeter) adapter.getAdapter().getItem(position);
			Intent intent = new Intent(this, BigActivity.class);
			intent.putExtra("DMAMeter", bm);
			startActivity(intent);
		}
	}
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
	}
	protected void onStart() {
        super.onStart();
	}
	
	protected void onResume() {
		bigAdapter = new BigAdapter(this, new ArrayList<DMAMeter>());
		bigmissAdapter = new BigAdapter(this, new WaterMeterService(this).getListBigMeter());
		bigAdapter.notifyDataSetChanged();
		bigmissAdapter.notifyDataSetChanged();
		
		meter_data.setText(String.format(getResources().getString(R.string.big_get),bigAdapter.getCount()));
		missed_data.setText(String.format(getResources().getString(R.string.big_miss),bigmissAdapter.getCount()));
		if(flag)
			listView.setAdapter(bigAdapter);
		else
			listView.setAdapter(bigmissAdapter);
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
	}	
	
	protected void onStop() {
		super.onStop();
	}

	
    public void display(String str){
		Toast.makeText(this,str, Toast.LENGTH_LONG).show();
	}	
	
    public class UartReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		    DMAMeter bm = (DMAMeter)intent.getSerializableExtra("DMAMeter");
		    
		    bigAdapter.CheckBigMeter(bm, bigmissAdapter.CheckMissedBigMeter(bm));
		    listView.setVisibility(View.GONE);
			bigAdapter.notifyDataSetChanged();
			listView.setVisibility(View.VISIBLE);
			meter_data.setText(String.format(getResources().getString(R.string.big_get), bigAdapter.getCount()));
			missed_data.setText(String.format(getResources().getString(R.string.big_miss),bigmissAdapter.getCount()));
		}
	}
    
    @Override
	protected void onDestroy()
	{
    	RfServ.stopRfModule();
    	
		if(receiver != null) {
			try {
			    unregisterReceiver(receiver);
			} catch (IllegalArgumentException e) {			   
			}
		}
		
		super.onDestroy();
	}
    
    @Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int index,long id) {
		// TODO Auto-generated method stub
    	final Context ctx = this;
    	final BigAdapter adapt = (BigAdapter)adapter.getAdapter();
    	
    	final DMAMeter bm = (DMAMeter)adapt.getItem(index); 
    	
    	new AlertDialog.Builder(ctx)
		.setTitle("删除水表号")
		.setIcon(null)
		.setCancelable(false)
		.setMessage("删除后不可恢复，确定删除  ?")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						adapt.delSelectBigWater(bm.getID());
						new WaterMeterService(ctx).del_bigwater(bm);
						bigAdapter.notifyDataSetChanged();
						bigmissAdapter.notifyDataSetChanged();
						
						meter_data.setText(String.format(getResources().getString(R.string.big_get),bigAdapter.getCount()));
						missed_data.setText(String.format(getResources().getString(R.string.big_miss),bigmissAdapter.getCount()));
						if(flag)
							listView.setAdapter(bigAdapter);
						else
							listView.setAdapter(bigmissAdapter);
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						dialog.dismiss();
					}
				}).create().show();
		
		return true;
	}
    
}
