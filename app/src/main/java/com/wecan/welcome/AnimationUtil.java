/**
 * AnimationUtils.java [V 1..0.0]
 * classes : com.hb56.hps.android.utils.AnimationUtils
 * zhangyx Create at 2014-10-31 涓嬪崍2:31:50
 */
package com.wecan.welcome;

import com.wecan.smallcollect.R;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

/**
 * 鑷畾涔夋帶浠剁殑鍔ㄧ敾鏁堟灉
 *com.zhangyx.MyLauncherGuide.utils.AnimationUtil
 * @author Admin-zhangyx
 *
 * create at 2015-1-21 涓嬪崍1:51:08
 */
public class AnimationUtil {

	/* 鐗规晥婧愮爜---------listview鍔犺浇鐨勬晥鏋?*/
	public static LayoutAnimationController getListAnimTranslate() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(800);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);

		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		return controller;
		/*-----------------------------------------*/
	}

	/**
	 * 閫?嚭Activity鐨勫姩鐢?: zoom 鍔ㄧ敾
	 * 
	 * @param context
	 */
	public static void finishActivityAnimation(Context context) {
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.w_enter,
				R.anim.w_exit);
	}

	/***
	 * zoom 鍔ㄧ敾s
	 * 
	 * @param context
	 */
	public static void activityZoomAnimation(Context context) {
		((Activity) context).overridePendingTransition(R.anim.w_enter,
				R.anim.w_exit);
	}

}
