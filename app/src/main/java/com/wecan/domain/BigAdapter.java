package com.wecan.domain;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.wecan.smallcollect.R;

import com.wecan.service.WaterMeterService;

public class BigAdapter extends BaseAdapter {

	private Context context = null;
	private List<DMAMeter> datas = new ArrayList<DMAMeter>();
	
	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */

	public BigAdapter(Context context, List<DMAMeter> datas) {
		this.datas = datas;
		this.context = context;
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
		TextView data_gtime= null;
		TextView data_time= null;
		TextView data_total= null;
		TextView data_status= null;
		TextView data_unit = null;


		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.big_list, parent, false);
			data_address = (TextView) convertView.findViewById(R.id.data_address);
			data_id = (TextView) convertView.findViewById(R.id.data_id);
			
			data_status = (TextView) convertView.findViewById(R.id.data_status);		
			data_gtime = (TextView) convertView.findViewById(R.id.data_gtime);
			
			data_total = (TextView) convertView.findViewById(R.id.data_total);
		
			data_time = (TextView) convertView.findViewById(R.id.data_time);
			data_unit = (TextView) convertView.findViewById(R.id.data_unit);
	

			
			convertView.setTag(new ViewHolder(data_address, data_id, data_status, data_gtime, data_total, data_time, data_unit));
			
			
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			data_address = dataWrapper.data_address;
			data_id = dataWrapper.data_id;
			
			data_status = dataWrapper.data_status;
			data_gtime = dataWrapper.data_gtime;
			
			data_total = dataWrapper.data_total;
			data_time = dataWrapper.data_time;
			
			data_unit = dataWrapper.data_unit;

		}

		DMAMeter bm = datas.get(position);

		/*
		 * 获得该item 是否允许删除
		 */
		data_address.setText(bm.getAddr());
		data_id.setText(bm.getID());
		String temps;
		if ((bm.getStaus() & 0x07) == 0 )
			temps = "工作正常";
		else
			temps = "工作异常";
		data_status.setText(temps);
		data_gtime.setText(bm.getGTime());
		
		data_total.setText(bm.getPTotal());
		data_time.setText(bm.getTime());
		
		Spannable superSpan = new SpannableString("m3");
		superSpan.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		data_unit.setText(superSpan);
		
		return convertView;
	}
	public final class ViewHolder{
		TextView data_address= null;
		TextView data_id= null;
		TextView data_gtime= null;
		TextView data_time= null;
		TextView data_total= null;
		TextView data_status= null;
		TextView data_unit = null;
		
		
		public ViewHolder(TextView data_address, TextView data_id, TextView data_status, TextView data_gtime, TextView data_total, TextView data_time ,
				TextView data_unit) {
			this.data_address = data_address;			
			this.data_id = data_id;
			
			this.data_status = data_status;
			this.data_gtime = data_gtime;
			
			this.data_total = data_total;
			this.data_time = data_time;
			
			this.data_unit = data_unit;
		}
	}
	public List<DMAMeter> getDatas() {
		return datas;
	}
	
	public DMAMeter CheckMissedBigMeter(DMAMeter bm){
		
		for (int i = 0; i < datas.size(); i++) {
			if(datas.get(i).getID().equals(bm.getID())){
				bm.setAddr(datas.get(i).getAddr());
				this.datas.remove(i);
				return bm;
			}
		}
		return null;
	}
	public void CheckBigMeter(DMAMeter bm, DMAMeter newbm){
		int i = 0;
		WaterMeterService wms = new WaterMeterService(context);
		
		for (; i < datas.size(); i++) {
			if(datas.get(i).getID().equals(bm.getID())){
				bm.setAddr(datas.get(i).getAddr());
				datas.set(i, bm);
				wms.updateBigMeter(bm);
				break;
			}
		}
		if(newbm != null){
			if( i == datas.size()){
				this.datas.add(newbm);
				wms.updateBigMeter(newbm);
			}
		}
			
	}
	
	public DMAMeter delSelectBigWater(String id) {
		DMAMeter bm = null;
		
		for (int i = 0; i < datas.size(); i++) {
			bm = datas.get(i);
			if(id.equals(bm.getID())) {
				datas.remove(i);
				break;
			} 
		}
		
		return bm;
	}
}

