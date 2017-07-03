package com.wecan.install;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wecan.smallcollect.R;
import com.wecan.domain.Configs;
import com.wecan.domain.WaterMeter;
import com.wecan.service.WaterMeterService;

public class MeterAdapter extends BaseAdapter {

	private Context context = null;
	private List<WaterMeter> datas = null;
	private Boolean bflag = false;
	public  Bundle bundle = null;
	
	private static final int[] statusId ={R.string.data_str0,R.string.data_str1,R.string.data_str2,R.string.data_str5,
		R.string.data_str5,R.string.data_str5,R.string.data_str3};
	private static final int[] rfId ={R.string.data_str0,R.string.data_str1,R.string.data_str5,R.string.data_str5,
		R.string.data_str4,R.string.data_str5,R.string.data_str5};
	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public MeterAdapter(Context context, List<WaterMeter> datas,Bundle bundle,Boolean bflag) {
		this.datas = datas;
		this.context = context;
		this.bflag = bflag;
		this.bundle = bundle;
	}

	/**
	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	 */
	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView tx_1= null;
		TextView tx_2= null;
		CheckBox m_cb = null;
		ImageView img_id = null;
		
		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.s_setting_listview, parent, false);
			
			tx_1 = (TextView) convertView.findViewById(R.id.tx_1);
			tx_2 = (TextView) convertView.findViewById(R.id.tx_2);
			img_id = (ImageView) convertView.findViewById(R.id.img_id);
			m_cb = (CheckBox) convertView.findViewById(R.id.m_cb);
			
			convertView.setTag(new ViewHolder(tx_1,tx_2,m_cb,img_id));
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			tx_1 = dataWrapper.tx_1;
			tx_2 = dataWrapper.tx_2;	
			m_cb = dataWrapper.m_cb;
			img_id = dataWrapper.img_id;
			
		}
		WaterMeter watermeter = datas.get(position);
		/*
		 * 获得该item 是否允许删除
		 */
		tx_1.setText(watermeter.unit +" " + watermeter.address + " " + watermeter.floor + "-" + watermeter.door);
		tx_2.setText(String.format(context.getString(R.string.meter_list_id),watermeter.id));
		
		if(bundle.getString("id").equals(watermeter.action_id))
			m_cb.setChecked(true);
		else
			m_cb.setChecked(false);
		
		return convertView;
	}
	public void update(int position){
		WaterMeter wm = datas.get(position);
		if(bundle.getString("id").equals(wm.action_id))
			wm.action_id = "0";
		else
			wm.action_id = bundle.getString("id");
		datas.set(position, wm);
	}
	
//	public void updateId(int position, String id){
//		WaterMeter wm = datas.get(position);
//		wm.id = id;
//		datas.set(position, wm);
//	}
	
	public void configCheckMap(boolean bflag) {
		if(bflag){
			for (int i = 0; i < datas.size(); i++) {
				WaterMeter wm = datas.get(i);
				wm.action_id = bundle.getString("id");
				datas.set(i, wm);
			}
		}

	}
	public void configClearMap() {
		for (int i = 0; i < datas.size(); i++) {
			WaterMeter wm = datas.get(i);
			if(bundle.getString("id").equals(wm.action_id))
				wm.action_id = "0";
			datas.set(i, wm);
		}

	}
	public void remove(int position) {
		this.datas.remove(position);
	}
	
	
	public void CheckMeter(WaterMeter wm){
		int i = 0;
		for (; i < datas.size(); i++) {
			if(datas.get(i).id.equals(wm.id)){
				datas.set(i, wm);
				break;
			}
		}
		if( i == datas.size()){
			this.datas.add(0, wm);
		}
	}
	public void removeMeter(WaterMeter wm){
		int i = 0;
		for (; i < datas.size(); i++) {
			if(datas.get(i).id.equals(wm.id)){
				this.datas.remove(i);
				break;
			}
		}
	}
	public void setCheckMap(int i){
		isCheckMap.put(i,true);
		//datas.set(i, datas.get(0));
	}
	// 移除一个项目的时候
	

	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}
	public void adapter_sort(){
		Collections.sort(datas,new reorder());
	}
	public static final class reorder implements Comparator<WaterMeter>{

		@Override
		public int compare(WaterMeter arg0, WaterMeter arg1) {
			// TODO Auto-generated method stub
			if(Integer.parseInt(arg0.id) > Integer.parseInt(arg1.id))
				return 1;
			else
				return -1;
		}
		
	}
	public final class ViewHolder{
		public TextView tx_1= null;
		public TextView tx_2= null;
		public CheckBox m_cb = null;
		public ImageView img_id = null;
		
		public ViewHolder(TextView tx_1, TextView tx_2,CheckBox m_cb,ImageView img_id) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.m_cb = m_cb;
			this.img_id = img_id;
		}
	}
	public List<WaterMeter> getDatas() {
		return datas;
	}
	public int getSelectNum(){
		int temp = 0;
		for (int i = 0; i < datas.size(); i++) {
			WaterMeter wm = datas.get(i);
			if(bundle.getString("id").equals(wm.action_id))
				temp ++;
			
		}
		return temp;
	}
	
	public List<WaterMeter> delSelectWaters() {
		List<WaterMeter> wms = new ArrayList<WaterMeter>();
		for (int i = 0; i < datas.size(); i++) {
			WaterMeter wm = datas.get(i);
			if(bundle.getString("id").equals(wm.action_id)) {
				wms.add(wm);
			} 
		}
		
		for (int i = 0; i < datas.size(); i++) {
			WaterMeter wm = datas.get(i);
			if(bundle.getString("id").equals(wm.action_id)) {
				datas.remove(i);
			} 
		}
		
		return wms;
	}

}
