package com.wecan.check;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wecan.smallcollect.R;
import com.wecan.domain.Configs;
import com.wecan.small.WaterAdapter.ViewHolder;
import com.wecan.small.WaterAdapter.reorder;

public class WaterErrorAdapter extends BaseAdapter {

	private Context context = null;
	private List<WaterError> datas = null;
	
	public WaterErrorAdapter(Context context, List<WaterError> datas) {
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
		TextView tx_1= null;
		TextView tx_2= null;
		TextView tx_3= null;
		TextView tx_4= null;
		
		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.listview_error, parent, false);
			
			tx_1 = (TextView) convertView.findViewById(R.id.data_address);
			tx_2 = (TextView) convertView.findViewById(R.id.data_id);
			tx_3 = (TextView) convertView.findViewById(R.id.data_time);
			tx_4 = (TextView) convertView.findViewById(R.id.data_dsc);
			
			convertView.setTag(new ViewHolder(tx_1,tx_2,tx_3,tx_4));
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			tx_1 = dataWrapper.tx_1;
			tx_2 = dataWrapper.tx_2;	
			tx_3 = dataWrapper.tx_3;
			tx_4 = dataWrapper.tx_4;
			
		}
		WaterError we = datas.get(position);
		
		tx_1.setText(we.unit +" " + we.address + " " + we.floor + "-" + we.door);
		tx_2.setText(we.id);
		tx_3.setText(String.format(context.getString(R.string.data_time),we.time));
		tx_4.setText(we.dsc);
		
		return convertView;
	}
	public final class ViewHolder{
		public TextView tx_1= null;
		public TextView tx_2= null;
		public TextView tx_3= null;
		public TextView tx_4 = null;
		
		public ViewHolder(TextView tx_1, TextView tx_2 ,TextView tx_3 ,TextView tx_4) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.tx_3 = tx_3;
			this.tx_4 = tx_4;
		}
	}
	public List<WaterError> getDatas() {
		return datas;
	}
}
