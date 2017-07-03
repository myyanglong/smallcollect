package com.wecan.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wecan.smallcollect.R;

public class ConfigAdapter extends BaseAdapter {

	private Context context = null;
	private List<Configs> cgs = new ArrayList<Configs>();
	
	public ConfigAdapter(Context context, List<Configs> cgs) {
		this.cgs = cgs;
		this.context = context;
	}
	@Override
	public int getCount() {
		return cgs == null ? 0 : cgs.size();
	}

	@Override
	public Object getItem(int position) {
		return cgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		TextView s_unit= null;
		TextView s_id= null;
		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.s_layout_list_item, parent, false);
			
			s_unit = (TextView) convertView.findViewById(R.id.s_unit);
			s_id = (TextView) convertView.findViewById(R.id.s_id);
			
			convertView.setTag(new ViewHolder(s_unit,s_id));
			//convertView.setTag(new ViewHolder(s_txview));
			
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			s_unit = dataWrapper.s_unit;
			s_id = dataWrapper.s_id;
		}

		Configs cg = cgs.get(position);
		s_unit.setText(cg.address);
		s_id.setText(cg.id);
			 
		return convertView;
	}
	
	// 移除一个项目的时候
	public void remove(int position) {
		this.cgs.remove(position);
	}
	public final class ViewHolder{
		public TextView s_unit = null;
		public TextView s_id = null;
		public ViewHolder(TextView s_unit,TextView s_id) {
			this.s_unit = s_unit;
			this.s_id = s_id;
		}
	}
	public List<Configs> getDatas() {
		return cgs;
	}

}
