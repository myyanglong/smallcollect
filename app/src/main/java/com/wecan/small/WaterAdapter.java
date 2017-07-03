package com.wecan.small;

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


public class WaterAdapter extends BaseAdapter {

	private Context context = null;
	private List<Configs> datas = null;
	public String action_id;
	
	public WaterAdapter(Context context,String action_id, List<Configs> datas) {
		this.datas = datas;
		this.context = context;
		this.action_id = action_id;
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
		TextView tx_3= null;
		CheckBox m_cb = null;
		
		/**
		 * 进行ListView 的优化
		 */
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
		Configs cg = datas.get(position);
		/*
		 * 获得该item 是否允许删除
		 */
		tx_1.setText(cg.address);
		tx_2.setText(cg.id);
		switch(cg.type){
			case 0:
				tx_3.setText("集中器");
				break;
			case 1:
				tx_3.setText("采集器");
				break;
		}
		
		if(cg.id.equals(action_id)){
			m_cb.setVisibility(0);
			m_cb.setChecked(true);
		}
		else{
			m_cb.setVisibility(4);
			m_cb.setChecked(false);
		}
		//if(position == 1)
			//img_id.setImageResource(R.drawable.del_icon_normal);
		
		return convertView;
	}
	
	
	public void adapter_sort(){
		Collections.sort(datas,new reorder());
	}
	public static final class reorder implements Comparator<Configs>{

		@Override
		public int compare(Configs arg0, Configs arg1) {
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
		public TextView tx_3= null;
		public CheckBox m_cb = null;
		
		public ViewHolder(TextView tx_1, TextView tx_2 ,TextView tx_3 ,CheckBox m_cb) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.tx_3 = tx_3;
			this.m_cb = m_cb;
		}
	}
	public List<Configs> getDatas() {
		return datas;
	}

}

