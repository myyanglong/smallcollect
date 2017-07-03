package com.wecan.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 鑷畾涔塈mageView瑙嗗�? */
public class MyImageView extends ImageView {

	private boolean onAnimation = true;
	private float minScale = 0.95f;
	
	private int vWidth;
	private int vHeight;
	private boolean isFinish = true;

	boolean XbigY = false;

	OnViewClick onclick = null;
	private boolean isFirst = true,isScale = false,isActionMove = false;
	
	//private Camera camera = null; 

	public MyImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//camera = new Camera();
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//camera = new Camera();
	}
	public void setOnClickIntent(OnViewClick onclick) {
		this.onclick = onclick;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isFirst) {
			isFirst = false;
			init();
		}
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));// 鎶楅敮榻�?
	}

	public void init() {
		vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		vHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		Drawable drawable = getDrawable();
		BitmapDrawable bd = (BitmapDrawable) drawable;
		bd.setAntiAlias(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (!onAnimation)		
			return true;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if(!isFinish || isScale)
					break;
				float X = event.getX();
				float Y = event.getY();
				
				isScale = X > vWidth / 3 && X < vWidth * 2 / 3 && Y > vHeight / 3
						&& Y < vHeight * 2 / 3;
				isScale = true ;
				isFinish = false;
				scaleHandler.sendEmptyMessage(1);
				break;
			case MotionEvent.ACTION_MOVE:// 绉诲�?				
				float x = event.getX();
				float y = event.getY();
				if (x > vWidth || y > vHeight || x < 0 || y < 0) {
					isActionMove = true;
				} else {
					isActionMove = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				scaleHandler.sendEmptyMessage(6);
				break;
		}
		return true;
	}
	public interface OnViewClick {
		public void onClick();
	}

	private Handler scaleHandler = new Handler() {
		private Matrix matrix = new Matrix();
		private float s;
		int count = 0;
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix.set(getImageMatrix());
			switch (msg.what) {
			case 1:
				count = 0;
				s = (float) Math.sqrt(Math.sqrt(minScale));
				BeginScale(matrix, s);
				scaleHandler.sendEmptyMessage(2);
				break;
			case 2:
				BeginScale(matrix, s);
				if (count < 4)
					scaleHandler.sendEmptyMessage(2);
				else
					isFinish = true;
				count++;
				break;
			case 3:
				BeginScale(matrix, s);
				if (count < 4)
					scaleHandler.sendEmptyMessage(3);
				else {
					isFinish = true;
					isScale = false;
					if (!isActionMove && onclick != null) {
						onclick.onClick();
					}
				}
				count++;
				break;
			case 6:
				if(!isFinish || !isScale)
					scaleHandler.sendEmptyMessage(6);
				else{
					isFinish = false;
					count = 0;
					s = (float) Math.sqrt(Math.sqrt(1.0f / minScale));
					BeginScale(matrix, s);
					scaleHandler.sendEmptyMessage(3);
				}
				break;
			}
		}
	};
	private synchronized void BeginScale(Matrix matrix, float scale) {
		int scaleX = (int) (vWidth * 0.5f);
		int scaleY = (int) (vHeight * 0.5f);
		matrix.postScale(scale, scale, scaleX, scaleY);
		setImageMatrix(matrix);
	}
}
