package com.wecan.small;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.wecan.check.WaterError;
import com.wecan.domain.Configs;
import com.wecan.domain.SmallConfigs;
import com.wecan.domain.WaterMeter;
import com.wecan.service.DBOpenHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceSmall {
	private DBOpenHelper dbOpenHelper;
	SQLiteDatabase db;
	//private Context context;
	public ServiceSmall(Context context) {
		//this.context = context;
		this.dbOpenHelper = new DBOpenHelper(context);
		this.db = dbOpenHelper.getWritableDatabase();
	}
	public List<String> selectAllConfigsFromId(String fid){
		List<String> list= new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_config where f_id=?", new String[]{fid});
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			list.add(id);
			List<String> temp= selectAllConfigsFromId(id);
			if(temp.size() > 0){
				for(String str:temp)
					list.add(str);
			}
		}
		return list;
	}
	public List<Configs> selectMeterForDev(){
		Cursor cursor = null;
		List<Configs> cgs = new ArrayList<Configs>();
		cursor = db.rawQuery("select * from tb_config", null);
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			Log.i("debug", cg.id);
			cg.type = cursor.getInt(cursor.getColumnIndex("type"));
			cg.u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			cg.f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			cg.tag = cursor.getInt(cursor.getColumnIndex("tag"));
			cg.address = cursor.getString(cursor.getColumnIndex("address"));
			cgs.add(cg);
		}
		cursor.close();
		return cgs;
	}
	
	public List<SmallConfigs> selectMeterForAddress(){
		Cursor cursor = null;
		List<SmallConfigs> cgs = new ArrayList<SmallConfigs>();
		cursor = db.rawQuery("select area_id, remarke, count(*) as count from tb_info group by area_id, remarke", null);
		while(cursor.moveToNext()){
			SmallConfigs cg = new SmallConfigs();
			cg.id = cursor.getString(cursor.getColumnIndex("area_id"));
			cg.address = cursor.getString(cursor.getColumnIndex("address"));
			cg.meterCnt = cursor.getInt(cursor.getColumnIndex("count"));
			cgs.add(cg);
		}
		cursor.close();
		return cgs;
	}
	
	
	public List<WaterMeter> selectWaterMeterForDev(String ac_id,boolean flag){
		List<WaterMeter> water = new ArrayList<WaterMeter>();
		List<String> temp = selectAllConfigsFromId(ac_id);
		temp.add(ac_id);
		if(temp.size() > 0){
			for(String str:temp){
				water.addAll(selectWaterMeterFromID(str,flag));
			}
		}
		return water;
	}
	
	public List<SmallConfigs> selectDevFromID(){
		Cursor cursor = null;
		List<SmallConfigs> cgs = new ArrayList<SmallConfigs>();
		cursor = db.rawQuery("select * from tb_config", null);
		while(cursor.moveToNext()){
			SmallConfigs cg = new SmallConfigs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			cg.address = cursor.getString(cursor.getColumnIndex("address"));
			cg.meterCnt = selectWaterMeterFromID(cg.id).size();
			cgs.add(cg);
		}
		cursor.close();
		return cgs;
	}
	
	
	public List<WaterError> selectWaterError(){
		List<WaterError> wes = new ArrayList<WaterError>();
		Cursor cursor = db.rawQuery("select a.area_id,a.remarke,a.floor,a.door,b.id,b.img,b.dsc,b.time from tb_info as a,tb_err as b  where a.id=b.id", null);
		while(cursor.moveToNext()){
			WaterError we=new WaterError();
			we.id = cursor.getString(cursor.getColumnIndex("id"));
			we.unit = cursor.getString(cursor.getColumnIndex("area_id"));
			we.address = cursor.getString(cursor.getColumnIndex("remarke"));
			we.floor = cursor.getString(cursor.getColumnIndex("floor"));
			we.door = cursor.getString(cursor.getColumnIndex("door"));
			we.img = cursor.getString(cursor.getColumnIndex("img"));
			we.dsc = cursor.getString(cursor.getColumnIndex("dsc"));
			we.time = cursor.getString(cursor.getColumnIndex("time"));
			wes.add(we);
		}
		cursor.close();
		return wes;
	}

	public  List<WaterMeter> selectWaterMeterFromID(String f_id,boolean flag){
		Cursor cursor = null;
		String temp,str;
		List<WaterMeter> water = new ArrayList<WaterMeter>();
		if(flag)
			str = "read=1";
		else
			str = "read=0";
		
		temp = "select * from tb_info where action_id=? and " + str;
		//Log.i("debug", temp +"," + f_id);
		cursor = db.rawQuery(temp, new String[]{f_id});
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			String address = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			if(unit == null)
				unit="";
			if(address == null)
				address="";
			if(floor == null)
				floor="";
			if(door == null)
				door="";
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			//int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			//int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			WaterMeter wm = new WaterMeter(id,unit,address,floor,door,action_id,total,time);
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			water.add(wm);
		}
		cursor.close();
		return water;
	}
	
	public List<WaterMeter> selectWaterMeterFromID(String f_id){
		Cursor cursor = null;
		String temp;
		List<WaterMeter> water = new ArrayList<WaterMeter>();
		
		temp = "select * from tb_info where action_id=?";
		cursor = db.rawQuery(temp, new String[]{f_id});
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			String address = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			if(unit == null)
				unit="";
			if(address == null)
				address="";
			if(floor == null)
				floor="";
			if(door == null)
				door="";
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			//int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			//int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			WaterMeter wm = new WaterMeter(id,unit,address,floor,door,action_id,total,time);
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			water.add(wm);
		}
		cursor.close();
		return water;
	}
	
	
	public List<WaterMeter> selectWaterMeterFromAddress(String area, String addr, boolean flag){
		Cursor cursor = null;
		String temp,str;
		List<WaterMeter> water = new ArrayList<WaterMeter>();
		if(flag)
			str = "read=1";
		else
			str = "read=0";
		
		temp = "select * from tb_info where area_id=? and remarke=? and " + str;
		//Log.i("debug", temp +"," + f_id);
		cursor = db.rawQuery(temp, new String[]{area, addr});
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			String address = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			if(unit == null)
				unit="";
			if(address == null)
				address="";
			if(floor == null)
				floor="";
			if(door == null)
				door="";
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			//int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			//int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			WaterMeter wm = new WaterMeter(id,unit,address,floor,door,action_id,total,time);
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			water.add(wm);
		}
		cursor.close();
		return water;
	}
	
	public List<WaterMeter> selectWaterMeterFromAddress(String area, String addr){
		Cursor cursor = null;
		String temp;
		List<WaterMeter> water = new ArrayList<WaterMeter>();

		temp = "select * from tb_info where area_id=? and remarke=?";
		cursor = db.rawQuery(temp, new String[]{area, addr});
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			String address = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			if(unit == null)
				unit="";
			if(address == null)
				address="";
			if(floor == null)
				floor="";
			if(door == null)
				door="";
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String time = cursor.getString(cursor.getColumnIndex("time"));

			WaterMeter wm = new WaterMeter(id,unit,address,floor,door,action_id,total,time);
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			water.add(wm);
		}
		cursor.close();
		return water;
	}
	
	public void update_water(Bundle bd){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).format(new Date(System.currentTimeMillis()));
		db.execSQL("update tb_info set time=?,total=?,status=?,rf=?,read=1 where id=?",
			new Object[]{time,bd.getString("t_0"),bd.getInt("s_0"),bd.getInt("rf_0"),bd.getString("id_0")});
		if(bd.getString("id_1") != null){
			db.execSQL("update tb_info set time=?,total=?,status=?,rf=?,read=1 where id=?",
					new Object[]{time,bd.getString("t_1"),bd.getInt("s_1"),bd.getInt("rf_1"),bd.getString("id_1")});
			if(bd.getString("id_2") != null){
				db.execSQL("update tb_info set time=?,total=?,status=?,rf=?,read=1 where id=?",
						new Object[]{time,bd.getString("t_2"),bd.getInt("s_2"),bd.getInt("rf_2"),bd.getString("id_2")});
			}
		}
	}

	/**
	 * 清除小表采集数据
	 * @param f_id 需要清除数据ID
	 */
	public void clear_water(String f_id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		List<String> temp = selectAllConfigsFromId(f_id);
		temp.add(f_id);
		if(temp.size() > 0){
			for(String str:temp){
				db.execSQL("update tb_info set time=null,total='',status=0,rf=0,read=0 where action_id=? and read =1",new String[]{str});
			}
		}
		db.close();
	}
}