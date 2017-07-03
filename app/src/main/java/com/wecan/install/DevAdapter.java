package com.wecan.install;

import java.util.List;

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

public class DevAdapter extends BaseAdapter {

	private Context context = null;
	private List<Configs> datas = null;
	public  Bundle bundle = null;

	public DevAdapter(Context context, List<Configs> datas,Bundle bundle) {
		this.datas = datas;
		this.context = context;
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
		Configs cg = datas.get(position);
		/*
		 * 获得该item 是否允许删除
		 */
		if(cg.type == 0)
			tx_1.setText(context.getString(R.string.meter_j));
		else if(cg.type == 1)
			tx_1.setText(context.getString(R.string.meter_c));
		else
			tx_1.setText(context.getString(R.string.meter_z));
		
		tx_2.setText(String.format(context.getString(R.string.meter_id),cg.id));
		
		if(bundle.getString("id").equals(cg.f_id))
			m_cb.setChecked(true);
		else
			m_cb.setChecked(false);
		
		return convertView;
	}
	public void update(int position){
		Configs cg = datas.get(position);
		//wm.bflag = (wm.bflag == 0)?1:0;
		if(bundle.getString("id").equals(cg.f_id))
			cg.f_id = "0";
		else
			cg.f_id = bundle.getString("id");
		Log.i("debug", bundle.getString("id") + "," + cg.f_id);
		datas.set(position, cg);
	}
	
	public void remove(int position) {
		this.datas.remove(position);
	}
	
	
	public void CheckMeter(Configs cg){
		int i = 0;
		for (; i < datas.size(); i++) {
			if(datas.get(i).id.equals(cg.id)){
				datas.set(i, cg);
				//wms.update_data(wm,true);
				break;
			}
		}
		if( i == datas.size()){
			this.datas.add(0, cg);
			//wms.update_data(wm,false);
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
	public List<Configs> getDatas() {
		return datas;
	}
	public int getSelectNum(){
		int temp = 0;
		for (int i = 0; i < datas.size(); i++) {
			Configs cg = datas.get(i);
			if(bundle.getString("id").equals(cg.f_id))
				temp ++;
		}
		return temp;
	}
}
