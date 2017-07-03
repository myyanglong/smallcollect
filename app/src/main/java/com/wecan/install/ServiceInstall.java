package com.wecan.install;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wecan.domain.Configs;
import com.wecan.domain.WaterMeter;
import com.wecan.service.DBOpenHelper;

public class ServiceInstall {
	private DBOpenHelper dbOpenHelper;
	SQLiteDatabase db;
	private Context context;
	public ServiceInstall(Context context) {
		this.context = context;
		this.dbOpenHelper = new DBOpenHelper(context);
		this.db = dbOpenHelper.getWritableDatabase();
	}
	
	public void insertWaterMeter(WaterMeter wm){
		db.execSQL("insert into tb_info(id,area_id,remarke,floor,door,action_id,tag,read) values(?,?,?,?,?,?,?,?)",
			new Object[]{wm.id, wm.unit,wm.address,wm.floor,wm.door,wm.action_id,wm.tag,wm.read});
	}
	/**
	 * 添加XML数据
	 * id,area_id,remarke,floor,door,action_id
	 * tag 自动填充为 0
	 * @param wms
	 */
	public void insertWaterMeters(List<WaterMeter> wms){
		db.execSQL("delete from tb_info");
		db.beginTransaction();//开启事务
		try{
			for(WaterMeter wm:wms)
				insertWaterMeter(wm);
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	////tb_config(keyid integer primary key autoincrement,id,type,u_id,f_id,tag)");
	public void insertMeters(List<Configs> cgs){
		db.execSQL("delete from tb_config");
	
		db.beginTransaction();//开启事务
		try{
			for(Configs cg:cgs)
				db.execSQL("insert into tb_config(id,type,u_id,f_id,action,tag,address) values(?,?,?,?,?,?,?)",
						new Object[]{cg.id,cg.type,cg.u_id,cg.f_id,cg.action,cg.tag,cg.address});
			db.setTransactionSuccessful();
		}finally{
			Log.i("debug", "测试");
			db.endTransaction();
		}
	}
	/**
	 * 添加水表的查询结果
	 * @param index
	 * @param str_area
	 * @param str_build
	 * @param str_floor
	 * @param flag
	 * @return
	 */
	//public List<WaterMeter> meter = new ArrayList<WaterMeter>();
	public List<WaterMeter> selectWaterMeter(int index,String str_area,String str_build,String str_floor,String f_id ,boolean flag){
		Cursor cursor = null;
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		String str = null;
		if(flag)
			str = "action_id=?";
		else
			str = "action_id!=?";//tag!=1 and tag!=2 and 
		//Log.i("debug", str + "," + index + "," + f_id);
		switch(index){
			case 0:
				cursor = db.rawQuery("select * from tb_info where " + str, new String[]{f_id} );
				break;
			case 1:
				cursor = db.rawQuery("select * from tb_info where area_id=? and " + str, new String[]{str_area,f_id});
				break;
			case 2:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and " + str, new String[]{str_area,str_build,f_id});
				break;
			case 3:
				cursor = db.rawQuery(String.format("select * from tb_info where area_id='%s' and remarke='%s' and (floor='%s') and ", str_area,str_build,str_floor) + str,new String[]{f_id});
				break;
			default:
				break;
		}
		 //WaterMeter(String id, String unit, String address,String floor,String door) 
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
			int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String ac_c = cursor.getString(cursor.getColumnIndex("ac_c"));
			String ac_z = cursor.getString(cursor.getColumnIndex("ac_z"));
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			meter.add(new WaterMeter(id,unit,address,floor,door,action_id,action_type,ac_c,ac_z,tag));
		}
		cursor.close();
		return meter;
	}
	//action_type,action_id,ac_c,ac_z,tag
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
	public List<WaterMeter> selectWaterMeterFromID(String f_id,boolean flag){
		Cursor cursor = null;
		String temp,str;
		List<WaterMeter> water = new ArrayList<WaterMeter>();
		if(flag)
			str = "tag=2";
		else
			str = "tag!=2";
		
		temp = "select * from tb_info where action_id=? and " + str;
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
			int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String ac_c = cursor.getString(cursor.getColumnIndex("ac_c"));
			String ac_z = cursor.getString(cursor.getColumnIndex("ac_z"));
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			//Log.i("debug", "id " + id + ",action_type " + action_type + ",action_id " + action_id + ",ac_c " + ac_c + ",ac_z " + ac_z);
			water.add(new WaterMeter(id,unit,address,floor,door,action_id,action_type,ac_c,ac_z,tag));
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
			int action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			String ac_c = cursor.getString(cursor.getColumnIndex("ac_c"));
			String ac_z = cursor.getString(cursor.getColumnIndex("ac_z"));
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			//Log.i("debug", "id " + id + ",action_type " + action_type + ",action_id " + action_id + ",ac_c " + ac_c + ",ac_z " + ac_z);
			water.add(new WaterMeter(id,unit,address,floor,door,action_id,action_type,ac_c,ac_z,tag));
		}
		cursor.close();
		return water;
	}
	
