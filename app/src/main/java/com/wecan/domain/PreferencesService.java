package com.wecan.domain;

import java.util.HashMap;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;
/**
 * 
 * @author Leo
 *
 */
public class PreferencesService {
	private Context context;
	
	public PreferencesService(Context context) {
		this.context = context;
	}
	/**
	 * 保存参数
	 */
	public void save_area(Integer area_id) {
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("area_id", area_id);
		editor.commit();
	}
	public void save_floor(Integer floor) {
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("floor", floor.toString());
		editor.commit();
	}
	public void saveGrpChnl(int grpchnl){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("GrpChnl", String.format("%04d", grpchnl));
		editor.commit();
	}
	/**
	 * 保存用户选择的内容
	 * @param type
	 * @param str_area
	 * @param str_build
	 * @param str_floor
	 */
	public void save_SmallList(int type, String str_area, String str_build,String str_floor) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putInt("type", type);
		editor.putString("str_area", str_area);
		editor.putString("str_build", str_build);
		editor.putString("str_floor", str_floor);
		editor.commit();
	}
	public void save_userinfo(String str_name, String str_pwd,int type,int flag) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putInt("user_type", type);
		editor.putInt("meter_type", flag);
		editor.putInt("login", 1);
		editor.putString("user_name", str_name);
		editor.putString("user_pwd", str_pwd);
		editor.commit();
	}
	public void save_actionId(String action_id) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putString("action_id", action_id);
		editor.commit();
	}
	
	public void save_actionAddress(String unit, String address) {
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putString("address_unit", unit);
		editor.putString("address_address", address);

		editor.commit();
	}
	
	public String getActionId(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getString("action_id", "0");
	}
	
	public String getActionAddress() {
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getString("address_unit", "0") + "+" + preferences.getString("address_address", "0");
	}
	
	public int getLoginFlag(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getInt("login", 0);
	}
	public int getUserType(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getInt("user_type", 0);
	}
	public int getMeterType(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getInt("meter_type", 0);
	}
	public String getFloor(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		return preferences.getString("floor", "0");
	}
	/**
	 * 获取各项配置参数
	 */
	public Map<String, String> getPreferences(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		params.put("name", preferences.getString("name", ""));
		params.put("area_id", String.valueOf(preferences.getInt("area_id", 0)));
		return params;
	}
	
	/**
	 * 获取信道信道组68574101XXXX051516
	 * @return
	 */
	public String getGrpChnl(boolean bflag){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		if(bflag)
			return preferences.getString("GrpChnl", "0000");
		else
			return new IntentInfo(preferences.getString("GrpChnl", "0000")).temp;
	}/*
	public Map<String, String> getPreferences(){
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		params.put("name", preferences.getString("name", ""));
		params.put("age", String.valueOf(preferences.getInt("age", 0)));
		return params;
	}*/
	
	/**
	 * 获取用户选择的内容(type,str_area,str_build,str_floor)
	 * @return
	 */
	public Bundle get_SmallListTag(){
		Bundle bundle = new Bundle();
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
    	bundle.putInt("type",preferences.getInt("type", 0));
    	bundle.putString("str_area", preferences.getString("str_area", ""));
    	bundle.putString("str_build", preferences.getString("str_build", ""));
    	bundle.putString("str_floor", preferences.getString("str_floor", ""));
		return bundle;
	}
	/**
	 * 清空用户设置(type)
	 */
	public void exit_clenn(){
		SharedPreferences preferences = context.getSharedPreferences("weian", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		
		editor.putInt("login", 0);
		editor.putString("user_name", "");
		editor.commit();
	}
}
