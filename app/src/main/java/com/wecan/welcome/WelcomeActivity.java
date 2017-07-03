package com.wecan.welcome;

import com.wecan.smallcollect.R;
import com.wecan.activity.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {
	
	private ImageView mShowPicture;
	//private TextView mShowText;

	private Animation mFadeIn;
	private Animation mFadeInScale;
	private Animation mFadeOut;
	
	private Drawable mPicture;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.w_welcome);
        
      
        mShowPicture = (ImageView) findViewById(R.id.guide_picture);
		//mShowText = (TextView) findViewById(R.id.guide_content);
		init();
		setListener();
    }
    
    /**
	 * 监听事件
	 */
	private void setListener() {
		mFadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				//mShowText.setText("setListener_onAnimationStart");
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeInScale);
				//mShowText.setText("setListener_onAnimationEnd");
			}
		});
		mFadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				//mShowText.setText("mFadeInScale_onAnimationStart");
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeOut);
				//mShowText.setText("mFadeInScale_onAnimationEnd");
			}
		});
		mFadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				if (mShowPicture.getDrawable().equals(mPicture)) {
					//mShowText.setText("图片2");
					//mShowPicture.setImageDrawable(mPicture_2);
					
				}
			}
			public void onAnimationRepeat(Animation animation) {
				if (mShowPicture.getDrawable().equals(mPicture)) {
					//mShowText.setText("图片3");
					//mShowPicture.setImageDrawable(mPicture_2);
				}
			}
			public void onAnimationEnd(Animation animation) {
				if (mShowPicture.getDrawable().equals(mPicture)) {
					//mShowText.setText("图片4");
					 
					startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
					AnimationUtil.finishActivityAnimation(WelcomeActivity.this);
					/*
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run(){
							Intent intent = new Intent (WelcomeActivity.this,EntryActivity.class);			
							startActivity(intent);			
							WelcomeActivity.this.finish();
							
							startActivity(new Intent(WelcomeActivity.this,EntryActivity.class));
							AnimationUtil.finishActivityAnimation(WelcomeActivity.this);
						}
					}, 10);*/
				}
				
			}
		});
		
	}

	/**
	 * 初始化
	 */
	private void init() {
		initAnim();
		initPicture();
		mShowPicture.setImageDrawable(mPicture);
		//mShowText.setText("图片1");
		mShowPicture.startAnimation(mFadeIn);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mFadeIn = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.w_fade_in);
		mFadeIn.setDuration(1000);
		mFadeInScale = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.w_fade_in_scale);
		mFadeInScale.setDuration(3000);
		mFadeOut = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.w_fade_out);
		mFadeOut.setDuration(1000);
	}

	/**
	 * 初始化图片
	 */
	private void initPicture() {
		mPicture = getResources().getDrawable(R.drawable.w_logo);
	}
    
}


