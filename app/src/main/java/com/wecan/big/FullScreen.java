package com.wecan.big;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.wecan.smallcollect.R;

public class FullScreen extends Activity{
	//定义Tab选项卡标示符
	private HomeDiagram padata;
	//定义单选按钮对象
	
	private float width;
	
	public float point_down,point_x,point_new,point_old= 0;
	
	double nLenStart = 0;
	public float nDown,nOld = 0;
	public boolean bfig = false;
	
	public int total = 0;
	private final static int MinNum = 25 ;
	public int num_start = 0,num_end = 0;
	
	private	RelativeLayout	pa_data;
	
	List<Integer> data_list = new ArrayList<Integer>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		setContentView(R.layout.big_fullscreen);

		//initView();
		initData();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.finish();
	}

	/**
	 * 初始化组件
	 */
	private void initView(){
		//得到TabHost
		
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		//给单选按钮设置监听
		width = FullScreen.this.getWindowManager().getDefaultDisplay().getWidth();
		
		Intent intent = getIntent();
		Bundle bundle=intent.getExtras();
		/*
	    if(bm.id.equals(bundle.getString("id"))){
	    	bm.time = bundle.getString("time");
		    bm.total = bundle.getString("total");
		    bm.status = bundle.getInt("status");
		    bm.max = bundle.getString("max");
		    bm.min = bundle.getString("min");
		    bm.gtime = bundle.getString("gtime");
		    if(bundle.getInt("command") == 3)
		    	bm.value = bundle.getString("value");
		    updateTextView();
	    }
	    bundle.putIntegerArrayList("value", data_list);*/
	    data_list = bundle.getIntegerArrayList("value");
		total = data_list.size();
		num_end = total;
		padata = new HomeDiagram(this,data_list,0,total);
		
		pa_data= (RelativeLayout) findViewById(R.id.b_show);
		pa_data.addView(padata);
		pa_data.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v,MotionEvent event){
				float xlen,ylen;
				if(3 > event.getPointerCount())
				{
					switch(getMotionEvent(event)){
					case MotionEvent.ACTION_POINTER_DOWN:
						if(2 == event.getPointerCount()){
							xlen = Math.abs(event.getX(0) - event.getX(1));  
				            ylen = Math.abs(event.getY(0) - event.getY(1));  
			            
				            nDown =(float) Math.sqrt(xlen*xlen + ylen * ylen);
				            nOld = nDown;
				            bfig = true;
				            System.out.println("ACTION_POINTER_DOWN");
			            }
						else{
							point_x = event.getX(0);
						}
						padata.im = event.getX(0);
						break;
					case MotionEvent.ACTION_POINTER_UP:
						if(2 == event.getPointerCount()){
							
							System.out.println( " ACTION_FIG_UP ");
						}
						else{
							bfig = false;
							System.out.println( " ACTION_POINTER_UP ");
						}
						
						//padata.invalidate();
						break;
					default:
						if(2 == event.getPointerCount()){
							xlen = Math.abs(event.getX(0) - event.getX(1));  
				            ylen = Math.abs(event.getY(0) - event.getY(1));   
				            float num = (float) Math.sqrt(xlen*xlen + ylen * ylen); 
				            
				            if(nOld != num){
				            	float index = (float) Math.abs(nOld - num)/width * total;
				            	if((num > nOld)&&(num_end - num_start - index > MinNum - 1)){
			            		
			            			num_start += index/2;
			            			num_end -= index/2;
			            			uppadata(num_start,num_end);
			            			System.out.println("Big:" +num_start+ " "+ num_end + " index:" +index);
			            		}
			            		else if((num < nOld)&&(num_end - num_start + index < total + 1)){
			            			num_start -=index/2;
			            			num_end += index/2;
			            			if(num_start < 1)
			            				num_start = 0;
			            			if(num_end > total)
			            				num_end = total;
			            			uppadata(num_start,num_end);
			            			System.out.println("Small:" +num_start+ " "+ num_end+ " index:" +index);
			            		}
				            	nOld = num;
				            }
						}
						else if(!bfig){
							float num = (int)((Math.abs(point_x - event.getX(0))/width *(num_end - num_start)));
							System.out.println("Move:" +num_start+ " "+ num_end + " num:" +(event.getAction()&MotionEvent.ACTION_MASK));
							if(point_x > event.getX(0))
							{
								if(num_end + num < total)
									num_end +=num ;
								else
									num_end = total;
								
								if(num_start + num < total - MinNum)
									num_start +=num ;
								else
									num_start = total - MinNum;
							}
							else{
								if(num_end - num > MinNum)
									num_end -=num ;
								else
									num_end = MinNum;
								
								if(num_start - num > 0)
									num_start -=num ;
								else
									num_start = 0;
							}
							point_x = event.getX(0);
							Log.i("debug", num_start + "," + num_end);
							uppadata(num_start,num_end);
				                
						}
						padata.im = event.getX(0);
						break;
					}
				}
				//Toast.makeText(getApplicationContext(), "Fig:"+event.getPointerCount() + " Action:"+event.getAction(), 3000).show(); 
				//System.out.println("Fig:"+event.getPointerCount() + " Action:"+event.getAction());
				return true;
			}
		});
	}
	public int getMotionEvent(MotionEvent event){
		//System.out.println("POINTER "+ (event.getAction()&MotionEvent.ACTION_MASK) +" " + (event.getAction() + MotionEvent.ACTION_MASK) );
		if(2 == event.getPointerCount())
			return event.getAction()&MotionEvent.ACTION_MASK;
		else
			return (event.getAction()&MotionEvent.ACTION_MASK) + 5;
	}
	public void uppadata(int start,int end){
		pa_data.removeAllViews();
		padata = new HomeDiagram(this,data_list.subList(start, end),start,total);
		
		pa_data.addView(padata);
		//System.out.println("uppadata :" + start+" "+end);
	}
}
