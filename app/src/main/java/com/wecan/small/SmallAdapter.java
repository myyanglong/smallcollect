package com.wecan.small;

import java.util.List;

import com.wecan.domain.SmallConfigs;
import com.wecan.smallcollect.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SmallAdapter extends BaseAdapter {
	public String action_id = null;
	private Context context = null;
	private List<SmallConfigs> datas = null;
	
	public SmallAdapter(Context context, String action_id, List<SmallConfigs> datas) {
		this.datas = datas;
		this.context = context;
		this.action_id = action_id;
	}
	

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tx_1= null;
		TextView tx_2= null;
		TextView tx_3= null;
		CheckBox m_cb = null;
		
		
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.small_tab_area_list, parent, false);
			
			tx_1 = (TextView) convertView.findViewById(R.id.data_address);
			tx_2 = (TextView) convertView.findViewById(R.id.data_id);
			tx_3 = (TextView) convertView.findViewById(R.id.data_type);
			
			
			m_cb = (CheckBox) convertView.findViewById(R.id.m_cb);
			
			convertView.setTag(new ViewHolder(tx_1,tx_2,tx_3,m_cb));
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			tx_1 = dataWrapper.tx_1;
			tx_2 = dataWrapper.tx_2;	
			tx_3 = dataWrapper.tx_3;
			m_cb = dataWrapper.m_cb;
			
		}
		
		SmallConfigs cg = datas.get(position);
		tx_1.setText(cg.address);
		tx_2.setText(cg.id);
		String tmp = "水表数：" + cg.meterCnt;
		tx_3.setText(tmp);
		
		
		if(cg.id.equals(action_id)){
			m_cb.setVisibility(0);
			m_cb.setChecked(true);
		}
		else{
			m_cb.setVisibility(4);
			m_cb.setChecked(false);
		}
		
		return convertView;
	}
	
	private final class ViewHolder {
		public TextView tx_1 = null;
		public TextView tx_2 = null;
		public TextView tx_3 = null;
		public CheckBox m_cb = null;
		
		public ViewHolder(TextView tx_1, TextView tx_2 ,TextView tx_3 ,CheckBox m_cb) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.tx_3 = tx_3;
			this.m_cb = m_cb;
		}
	}
}
