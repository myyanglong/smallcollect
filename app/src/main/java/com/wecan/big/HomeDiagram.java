package com.wecan.big;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wecan.smallcollect.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Shader;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class HomeDiagram extends View {

	private List<Integer> milliliter;
	private float tb;
	private float pading_x = 10.0f,pading_y = 2.0f;
	
	
	private Paint paint_date, paint_maxmin,paint_brokenLine, paint_dottedline,
			paint_brokenline_big, framPanint;
	
	private int nMax,nMin;

	private int time_index;
	private Bitmap bitmap_point;
	private Path point_path;
	private float dotted_text;
	private int screen_fix = 17;
	private int start,total = 0;

	public float getDotted_text() {
		return dotted_text;
	}

	public void setDotted_text(float dotted_text) {
		this.dotted_text = dotted_text;
	}

	private int fineLineColor = 0xffffffff; //线条颜色
	private int blueLineColor = 0xff00ffff; //
	private int orangeLineColor = 0xfff1f915; //

	public HomeDiagram(Context context, List<Integer> milliliter,int start,int total) {
		super(context);
		this.total = total;
		this.start = start;
		init(milliliter);
	}

	public void init(List<Integer> milliliter) {
		if (null == milliliter || milliliter.size() == 0)
			return;
		this.milliliter = milliliter;
		setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		
	}
	public List<Integer> delZero(List<Integer> milliliter) {
		List<Integer> list = new ArrayList<Integer>();
		int sta = 0;
		int end = 0;
		for (int i = 0; i < milliliter.size(); i++) {
			if (milliliter.get(i) != 0) {
				sta = i;
				break;
			}
		}
		for (int i = milliliter.size() - 1; i >= 0; i--) {
			if (milliliter.get(i) != 0) {
				end = i;
				break;
			}
		}
		for (int i = 0; i < milliliter.size(); i++) {
			if (i >= sta && i <= end) {
				list.add(milliliter.get(i));
			}
		}
		time_index = sta;
		dotted_text = ((Collections.max(milliliter) - Collections
				.min(milliliter)) / 12.0f * 5.0f);
		return list;
	}
	private void initData() {
		// TODO Auto-generated method stub
		tb = 7.5f;
		paint_maxmin = new Paint();
		paint_maxmin.setStrokeWidth(tb * 0.1f);
		paint_maxmin.setTextSize(tb * 1.8f);
		paint_maxmin.setColor(orangeLineColor);
		
		paint_date = new Paint();
		paint_date.setStrokeWidth(tb * 0.1f);
		paint_date.setTextSize(tb * 1.8f);
		paint_date.setColor(fineLineColor);
		
		paint_brokenLine = new Paint();
		paint_brokenLine.setStrokeWidth(tb * 0.3f);
		paint_brokenLine.setColor(blueLineColor);
		paint_brokenLine.setAntiAlias(true);

		paint_dottedline = new Paint();
		paint_dottedline.setStyle(Paint.Style.STROKE);
		paint_dottedline.setColor(fineLineColor);

		paint_brokenline_big = new Paint();
		paint_brokenline_big.setStrokeWidth(tb * 0.4f);
		paint_brokenline_big.setColor(fineLineColor);
		paint_brokenline_big.setAntiAlias(true);

		framPanint = new Paint();
		framPanint.setAntiAlias(true);
		framPanint.setStrokeWidth(2f);

		point_path = new Path();
		bitmap_point = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_point_blue);
		
		nMax = Collections.max(milliliter);
		nMin = Collections.min(milliliter);
		pading_x = getWidth()/12;
		pading_y = getHeight()/8;
		
	}
	public float im = 0;
	protected void onDraw(Canvas c) {
		
		if (null == milliliter || milliliter.size() == 0)
			return;
		initData();
		if(getWidth() > 480)
			drawMinMax(c);
		drawStraightLine(c);
		drawBrokenLine(c);
		//drawDate(c);
	}
	public void drawMinMax(Canvas c) {
		paint_date.setTextSize(tb * 2.5f);
		paint_maxmin.setTextSize(tb * 2.5f);
		screen_fix = 25;
		
		//String str = String.format("Max:%d , Min:%d", nMax,nMin);
		
		
		float base_y = (getHeight() - 5*pading_y/2  )/ Collections.max(milliliter) ;
		float base_x = (getWidth() - 2*pading_x)/( milliliter.size() - 1);
		if(im < pading_x)
			im = pading_x;
		int i= (int)((im - pading_x)/base_x);
		
		if(i > milliliter.size() - 1)
			i = milliliter.size() - 1;
		
		c.drawText(""+String.format("%.2fMpa", (float)milliliter.get(i)/100),pading_x + base_x * i, pading_y, paint_maxmin);
		c.drawText("" + String.format("%02d:%02d", (24*60*(start + i)/total)/60,(24*60*(start + i)/total)%60),pading_x + base_x * i, 3*pading_y/2, paint_maxmin);
		c.drawLine(pading_x + base_x * i,pading_y, pading_x + base_x * i, getHeight() - pading_y, paint_maxmin);
		//c.drawText("m /h", getWidth() - pading_x + 5, pading_y + 8, paint_maxmin);
		//paint_maxmin.setTextSize(tb * 1.5f);
		//c.drawText("3", getWidth() - pading_x + 18 + 5 , pading_y, paint_maxmin);
		//c.drawText(nMin+"", getWidth() - pading_x , getHeight() - pading_y - base_y*nMin + 15, paint_date);
	}
	public void drawStraightLine(Canvas c) {
		paint_dottedline.setColor(fineLineColor);
		float wid = ((float)getWidth() - 2*pading_x)/12;
		float n=  (float)milliliter.size()/12;
		
		for (int i = 0; i < 13; i++) {
			if(i != 0){
				Path path = new Path();
				path.moveTo(pading_x + wid * (i ), pading_y + pading_y*(i%2)/2);
				path.lineTo(pading_x + wid * (i), getHeight() - pading_y);
				PathEffect effects = new DashPathEffect(new float[] { tb * 0.3f,
						tb * 0.3f, tb * 0.3f, tb * 0.3f }, tb * 0.1f);
				paint_dottedline.setPathEffect(effects);
				c.drawPath(path, paint_dottedline);
			}
			if(0 == i%2){
				//n = milliliter.get(0) + i*n;
				//Log.i("debug", "drawStraightLine:" + start + ","+(start + (int)i*n));
				int temp =(int) (i*n/1);
				c.drawText(String.format("%02d:%02d", (24*60*(start + temp)/total)/60,(24*60*(start + temp)/total)%60), pading_x + wid * i - screen_fix, getHeight() - pading_y + 30, paint_date);
			}
			
		}
		
		//print X Line
		c.drawLine(pading_x, getHeight() - pading_y, getWidth() - pading_x/2, getHeight() - pading_y, paint_brokenline_big);
		//print Y Line
		c.drawLine(pading_x,pading_y/2, pading_x, getHeight() - pading_y, paint_brokenline_big);
		
		c.drawText("MPa",  pading_x/2 - 10,pading_y/2,paint_date);
		
	}
	public void drawBrokenLine(Canvas c) {
		int index = 0;
		float base_y = (getHeight() - 5*pading_y/2  )
				/ Collections.max(milliliter) ;//- Collections.min(milliliter));
		float base_x = (getWidth() - 2*pading_x)
				/( milliliter.size() - 1);
		
		Shader mShader = new LinearGradient(pading_x, pading_y, pading_x, getHeight()-pading_y, new int[] {
				Color.argb(100, 0, 255, 255), Color.argb(45, 0, 255, 255),
				Color.argb(10, 0, 255, 255) }, null, Shader.TileMode.CLAMP);
		framPanint.setShader(mShader);
		for (int i = 0; i < milliliter.size() - 1; i++) {
			float x1 = pading_x + base_x * i;
			float y1 = getHeight() - pading_y - (base_y * milliliter.get(i));
			float x2 = pading_x + base_x * (i + 1);
			float y2 = getHeight() - pading_y - (base_y * milliliter.get(i + 1));
			if(i == 0)
				point_path.moveTo(pading_x, getHeight() - pading_y);
			
			if (index == 0) {
				c.drawLine(x1, y1, x2, y2, paint_brokenLine);
				point_path.lineTo(x1, y1);
				/*
				if (nMax == milliliter.get(i) || nMin == milliliter.get(i))
					c.drawBitmap(bitmap_point,
							x1 - bitmap_point.getWidth() / 2,
							y1 - bitmap_point.getHeight() / 2, null);*/
				if (i == milliliter.size() - 2) {
					point_path.lineTo(x2, y2);
					point_path.lineTo(x2, getHeight() - pading_y);
					point_path.lineTo(pading_x, getHeight() - pading_y);
					point_path.close();
					c.drawPath(point_path, framPanint);
				}
			}
		}
		//花红色的线条
		paint_dottedline.setColor(orangeLineColor);
		Path path = new Path();
		path.moveTo(pading_x, getHeight() - pading_y - base_y*nMax);
		path.lineTo(getWidth() - pading_x, getHeight() - pading_y - base_y*nMax);
		path.moveTo(pading_x, getHeight() - pading_y - base_y*nMin);
		path.lineTo(getWidth() - pading_x, getHeight() - pading_y - base_y*nMin);
		
		PathEffect effects = new DashPathEffect(new float[] { tb * 0.3f,
				tb * 0.3f, tb * 0.3f, tb * 0.3f }, tb * 0.1f);
		paint_dottedline.setPathEffect(effects);
		c.drawPath(path, paint_dottedline);
		
		
		String str = String.format("%.2f",(float)nMin/100);
		c.drawText(str, pading_x/2 - 10, getHeight() - pading_y - base_y*nMin, paint_maxmin);
		
		str = String.format("%.2f",(float)nMax/100);
		c.drawText(str, pading_x/2 - 10, getHeight() - pading_y - base_y*nMax, paint_maxmin);

	}
}
