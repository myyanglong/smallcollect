package com.wecan.install;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
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
import com.wecan.install.MeterAdapter.ViewHolder;
import com.wecan.install.MeterAdapter.reorder;
import com.wecan.service.WaterMeterService;

public class ListViewAdapter extends BaseAdapter {

	private Context context = null;
	private List<InstallListView> datas = new ArrayList<InstallListView>();
	private Boolean bflag = false;
	public  String action_id = "nil";
	
	public ListViewAdapter(Context context, String action_id,List<WaterMeter> wms) {
		initListViewFromWM(wms);
		this.context = context;
		this.action_id = action_id;
	}
	public ListViewAdapter(Context context, List<Configs> cgs, String action_id) {
		initListViewFromCG(cgs);
		this.context = context;
	}
	public void initListViewFromWM(List<WaterMeter> wms){
		for(WaterMeter wm:wms){
			datas.add(new InstallListView(wm));
		}
	}
	public void initListViewFromCG(List<Configs> cgs){
		for(Configs cg:cgs){
			datas.add(new InstallListView(cg,0));
		}
	}
	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}
	public int getInitCount() {
		int num = 0;
		for (int i=0; i < datas.size(); i++) {
			if(datas.get(i).type == 0){
				num++;
			}
		}
		return num;
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
		TextView tx_3= null;
		TextView tx_4= null;
		CheckBox m_cb = null;
		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.s_setting_tab_list, parent, false);
			
			tx_1 = (TextView) convertView.findViewById(R.id.tx_1);
			tx_2 = (TextView) convertView.findViewById(R.id.tx_2);
			tx_3 = (TextView) convertView.findViewById(R.id.tx_3);
			tx_4 = (TextView) convertView.findViewById(R.id.tx_4);
			m_cb = (CheckBox) convertView.findViewById(R.id.m_cb);
			
			convertView.setTag(new ViewHolder(tx_1,tx_2,tx_3,tx_4,m_cb));
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			tx_1 = dataWrapper.tx_1;
			tx_2 = dataWrapper.tx_2;	
			tx_3 = dataWrapper.tx_3;
			tx_4 = dataWrapper.tx_4;
			m_cb = dataWrapper.m_cb;
			
		}
		InstallListView lv = datas.get(position);
		/*
		 * 获得该item 是否允许删除
		 */
		if(lv.bflag){
			switch(lv.action_type){
				case 0:
					tx_3.setText(context.getString(R.string.meter_j));
					break;
				case 1:
					tx_3.setText(context.getString(R.string.meter_c));
					//tx_3.setText(context.getString(R.string.meter_j));
					break;
				default:
					tx_3.setText(context.getString(R.string.meter_z));
					//tx_3.setText(context.getString(R.string.meter_c));
					break;
			}
			tx_1.setText(lv.address);
			tx_2.setText(String.format(context.getString(R.string.meter_id),lv.id));
		}
		else{
			tx_1.setText(lv.address);
			tx_2.setText(String.format(context.getString(R.string.meter_list_id),lv.id));
			tx_3.setText(lv.action_id);
//			if(action_id.equals(lv.action_id))
//				tx_3.setVisibility(4);
//			else
//				tx_3.setVisibility(0);
		}
		
		if(lv.flag){
			m_cb.setChecked(true);
		}
		else
			m_cb.setChecked(false);
		
		//type 0 空白 1 配置 2正常 3异常
		tx_4.setVisibility(0);
		switch(lv.type){
			case 0:
				tx_4.setVisibility(4);
				break;
			case 1:
				break;
			case 2:
				tx_4.setText("正常");
				break;
			default:
				tx_4.setText("异常");
				break;
		}
		return convertView;
	}
	public void update(int position){
		InstallListView lv = datas.get(position);
		lv.flag = !lv.flag;
		datas.set(position, lv);
	}
	public void updateTag(String id,int type){
		InstallListView il = null;
		for (int i=0; i < datas.size(); i++) {
			if(datas.get(i).id.equals(id)){
				il = datas.get(i);
				il.type = type;
				datas.set(i, il);
				break;
			}
		}
		
	}
	public void remove(int position) {
		this.datas.remove(position);
	}
	
	
	public InstallListView CheckMeter(String id){
		InstallListView il = null;
		for (int i=0; i < datas.size(); i++) {
			if(datas.get(i).id.equals(id)){
				il = datas.get(i);
				this.datas.remove(i);
				break;
			}
		}
		return il;
	}
	public boolean add(InstallListView il){
		if(il != null){
			datas.add(il);
			return true;
		}
		else
			return false;
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
	public InstallListView doubleCheckMeter(String id){
		InstallListView il = null;
		for (int i=0; i < datas.size(); i++) {
			if(datas.get(i).id.equals(id)){
				il = datas.get(i);
				this.datas.remove(i);
				break;
			}
		}
		return il;
	}
	public InstallListView doubleWriteMeter(String id,int type){
		InstallListView il = null;
		
		for (int i=0; i < datas.size(); i++) {
			if(datas.get(i).id.equals(id)){
				il = datas.get(i);
				il.type = type;
				datas.set(i, il);
				break;
			}
		}
		return il;
	}
	public List<InstallListView> doubleGetSelfWater(boolean flag){
		List<InstallListView> temp = new ArrayList<InstallListView>();
		if(flag){
			for (int i=0; i < datas.size(); i++) {
				if(datas.get(i).action_id.equals(action_id)){
					temp.add(datas.get(i));
				}
			}
		}
		else{
			for (int i=0; i < datas.size(); i++) {
				if(!(datas.get(i).action_id.equals(action_id))){
					temp.add(datas.get(i));
				}
			}
		}
		return temp;
	}
	public final class ViewHolder{
		public TextView tx_1= null;
		public TextView tx_2= null;
		public TextView tx_3= null;
		public TextView tx_4= null;
		public CheckBox m_cb = null;
		
		public ViewHolder(TextView tx_1, TextView tx_2,TextView tx_3, TextView tx_4,CheckBox m_cb) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.tx_3 = tx_3;
			this.tx_4 = tx_4;
			this.m_cb = m_cb;
		}
	}
	public List<InstallListView> getDatas() {
		return datas;
	}
}
