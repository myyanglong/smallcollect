package com.wecan.install;

import java.util.ArrayList;
import java.util.List;

import com.wecan.smallcollect.R;
import com.wecan.domain.PreferencesService;
import com.wecan.domain.WaterMeter;
import com.wecan.service.WaterMeterService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class InstallSelectWater extends Activity implements OnClickListener,OnItemClickListener,OnItemLongClickListener {
	TextView tx,m_i_w;
	WaterMeterService wms;
	WaterMeter wm;
	private MeterAdapter meterAdapter = null;
	private Bundle bundle;
	private ListView meter;
	List<WaterMeter> meterList = new ArrayList<WaterMeter>();
	private WaterMeterService service;
	private ServiceInstall svinstall;
	
	private Spinner  spinner_area,spinner_build,spinner_floor;
	private Button btn_floor;
	private ArrayAdapter<String> adapterarea,adapterbuild,adapterfloor;

	private int type;
	private String str_area=null, str_build=null, str_floor=null;
	PreferencesService prservice =null;
	private	RelativeLayout s_back,s_add;
	
	private String[] floor_str;
	private ListView areaCheckListView;
	
	private CheckBox cb_all,cb_set;
	private Button btn_del;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_setting_select_water);
		
		findView();
		initData();
		initView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		meter = (ListView) this.findViewById(R.id.small_water_list);
		spinner_area = (Spinner) findViewById(R.id.spinner_area);
		spinner_build = (Spinner) findViewById(R.id.spinner_build);
		spinner_floor = (Spinner) findViewById(R.id.spinner_floor);
		s_back = (RelativeLayout) findViewById(R.id.s_back);
		s_add = (RelativeLayout) findViewById(R.id.s_add);
		
		cb_all = (CheckBox) findViewById(R.id.cb_all);
		cb_set = (CheckBox) findViewById(R.id.cb_set);
		btn_del = (Button) findViewById(R.id.btn_water_del);
		
		m_i_w = (TextView) findViewById(R.id.m_i_w);
		
		this.btn_floor = (Button) this.findViewById(R.id.btn_floor);
	}

	private void initView() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		bundle = intent.getExtras();
		
		s_back.setOnClickListener(this);
		s_add.setOnClickListener(this);
		
		btn_floor.setOnClickListener(this);

		meterAdapter = new MeterAdapter(this, meterList,bundle,false);
		meter.setAdapter(meterAdapter);
		meter.setOnItemClickListener(this);
		meter.setOnItemLongClickListener(this);
		
		cb_all.setOnClickListener(this);
		cb_set.setOnClickListener(this);
		btn_del.setOnClickListener(this);
		
		spinner_area.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					spinner_build.setClickable(false);
					type = 0;
				}
				else{
					spinner_build.setClickable(true);
					str_area = (String)parent.getAdapter().getItem(position);
					updatebuild(str_area);
					adapterarea.notifyDataSetChanged();
					type = 1;
				}
				spinner_floor.setClickable(false);
				btn_floor.setClickable(false);
				spinner_build.setSelection(0);
				spinner_floor.setSelection(0);
				updateListView(0);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			 
			}
			});
		
		spinner_build.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("onItemSelected:" + position);
				if(!(type == 0)){
					if(position == 0){
						spinner_floor.setClickable(false);
						btn_floor.setClickable(false);
						type = 1;
						updateListView(0);
					}
					else{
						spinner_floor.setClickable(true);
						btn_floor.setClickable(true);
						str_build = (String)parent.getAdapter().getItem(position);
						//Log.i("debug", str_build);
						updatefloor(str_build);
						adapterbuild.notifyDataSetChanged();
						type = 2;
						updateListView(0);
					}
//					spinner_floor.setSelection(0);
					
				}
			}
			
			public void onNothingSelected(AdapterView<?> parent) {
			 
			}
			});
		
		//spinner_build.setOnItemSelectedListener(this);
		
	}
	
	private void updatefloor(String str) {
		// TODO Auto-generated method stub
		adapterfloor = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListFloor(str_area, str));
		adapterfloor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_floor.setAdapter(adapterfloor);
	}
	public void updatebuild(String str){
		adapterbuild = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListBuild(str));
		adapterbuild.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_build.setAdapter(adapterbuild);
	}
	public void updateListView(int cmd){
		prservice.save_SmallList(type,str_area,str_build,str_floor);
		meterAdapter = new MeterAdapter(this,svinstall.selectWaterMeter(type,str_area,str_build,str_floor,bundle.getString("id"),cb_set.isChecked()),bundle,false);
		switch(cmd){
			case 0:
				break;
			case 1:
				meterAdapter.configCheckMap(true);
				break;
			case 2:
				meterAdapter.configClearMap();
				break;
		}
		m_i_w.setText(String.format(getString(R.string.m_i_w),meterAdapter.getSelectNum() + "/" + meterAdapter.getCount()));
		Log.i("debug", "updateListView "+ meterList.size());
//		meterAdapter.notifyDataSetInvalidated();
		meter.setAdapter(meterAdapter);
	}
	private void initData() {
		// TODO Auto-generated method stub
		service = new WaterMeterService(this);
		svinstall = new ServiceInstall(this);
		prservice = new PreferencesService(this);
		
		adapterarea = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListArea());
		adapterarea.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_area.setAdapter(adapterarea);
		
		adapterbuild = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListBuild(null));
		adapterbuild.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_build.setAdapter(adapterbuild);
		
		adapterfloor = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListFloor(null,null));
		adapterfloor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_floor.setAdapter(adapterfloor);
		
		//cgtemp = new Configs(bundle.getString("id"),bundle.getInt("type"),"","");
		Intent intent = getIntent();
		bundle = intent.getExtras();
	}
	public boolean[] areaState;
	public AlertDialog ad;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.s_back:
				svinstall.print_info();
				this.finish();
				break;
			case R.id.s_add:
				//Log.i("debug", "Meter:"+meter.getAdapter().getCount());
				svinstall.updateWaterMeters(meterAdapter.getDatas());
				Toast.makeText(this,"添加水表成功 +" + meterAdapter.getSelectNum(), Toast.LENGTH_LONG).show();
				this.finish();
				break;
			case R.id.btn_floor:
				floor_str = service.getListFloorString(str_area, str_build).split(",");
				areaState=new boolean[floor_str.length];
				 ad = new AlertDialog.Builder(InstallSelectWater.this)
				.setTitle("选择楼层")
				.setMultiChoiceItems(floor_str,areaState,new DialogInterface.OnMultiChoiceClickListener(){
				public void onClick(DialogInterface dialog,int whichButton, boolean isChecked){
				//点击某个区域 
					if(whichButton == 0){
						for (int i = 0; i < areaState.length; i++) {
							ad.getListView().setItemChecked(i, isChecked);
						}
					}
						//if(ad.getListView().getCheckedItemPositions().get(0))
							//ad.getListView().setItemChecked(0, isChecked);
							//((AlertDialog)(dialog.getClass())).getListView().setItemChecked(0, isChecked);
					
				}
				}).setPositiveButton("确定",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int whichButton){
						//svinstall.meter.clear();
						Log.i("debug", "Click");
						str_floor = null;
						for (int i = 1; i < floor_str.length; i++){
							if (areaCheckListView.getCheckedItemPositions().get(i)){
								if(str_floor == null)
									str_floor = (String) areaCheckListView.getAdapter().getItem(i);
								else
									str_floor = str_floor + "' or floor='" + (String) areaCheckListView.getAdapter().getItem(i);
							}else{
								areaCheckListView.getCheckedItemPositions().get(i,false);
							}
						}
						if(str_floor!=null){
							adapterfloor.notifyDataSetChanged();
							type = 3;
							updateListView(0);
						}
						dialog.dismiss();
					}
				}).setNegativeButton("取消", null).create();
				areaCheckListView = ad.getListView();
				ad.show();
				break;
			case R.id.cb_all:
				if(cb_all.isChecked())
					updateListView(1);
				else
					updateListView(2);
				break;
			case R.id.cb_set:
				updateListView(0);
				break;
			case R.id.btn_water_del:			
				final Context ctx = this;
				new AlertDialog.Builder(this)
				.setTitle("删除水表号")
				.setIcon(null)
				.setCancelable(false)
				.setMessage("删除后不可恢复，确定删除  ?")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								svinstall.deleteWaterMeters(meterAdapter.delSelectWaters());
								updateListView(0);
			            		Toast.makeText(ctx, "删除表号成功！"	, Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								dialog.dismiss();
							}
						}).create().show();
				
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View listView, int position, long id) {
		// TODO Auto-generated method stub
		if(adapter.getId() == R.id.small_water_list){
			meterAdapter.update(position);
			meterAdapter.notifyDataSetChanged();
			m_i_w.setText(String.format(getString(R.string.m_i_w),meterAdapter.getSelectNum() + "/" + meterAdapter.getCount()));
		}
	}
	
	
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int index,long id) {
		// TODO Auto-generated method stub
		final Context ctx = this;
		if(adapter.getId() != R.id.small_water_list) {
			return false;
		}
		
		final WaterMeter wm = (WaterMeter)adapter.getAdapter().getItem(index); 
		
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null); 
		
		final TextView tmpTextView = (TextView) textEntryView.findViewById(R.id.textView1); 
		String tmp = "原表号："+wm.unit+wm.address+wm.floor+"楼"+wm.door;
		tmpTextView.setText(tmp);
		
        final EditText editTextAddress = (EditText) textEntryView.findViewById(R.id.editTextName); 
        editTextAddress.setText(wm.id); 
        editTextAddress.setEnabled(false);
        
        final TextView tmpIdView = (TextView) textEntryView.findViewById(R.id.s_id); 
        tmpIdView.setText("新表号：");
		
        final EditText editTextNum = (EditText)textEntryView.findViewById(R.id.editTextNum);
        editTextNum.setKeyListener(new DigitsKeyListener(false,true));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更改表号").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	String oldId = wm.id;
            	wm.id = editTextNum.getText().toString();
            	if ((wm.id == null) || wm.id.length() != 10) {
            		Toast.makeText(ctx, "非法表号，更改失败！"	, Toast.LENGTH_LONG).show();
            	} else {
            		svinstall.updateWaterMeterId(oldId, wm.id);
        			meterAdapter.notifyDataSetChanged();
            		Toast.makeText(ctx, "更改表号成功！"	, Toast.LENGTH_LONG).show();
            	}
            		
            }
        });
        
        builder.show();
		return false;
	}
}