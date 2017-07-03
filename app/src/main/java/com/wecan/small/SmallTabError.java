package com.wecan.small;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.wecan.smallcollect.R;
import com.wecan.check.CheckMain;
import com.wecan.check.WaterError;
import com.wecan.check.WaterErrorAdapter;


public class SmallTabError extends Activity implements OnItemClickListener{
	private ListView list_we;
	private WaterErrorAdapter adapter;
	private ServiceSmall service;
	private static final int ACTIVITY_RETURN_DATA = 3023;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_tab_setting);
		
		initdata();
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		//chnl_gb.check(R.id.chnl_2);
	}
	private void initdata() {
		// TODO Auto-generated method stub
		service = new ServiceSmall(this);
		list_we = (ListView) this.findViewById(R.id.small_water_list);
		adapter = new WaterErrorAdapter(this,service.selectWaterError());
		list_we.setAdapter(adapter);
		list_we.setOnItemClickListener(this);
	}
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		Log.i("debug", "onPostResume");
		adapter = new WaterErrorAdapter(this,service.selectWaterError());
		list_we.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		// TODO Auto-generated method stub
		WaterError we =(WaterError) adapter.getAdapter().getItem(position);
		Intent intent = new Intent(this, CheckMain.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
    	Bundle bundle = new Bundle();
    	bundle.putString("id", we.id);
    	intent.putExtras(bundle);
    	startActivityForResult(intent, ACTIVITY_RETURN_DATA);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		if (resultCode != RESULT_OK){
			return;
		}
		switch (requestCode) {
			case ACTIVITY_RETURN_DATA:
				adapter = new WaterErrorAdapter(this,service.selectWaterError());
				adapter.notifyDataSetChanged();
				break;
		}
	}
}