	public List<String> selectAllConfigsFromId(String fid){
		List<String> list= new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_config where f_id=?", new String[]{fid});
		//Cursor cursor = db.rawQuery("select * from tb_config", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		*/
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			String u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			String f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			//Log.i("debug",id+","+type+"," + u_id+"," + f_id);
			list.add(id);
			List<String> temp= selectAllConfigsFromId(id);
			if(temp.size() > 0){
				for(String str:temp)
					list.add(str);
			}
		}
		return list;
	}
	public List<Configs> selectMeterForDev(String id){
		Cursor cursor = null;
		String temp,str;
		List<Configs> cgs = new ArrayList<Configs>();
		//tb_config(keyid integer primary key autoincrement,id,type,u_id,f_id,tag)");
		
		cursor = db.rawQuery("select * from tb_config where f_id=?", new String[]{id});
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
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
	public List<Configs> selectMeterForList(int type){
		Cursor cursor = null;
		String str;
		List<Configs> cgs = new ArrayList<Configs>();
		//tb_config(keyid integer primary key autoincrement,id,type,u_id,f_id,tag)");
		if(type == 0)
			str = "select * from tb_config where type!=0";
		else
			str = "select * from tb_config where type=2";
		cursor = db.rawQuery(str, null);
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			cg.type = cursor.getInt(cursor.getColumnIndex("type"));
			cg.u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			cg.f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			cg.tag = cursor.getInt(cursor.getColumnIndex("tag"));
			cgs.add(cg);
		}
		cursor.close();
		return cgs;
	}
	public void updateWaterMeter(WaterMeter wm){
		//Log.i("debug", wm.time);
		db.execSQL("update tb_info set action_id=?,tag=? where id=?",
				new Object[]{wm.action_id,wm.tag,wm.id});
		Log.i("debug", "id " + wm.id + ",action_id " + wm.action_id + ",tag " + wm.tag);
	}
	public void updateWaterMeterId(String oldId, String newId){
		//Log.i("debug", wm.time);
		db.execSQL("update tb_info set id=? where id=?",
				new Object[]{newId,oldId});
	}
	public void updateWaterMeterTag(String id,int type){
		//Log.i("debug", wm.time);
		db.execSQL("update tb_info set tag=? where id=?",
				new Object[]{type,id});
	}
	public void resetWaterMeterTag(String id,int type){
		//Log.i("debug", wm.time);
		db.execSQL("update tb_info set tag=? where action_id=?",
				new Object[]{type,id});
	}
	public void updateMeterTag(String id,int type){
		db.execSQL("update tb_config set tag=? where id=?",
				new Object[]{type,id});
	}
	public void restMeterTag(String id){
		db.execSQL("update tb_config set tag=1 where f_id=? and tag > 1",
				new Object[]{id});
	}
	public void readWaterMeter(WaterMeter wm) {
		db.execSQL("update tb_info set action_type=0,action_id=?,tag=? where id=?",
				new Object[]{wm.action_id,wm.type, wm.id});
	}
	public void updateWaterMeterDel(String id,int type){
		//Log.i("debug", wm.time);
		switch(type){
			case 0:
				db.execSQL("update tb_info set action_type=0,action_id=0,tag=0 where id=?",
						new Object[]{id});
				break;
			case 1:
				db.execSQL("update tb_info set action_type=0,ac_c=0,tag=0 where id=?",
						new Object[]{id});
				break;
			default:
				db.execSQL("update tb_info set action_type=0,ac_z=0,tag=0 where id=?",
						new Object[]{id});
				break;
		}
		
	}
	public void updateWaterMeterSec(String id){
		//Log.i("debug", wm.time);
		db.execSQL("update tb_info set action_id='0',tag=0 where id=?",
				new Object[]{id});
	}
	/**
	 * 更新添加水表列表
	 * @param wms
	 */
	public void updateWaterMeters(List<WaterMeter> wms){
		db.beginTransaction();//开启事务
		try{
			for(WaterMeter wm:wms)
				updateWaterMeter(wm);
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	public void updateWaterMetersList(List<Configs> cgs){
		db.beginTransaction();//开启事务
		try{
			for(Configs cg:cgs)
				db.execSQL("update tb_config set f_id=? where id=?",
						new Object[]{cg.f_id,cg.id});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	public void deleteWaterMeters(List<WaterMeter> wms) {
		db.beginTransaction();//开启事务
		try{
			for(WaterMeter wm:wms) {
				db.execSQL("delete from tb_info where id=?",new Object[]{wm.id});
			}
			db.setTransactionSuccessful();//设置事务的标志为True
		} finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		}
	}
	
	public void DeleteMeter(String id){
		db.execSQL("delete from tb_config where id=?",new Object[]{id});
	}
	public void print_info(){
		Cursor cursor = db.rawQuery("select * from tb_info",null);
		
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
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			Log.i("debug", id + "," + unit  + "," + address  + "," + floor  + "," + door  + "," +action_id  + "," +
			               tag  + ",");
		}
		cursor.close();
	}

	public void resetWaterMeterForDev(String ac_id) {
		// TODO Auto-generated method stub
		List<String> temp = selectAllConfigsFromId(ac_id);
		temp.add(ac_id);
		if(temp.size() > 0){
			for(String str:temp){
				db.execSQL("update tb_info set tag=0 where action_id=?",
						new Object[]{str});
			}
		}
	}
}