package com.wecan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wecan.smallcollect.R;
import com.wecan.service.WaterMeterService;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class WaterMeterAdapter extends BaseAdapter {

	private Context context = null;
	private List<WaterMeter> datas = new ArrayList<WaterMeter>();
	private Boolean bflag = false;
	private String action_id;
	
	private static final int[] statusId ={R.string.data_str0,R.string.data_str1,R.string.data_str2,R.string.data_str5,
		R.string.data_str5,R.string.data_str5,R.string.data_str3};
	private static final int[] rfId ={R.string.data_str0,R.string.data_str1,R.string.data_str5,R.string.data_str5,
		R.string.data_str4,R.string.data_str5,R.string.data_str5};
	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	private Spannable superSpan = new SpannableString("m3");

	public WaterMeterAdapter(Context context, String action_id,List<WaterMeter> datas,Boolean bflag) {
		this.datas = datas;
		this.context = context;
		this.bflag = bflag;
		this.action_id = action_id;
		superSpan.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		if(bflag)
			configCheckMap(false);// 初始化,默认都没有选中
	}
	public void configCheckMap(boolean bool) {

		for (int i = 0; i < datas.size(); i++) {
			isCheckMap.put(i, bool);
		}

	}
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

		TextView data_address= null;
		TextView data_id= null;
		TextView data_time= null;
		TextView data_total= null;
		TextView data_status= null;
		TextView data_type= null;
		TextView data_unit= null;
		TextView data_rf= null;
		
		TextView tb_info= null;
		TextView tb_id= null;
		CheckBox cbCheck = null;
		
		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			if(bflag){
				convertView = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.listview_item_layout, parent, false);
				
				tb_info = (TextView) convertView.findViewById(R.id.tb_info);
				tb_id = (TextView) convertView.findViewById(R.id.tb_id);
				cbCheck = (CheckBox) convertView.findViewById(R.id.tb_action);
				
				convertView.setTag(new ViewHolder(tb_info,tb_id,cbCheck));
			}
			else{
				convertView = (ViewGroup) LayoutInflater.from(context).inflate(
						R.layout.listview_data, parent, false);
				data_address = (TextView) convertView.findViewById(R.id.data_address);
				data_id = (TextView) convertView.findViewById(R.id.data_id);
				data_time = (TextView) convertView.findViewById(R.id.data_time);
				data_total = (TextView) convertView.findViewById(R.id.data_total);
				data_status = (TextView) convertView.findViewById(R.id.data_status);
				data_type = (TextView) convertView.findViewById(R.id.data_type);
				data_unit = (TextView) convertView.findViewById(R.id.data_unit);
				data_rf = (TextView) convertView.findViewById(R.id.data_rf);
				convertView.setTag(new ViewHolder(data_address,data_id, data_time, data_total, data_status, data_type,data_unit,data_rf));
				
			}
			
		} else {
			if(bflag){
				ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
				tb_info = dataWrapper.tb_info;
				tb_id = dataWrapper.tb_id;	
				cbCheck = dataWrapper.cbCheck;
			}
			else{
				ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
				data_address = dataWrapper.data_address;
				data_id = dataWrapper.data_id;
				data_time = dataWrapper.data_time;	
				data_status = dataWrapper.data_status;
				data_type = dataWrapper.data_type;	
				data_total = dataWrapper.data_total;
				data_unit = dataWrapper.data_unit;
				data_rf = dataWrapper.data_rf;
			}
		}

		WaterMeter watermeter = datas.get(position);

		/*
		 * 获得该item 是否允许删除
		 */
		if(bflag){
			tb_info.setText(watermeter.unit +" "+ watermeter.address);
			tb_id.setText(watermeter.id);
			if (isCheckMap.get(position) == null) {
				isCheckMap.put(position, false);
			}
			cbCheck.setChecked(isCheckMap.get(position));
		}
		else{
			data_address.setText(watermeter.unit +" " + watermeter.address + " " + watermeter.floor + "-" + watermeter.door);
			data_id.setText(watermeter.id);
			data_time.setText(String.format(context.getString(R.string.data_time),watermeter.time));
			data_total.setText(watermeter.total);
			//if(watermeter.status > 7 || watermeter.status < 0)
				//watermeter.status = 0;
			
			String status_str = null,temp_tap=null;
			
			if(watermeter.status == 0){
				data_status.setText("工作正常");
				data_status.setTextColor(0xff484444);
			}
			else{
				if((watermeter.status&0x01) != 0)
					status_str = "测量电路电量低";
				if((watermeter.status&0x02) != 0){
					if(status_str != null)
						status_str = status_str + "/已超过Q4";
					else
						status_str = "已超过Q4";
				}
				if((watermeter.status&0x04) != 0){
					if(status_str != null)
						status_str = status_str + "/无线模块电量低";
					else
						status_str = "无线模块电量低";
				}
				if((watermeter.status&0x08) != 0){
					if(status_str != null)
						status_str = status_str + "/水表安装反向";
					else
						status_str = "水表安装反向";
				}
				if((watermeter.status&0x80) != 0){
					if(status_str != null)
						status_str = status_str + "/数据未更新";
					else
						status_str = "数据未更新";
				}
				Log.i("debug", "watermeter.status "+watermeter.status);
				if(status_str == null)
					status_str = "未知报错";
				data_status.setText(status_str);
				data_status.setTextColor(0xffff0000);
			}
			if(action_id.equals(watermeter.action_id))
				data_type.setVisibility(4);
			else
				data_type.setVisibility(0);
			data_unit.setText(superSpan);
			
			if(watermeter.rf > 255 || watermeter.rf < 0)
				watermeter.rf = 0;
			if (watermeter.rf >= 10) 
				data_rf.setText("报错");
			else
				data_rf.setText(String.format(context.getString(R.string.data_rf),watermeter.rf + " L/h"));
			
		}
		return convertView;
	}
	public void CheckMeter(WaterMeter wm,WaterMeter newwm){
		int i = 0;
		WaterMeterService wms;
		wms = new WaterMeterService(context);
		
		for (; i < datas.size(); i++) {
			if(datas.get(i).id.equals(wm.id)){
				wm.unit = datas.get(i).unit;
				wm.address = datas.get(i).address;
				wm.floor = datas.get(i).floor;
				wm.door = datas.get(i).door;
				datas.set(i, wm);
				wms.update_data(wm);
				break;
			}
		}
		if(newwm !=null){
			if( i == datas.size()){
				newwm.read = 1;
				this.datas.add(0, newwm);
				wms.update_data(newwm);
			}
		}
			
	}
	public WaterMeter CheckMissedMeter(WaterMeter wm){
		
		for (int i = 0; i < datas.size(); i++) {
			if(datas.get(i).id.equals(wm.id)){
				WaterMeter temp = datas.get(i);
				temp.time = wm.time;
				temp.total = wm.total;
				temp.status = wm.status;
				temp.rf = wm.rf ;
				return temp;
			}
		}
		return null;
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
	public void add(WaterMeter watermeter) {
		this.datas.add(0, watermeter);

		// 让所有项目都为不选择
		if(bflag)
			configCheckMap(false);
	}
	public void setCheckMap(int i){
		isCheckMap.put(i,true);
		//datas.set(i, datas.get(0));
	}
	// 移除一个项目的时候
	public void remove(int position) {
		this.datas.remove(position);
	}

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
		public TextView tb_info = null;
		public TextView tb_id = null;
		public CheckBox cbCheck = null;
		
		public TextView data_address ;
		public TextView data_id ;
		public TextView data_time;
		public TextView data_total;
		public TextView data_status;
		public TextView data_type;
		public TextView data_unit;
		public TextView data_rf;
		 
		public ViewHolder(TextView tb_info, TextView tb_id,CheckBox cbCheck) {
			this.tb_info = tb_info;
			this.tb_id = tb_id;
			this.cbCheck = cbCheck;
		}
		
		public ViewHolder(TextView data_address,TextView data_id, TextView data_time, TextView data_total, TextView data_status, TextView data_type ,TextView data_unit,TextView data_rf) {
			this.data_address = data_address;
			this.data_id = data_id;
			this.data_time = data_time;
			this.data_total = data_total;
			this.data_status = data_status;
			this.data_type = data_type;
			this.data_unit = data_unit;
			this.data_rf = data_rf;
		}
	}
	public List<WaterMeter> getDatas() {
		return datas;
	}

}
