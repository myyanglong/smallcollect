package com.wecan.install;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.wecan.smallcollect.R;

public class InstallSelectMeter extends Activity implements OnClickListener,OnItemClickListener{
	
	private DevAdapter devAdapter = null;
	private Bundle bundle;
	
	//List<Configs> cgList = new ArrayList<Configs>();
	private ServiceInstall svinstall;
	
	private	RelativeLayout s_back,s_add;
	private ListView meter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_setting_select_meter);
		
		findView();
		initData();
		initView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		meter = (ListView) this.findViewById(R.id.small_meter_list);
		s_back = (RelativeLayout) findViewById(R.id.s_back);
		s_add = (RelativeLayout) findViewById(R.id.s_add);
	}

	private void initView() {
		// TODO Auto-generated method stub
		s_back.setOnClickListener(this);
		s_add.setOnClickListener(this);
		devAdapter = new DevAdapter(this, svinstall.selectMeterForList(bundle.getInt("type")),bundle);
		meter.setAdapter(devAdapter);
		meter.setOnItemClickListener(this);
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		svinstall = new ServiceInstall(this);
		Intent intent = getIntent();
		bundle = intent.getExtras();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.s_back:
				//svinstall.print_info();
				this.finish();
				break;
			case R.id.s_add:
				//Log.i("debug", "Meter:"+meter.getAdapter().getCount());
				svinstall.updateWaterMetersList(devAdapter.getDatas());
				Toast.makeText(this,"成功设置 +" + devAdapter.getSelectNum(), Toast.LENGTH_LONG).show();
				this.finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View listView, int position, long id) {
		// TODO Auto-generated method stub
		if(adapter.getId() == R.id.small_meter_list){
			devAdapter.update(position);
			devAdapter.notifyDataSetChanged();
		}
	}

}
