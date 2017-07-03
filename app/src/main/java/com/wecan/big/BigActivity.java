package com.wecan.big;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wecan.smallcollect.R;
import com.wecan.domain.DMAMeter;



public class BigActivity extends Activity implements OnClickListener{

	public float point_down,point_x,point_new,point_old= 0;
	
	double nLenStart = 0;
	public float nDown,nOld = 0;
	public boolean bfig = false;
	
	public int total = 0;
	public int num_start = 0,num_end = 0;
	
	private	RelativeLayout	pa_data,s_back;
	
	private TextView big_id, big_address, big_gtime, big_status, big_time, big_ptotal, big_ntotal, big_insflow,big_max, big_min,
						big_press, big_temp, big_bubbles;
			
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
		/*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  */
        
		setContentView(R.layout.big_main);
		
		findView();
		initView();
		initData();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		//big_id,big_address,big_time,big_status,big_total,big_min,big_max,big_unit,big_unit1,big_unit2;
		big_id = (TextView)findViewById(R.id.big_id);
		big_address = (TextView)findViewById(R.id.big_address);
		big_gtime = (TextView)findViewById(R.id.big_gtime);
		big_status = (TextView)findViewById(R.id.big_status);
		big_time = (TextView)findViewById(R.id.big_time);
		big_ptotal = (TextView)findViewById(R.id.big_ptotal);	
		big_ntotal = (TextView)findViewById(R.id.big_ntotal);
		big_insflow = (TextView)findViewById(R.id.big_insflow);
		
		big_min = (TextView)findViewById(R.id.big_min);
		big_max = (TextView)findViewById(R.id.big_max);
		big_press = (TextView)findViewById(R.id.big_press);
		big_temp = (TextView)findViewById(R.id.big_temp);
		big_bubbles = (TextView)findViewById(R.id.big_bubbles);

		
		pa_data= (RelativeLayout) findViewById(R.id.pa_data);
		s_back= (RelativeLayout) findViewById(R.id.s_back);
		
		//btn = (ToggleButton)findViewById(R.id.btn);
	}

	public void updateTextView(DMAMeter bm) {
		big_id.setText(bm.getID());
		big_address.setText(bm.getAddr());
		big_gtime.setText(bm.getGTime());
		String str;		
		int status = bm.getStaus() & 0x07;
		if (status == 0) {
			str = "正常";
		} else {
			str = "";
		}
		if ((status & 0x01) != 0) {
			str += "电量低";
		}
		if ((status & 0x02) != 0) {
			str += ",压力故障";
		}
		if ((status & 0x04) != 0) {
			str += ",空管";
		}
		big_status.setText(str);
		big_time.setText(bm.getTime());
		big_ptotal.setText(bm.getPTotal());
		big_ntotal.setText(bm.getNTotal());
		big_insflow.setText(Float.toString(bm.getInsFlow()));
		
		int h, m;
		h = (bm.getMaxTm() & 0xff00) >> 8;
		if (h > 23)
			h = 23;
		m = bm.getMaxTm() & 0xff;
		if (m > 59)
			m = 59;
		
		str = Float.toString(bm.getMax()) + "(" + Integer.toString(h) + ":" + Integer.toString(m) + ")";
		big_max.setText(str);
		
		h = (bm.getMinTm() & 0xff00) >> 8;
		if (h > 23)
			h = 23;
		m = bm.getMinTm() & 0xff;
		if (m > 59)
			m = 59;
		
		str = Float.toString(bm.getMin()) + "(" + Integer.toString(h) + ":" + Integer.toString(m) + ")";
		big_min.setText(str);
		
		big_press.setText(Integer.toString(bm.getPress()));
		big_temp.setText(Float.toString(bm.getTemp()));
		big_bubbles.setText(Integer.toString(bm.getBubbles()));
		
	}
	private void initView(){
		//得到TabHost
		DMAMeter bm = (DMAMeter)getIntent().getSerializableExtra("DMAMeter");

		updateTextView(bm);
		
		s_back.setOnClickListener(this);
		pa_data.setOnClickListener(this);
	}
	private void initData(){
		//给单选按钮设置监听
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.s_back:
				this.finish();
				break;
			default:
				break;
		}
		
	}


}
