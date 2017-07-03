package com.wecan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.wecan.domain.BigMeter;
import com.wecan.domain.Configs;
import com.wecan.domain.DMAMeter;
import com.wecan.domain.Fault;
import com.wecan.domain.Users;
import com.wecan.domain.WaterMeter;
import com.wecan.install.ServiceInstall;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;


public class WaterMeterService {
	private DBOpenHelper dbOpenHelper;
	private Context context;
	public WaterMeterService(){
		
	}
	public WaterMeterService(Context context) {
		this.context = context;
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	
	public void printDataList(){
		Cursor cursor;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		cursor = db.rawQuery("select tb_area.name as name1,tb_region.name as name2,tb_district.name as name3 from tb_area,tb_region,tb_district " +
				"where tb_district.r_id=tb_region.id and tb_region.a_id=tb_area.id", null);
		
		while(cursor.moveToNext()){
			Log.i("debug",cursor.getString(cursor.getColumnIndex("name1")) + "," +
					cursor.getString(cursor.getColumnIndex("name2")) + "," +
					cursor.getString(cursor.getColumnIndex("name3")));
		}
		cursor.close();
	}
	/*
	 * 添加记录
	 * @param WaterMeter
	 */
	public void save(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into tb_info(id, remarke, floor,door,read) values(?,?,?,?,1)",
				new Object[]{watermeter.id, watermeter.unit, watermeter.address,watermeter.door});
	}
	/*
	 * 删除记录
	 * @param id 记录ID
	 */
	public void delete(String id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_info where id=?", new Object[]{id});
	}
	/*
	 * 更新记录
	 * @param person
	 */
	public void update(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update tb_info set action_id=?,action=? where id=?",
				new Object[]{watermeter.action_id,watermeter.action_type, watermeter.id});
	}
	/*
	 * 添加采集到的数据，空白数据（数据库没有此ID）
	 * id,area_id,remarke,floor,door,time,action_id ,action,total,status,rf,read
	 */
	public void add_water(WaterMeter wm){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	
		db.execSQL("insert into tb_info(id,area_id,remarke,floor,door,action_id,tag,read) values(?,?,?,?,?,?,?,?)",
				new Object[]{wm.id, wm.unit,wm.address,wm.floor,wm.door,wm.action_id,wm.tag,wm.read});
	}
	
	public void add_bigwater(DMAMeter bm){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	
		db.execSQL("insert into tb_dma(id,address,gtime,status,bubbles,time,ptotal,ntotal,insflow,max,maxtm,min,mintm,press,temp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				   new Object[]{bm.getID(),bm.getAddr(),bm.getGTime(),bm.getStaus(),bm.getBubbles(),bm.getTime(),
								bm.getPTotal(),bm.getNTotal(),bm.getInsFlow(),bm.getMax(),bm.getMaxTm(),
								bm.getMin(),bm.getMinTm(),bm.getPress(),bm.getTemp()});
	}
	
	public void del_bigwater(DMAMeter bm){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();	
		db.execSQL("delete from tb_dma where id=?", new Object[]{bm.getID()});		
	}
	
	/**
	 * 更新采集到的数据
	 * String id, String time, String total,int status,int rf,read=1
	 */
	public void update_data(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update tb_info set time=?,total=?,status=?,rf=?,read=1 where id=?",
			new Object[]{watermeter.time, watermeter.total,watermeter.status + "",watermeter.rf+"",watermeter.id});
	}
	public void updateBigMeter(DMAMeter bm) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		//time,total,status,min,max,value
		db.execSQL("update tb_dma set gtime=?,status=?,bubbles=?,time=?,ptotal=?,ntotal=?,insflow=?,max=?,maxtm=?,min=?,mintm=?,press=?,temp=? where id=?",
				   new Object[]{bm.getGTime(),bm.getStaus(),bm.getBubbles(),bm.getTime(),
								bm.getPTotal(),bm.getNTotal(),bm.getInsFlow(),bm.getMax(),bm.getMaxTm(),
								bm.getMin(),bm.getMinTm(),bm.getPress(),bm.getTemp(),bm.getID()});
	}
	/*
	 * 
	 */
	public boolean find(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			return true;
		}
		return false;
	}
	
	public String select_bigdata(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_dma where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			String mid = cursor.getString(cursor.getColumnIndex("id"));
			String address = cursor.getString(cursor.getColumnIndex("address"));

			cursor.close();
			return "id=" + mid + ",address=" + address ;
		}
		cursor.close();
		return null;
	}
	
	/*
	 * 查询采集的数据
	 * id,area_id,remarke,floor,door,time,action_id ,action,total,status,rf,read
	 */
	public String select_data(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			String mid = cursor.getString(cursor.getColumnIndex("id"));
			String remarke = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			String action = cursor.getString(cursor.getColumnIndex("action"));
			String area_id = cursor.getString(cursor.getColumnIndex("area_id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String status = cursor.getString(cursor.getColumnIndex("status"));
			String rf = cursor.getString(cursor.getColumnIndex("rf"));
			String read = cursor.getString(cursor.getColumnIndex("read"));
			//return (new WaterMeter(mid,remarke,floor,door, action_id, action)).toString();
			cursor.close();
			return "id=" + mid + ",remarke=" + remarke + ",floor=" + floor
					+ "\ndoor=" + door + ",action_id=" + action_id + ",action=" + action 
					+ "\narea_id=" + area_id + ",time=" + time + ",total=" + total
					+ "\nstatus=" + status + ",rf=" + rf + ",read=" + read;
		}
		cursor.close();
		return null;
	}
	/**
	 * 保存配置列表
	 * @param configs
	 */
	public void saveConfigs(List<Configs> configs){
		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_config");
		db.beginTransaction();//开启事务
		try{
			for(Configs config:configs)
				db.execSQL("insert into tb_config(id,type,u_id,f_id) values(?,?,?,?)",
						new Object[]{config.id, config.type + "",config.u_id,config.f_id});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	
	//db.execSQL("insert into tb_info(id,area_id,remarke,floor) values('" +"1512100001','伟岸测试','38栋','101')");
	public void saveWaterMeters(List<WaterMeter> wms){
		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_info");
		db.beginTransaction();//开启事务
		try{
			for(WaterMeter wm:wms)
				db.execSQL("insert into tb_info(id,area_id,remarke,floor,door,read,action_id) values(?,?,?,?,?,?,?)",
						new Object[]{wm.id, wm.unit,wm.address,wm.floor,wm.door,wm.read,wm.action_id});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	/**
	 * 保存大表数据
	 * @param bms
	 * @param bflag 0 抄表人员，1 稽查人员
	 */
	private void saveBigMeters(List<DMAMeter> bms,int bflag) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_big");
		db.beginTransaction();//开启事务
		try{
			//id,type,gTime,time,total,status,min,max,value,read
			for(DMAMeter bm:bms) {
				db.execSQL("insert into tb_dma(id,address,gtime,status,bubbles,time,ptotal,ntotal,insflow,max,maxtm,min,mintm,press,temp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						   new Object[]{bm.getID(),bm.getAddr(),bm.getGTime(),bm.getStaus(),bm.getBubbles(),bm.getTime(),
										bm.getPTotal(),bm.getNTotal(),bm.getInsFlow(),bm.getMax(),bm.getMaxTm(),
										bm.getMin(),bm.getMinTm(),bm.getPress(),bm.getTemp()});
			}
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	private void saveUsers(List<Users> users) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_user");
		db.beginTransaction();//开启事务
		try{
			//id,type,pwd
			for(Users user:users)
				db.execSQL("insert into tb_user(id,pwd,type) values(?,?,?)",
						new Object[]{user.id,user.pwd,user.type});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();
		}
	}
	public boolean checkUserLogin(String name,String pwd){
		Users user =  new Users();
		boolean flag = false;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_user where id=? and pwd=?", new String[]{name,pwd});
		 //WaterMeter(String id, String unit, String address,String floor,String door) 
		while(cursor.moveToNext()){
			user.id = cursor.getString(cursor.getColumnIndex("id"));
			user.pwd = cursor.getString(cursor.getColumnIndex("pwd"));
			user.type = cursor.getInt(cursor.getColumnIndex("type"));
			
			flag = true;
			Log.i("debug", user.id + "," + user.type + "," + user.pwd);
		}
		cursor.close();
		return flag;
	}
	/**
	 * 添加配置信息
	 * @param config
	 * @return
	 */
	public boolean addConfigs(Configs config){
		boolean bflag = true;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_config where id=?", new String[]{config.id});
		if(cursor.moveToFirst()){
			bflag = false;
		}
		cursor.close();
		if(bflag){
			db.execSQL("insert into tb_config(id,type,u_id,f_id,action,tag,address) values(?,?,?,?,?,?,?)",
					new Object[]{config.id,config.type,config.u_id,config.f_id,config.action,config.tag,config.address});
			Log.i("debug","Insert ");
		}
		else
			db.execSQL("update tb_config set type=?,f_id=?,action=?,tag=? where id=?", new Object[]{config.type,config.f_id,config.action,config.tag,config.id});
		return true;
	}
	public boolean updateConfigs(Configs config){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update tb_config set address=? where id=?", new Object[]{config.address,config.id});
		return true;
	}
	/**
	 * 查询配置信息
	 */
	public List<String> selectConfigs(String fid){
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
			//Log.i("debug",id+","+type+"," + u_id+"," + f_id);
			list.add(id);
			List<String> temp= selectConfigs(id);
			if(temp.size() > 0){
				for(String str:temp)
					list.add(str);
			}
		}
		return list;
	}
	/**
	 * 查询配置信息，按类别
	 * @param type
	 * @return
	 */
	public List<Configs> selectConfigs(int type){
		
		List<Configs> cgs = new ArrayList<Configs>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_config where type=" + type, null);
		
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			cg.type = cursor.getInt(cursor.getColumnIndex("type"));
			cg.u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			cg.f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			cg.address = cursor.getString(cursor.getColumnIndex("address"));
			cgs.add(cg);
		}
		return cgs;
	}
	/**
	 * 更新配置信息，
	 * @param ocg	以前的配置
	 * @param ncg	新的配置
	 */
	public void updateConfigs(Configs ocg,Configs ncg){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.beginTransaction();//开启事务
		try{
			db.execSQL("update tb_config set id=? f_id=? where id=?", new Object[]{ncg.id,ncg.f_id,ocg.id});
			db.execSQL("update tb_config set f_id=? where f_id=?", new Object[]{ncg.id,ocg.id});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
		//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
		
	}
	/**
	 * 删除配置
	 * @param id
	 */
	public void deleteConfigs(String id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from tb_config where id=?", new Object[]{id});
		db.execSQL("update tb_config set f_id='0' where f_id=?", new Object[]{id});
	}
	/**
	 * 获取当前小区列表
	 * 
	 */
	public List<String> getListArea(){
		List<String> arealist = new ArrayList<String>();
		arealist.add("全部小区");
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select area_id,count(*) from tb_info group by area_id", null);
		while(cursor.moveToNext()){
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			if(unit !=null)
				arealist.add(unit);
			else
				arealist.add("未知");
		}
		cursor.close();
		return arealist;
	}
	/**
	 * 获取楼栋列表
	 * @param area	小区名称
	 * @return
	 */
	public List<String> getListBuild(String area){
		List<String> buildlist = new ArrayList<String>();
		buildlist.add("全部楼栋");
		if(area == null)
			return buildlist;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select remarke,count(*) from tb_info where area_id=? group by remarke", new String[]{area});
		
		while(cursor.moveToNext()){
			String build = cursor.getString(cursor.getColumnIndex("remarke"));
			buildlist.add(build);
		}
		cursor.close();
		return buildlist;
	}
	public List<String> getListFloor(String area,String build) {
		List<String> floorlist = new ArrayList<String>();
		floorlist.add("全部楼层");
		if(area == null)
			return floorlist;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select floor,count(*) from tb_info where area_id=? and remarke=? group by floor", new String[]{area,build});
		
		while(cursor.moveToNext()){
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			floorlist.add(floor);
		}
		cursor.close();
		return floorlist;
	}
	
	public String getListFloorString(String area,String build) {
		String floor_str = "全部楼层";
		if(area == null)
			return floor_str;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select floor,count(*) from tb_info where area_id=? and remarke=? group by floor", new String[]{area,build});

		while(cursor.moveToNext()){
			String floor = "," + cursor.getString(cursor.getColumnIndex("floor"));
			floor_str = floor_str + floor;
		}
		cursor.close();
		return floor_str;
	}
	
	public List<WaterMeter> getSmallListWater(int index,String str_area,String str_build,String str_floor,boolean flag){
		Cursor cursor = null;
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		switch(index){
			case 0:
				cursor = db.rawQuery("select * from tb_info", null);
				break;
			case 1:
				cursor = db.rawQuery("select * from tb_info where area_id=?", new String[]{str_area});
				break;
			case 2:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=?", new String[]{str_area,str_build});
				break;
			case 3:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				break;
			case 10:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1", null);
				else
					cursor = db.rawQuery("select * from tb_info where read=0", null);
				break;
			case 11:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=?", new String[]{str_area});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=?", new String[]{str_area});
				break;
			case 12:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=? and remarke=?", new String[]{str_area,str_build});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=?", new String[]{str_area,str_build});
				break;
			case 13:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				break;
			case 20:
				cursor = db.rawQuery("select * from tb_info where tag=0", null);
				break;
			case 21:
				cursor = db.rawQuery("select * from tb_info where area_id=? and tag=0", new String[]{str_area});
				break;
			case 22:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and tag=0", new String[]{str_area,str_build});
				break;
			case 23:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=? and tag=0", new String[]{str_area,str_build,str_floor});
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
			
			
			switch(index){
				case 0:
				case 1:
				case 2:
				case 3:
					//meter.add(new WaterMeter(id,unit,address,floor,door,time,total,status,rf,read));
					//break;
				case 10:
				case 11:
				case 12:
				case 13:
					String time = cursor.getString(cursor.getColumnIndex("time"));
					String total = cursor.getString(cursor.getColumnIndex("total"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int rf = cursor.getInt(cursor.getColumnIndex("rf"));
					int read = cursor.getInt(cursor.getColumnIndex("read"));
					if(total == null)
						total = "0";
					meter.add(new WaterMeter(id,unit,address,floor,door,time,total,status,rf,read));
					break;
				default:
					String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
					meter.add(new WaterMeter(id,unit,address,floor,door,action_id));
					break;
			}
		}
		cursor.close();
		return meter;
	}
	public List<WaterMeter> getSmallListWaterForXML(){
		Cursor cursor = null;
		
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from tb_info where read=1", null);
		
		while(cursor.moveToNext()){
			WaterMeter wm = new WaterMeter(0);
			wm.id = cursor.getString(cursor.getColumnIndex("id"));
			wm.time = cursor.getString(cursor.getColumnIndex("time"));
			wm.total = cursor.getString(cursor.getColumnIndex("total"));
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			wm.action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			wm.action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			wm.ac_c = cursor.getString(cursor.getColumnIndex("ac_c"));
			wm.ac_z = cursor.getString(cursor.getColumnIndex("ac_z"));
			meter.add(wm);
		}
		cursor.close();
		return meter;
	}
	public List<WaterMeter> getListWaterForXML(){
		Cursor cursor = null;
		
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from tb_info ", null);
		while(cursor.moveToNext()){
			WaterMeter wm = new WaterMeter(0);
			wm.id = cursor.getString(cursor.getColumnIndex("id"));
			wm.unit = cursor.getString(cursor.getColumnIndex("area_id"));
			wm.address = cursor.getString(cursor.getColumnIndex("remarke"));
			wm.floor = cursor.getString(cursor.getColumnIndex("floor"));
			wm.door = cursor.getString(cursor.getColumnIndex("door"));
			wm.action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			meter.add(wm);
		}
		cursor.close();
		return meter;
	}
	public List<Configs> getListConfigsForXML(){		
		List<Configs> cgs = new ArrayList<Configs>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_config", null);
		
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			cg.type = cursor.getInt(cursor.getColumnIndex("type"));
			cg.address = cursor.getString(cursor.getColumnIndex("address"));
			if(cg.type == 0)
				cg.f_id = "0";
			else
				cg.f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			cg.u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			cg.action = cursor.getInt(cursor.getColumnIndex("action"));
			
			cgs.add(cg);
		}
		return cgs;
	}
	public void print_selectConfigs(){
		
		List<Configs> cgs = new ArrayList<Configs>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from tb_config", null);
		
		while(cursor.moveToNext()){
			Configs cg = new Configs();
			cg.id = cursor.getString(cursor.getColumnIndex("id"));
			cg.type = cursor.getInt(cursor.getColumnIndex("type"));
			cg.u_id = cursor.getString(cursor.getColumnIndex("tag"));
			cg.f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			Log.i("debug", cg.id +"," + cg.type +"," + cg.f_id);
			cgs.add(cg);
		}
	}
	/**
	 * 测试函数
	 * @param info
	 */
	public void print_Tb_info(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_info where id='1505100029'", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		area_id,remarke,floor,door
		*/
		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String read = cursor.getString(cursor.getColumnIndex("read"));
			
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			
			
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			
			Log.i("debug",total+","+ id+","+time+",read=" + read+",action_id=" + action_id +",status=" +status);
		}
	}
	
	public List<DMAMeter> getListBigMeter(){
		Cursor cursor;
		List<DMAMeter> biglist = new ArrayList<DMAMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from tb_dma", null);
		
		//id,address,gtime,status,bubbles,time,ptotal,ntotal,insflow,min,max,press,temp
		while(cursor.moveToNext()){
			DMAMeter bm = new DMAMeter();			
			bm.setID(cursor.getString(cursor.getColumnIndex("id")));
			bm.setAddr(cursor.getString(cursor.getColumnIndex("address")));
			bm.setGTime(cursor.getString(cursor.getColumnIndex("gtime")));
			bm.setStaus(cursor.getInt(cursor.getColumnIndex("status")));
			bm.setBubbles(cursor.getInt(cursor.getColumnIndex("bubbles")));
			bm.setTime(cursor.getString(cursor.getColumnIndex("time")));
			bm.setPTotal(cursor.getString(cursor.getColumnIndex("ptotal")));
			bm.setNTotal(cursor.getString(cursor.getColumnIndex("ntotal")));
			bm.setInsFlow(cursor.getFloat(cursor.getColumnIndex("insflow")));
			bm.setMax(cursor.getFloat(cursor.getColumnIndex("max")));
			bm.setMaxTm(cursor.getInt(cursor.getColumnIndex("maxtm")));
			bm.setMin(cursor.getFloat(cursor.getColumnIndex("min")));
			bm.setMinTm(cursor.getInt(cursor.getColumnIndex("mintm")));
			bm.setPress(cursor.getInt(cursor.getColumnIndex("press")));
			bm.setTemp(cursor.getFloat(cursor.getColumnIndex("temp")));
			if(bm.getID() != null)
				biglist.add(bm);
		}
		Log.i("debug","getCount:"+ cursor.getCount());
		cursor.close();
		return biglist;
	}
	
	public void print_area(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select area_id,count(*) from tb_info group by area_id", null);
		
		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){
			
			String area_id = cursor.getString(cursor.getColumnIndex("area_id"));
			
			Log.i("debug","area_id=" + area_id);
		}
	}

	/*
	 * 获取列表
	 * String id,  time, String total,int status,int rf
	 */
	public List<WaterMeter> getInitDataList(String info){
		Cursor cursor;
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		if(info.equals("0"))
			cursor = db.rawQuery("select * from tb_info", null);
		else
			cursor = db.rawQuery("select * from tb_info where floor=?", new String[]{info});
		
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			int rf = cursor.getInt(cursor.getColumnIndex("rf"));
			meter.add(new WaterMeter(id,time,total,status,rf));
		}
		cursor.close();
		return meter;
	}
	public List<WaterMeter> getDataList(Integer read,Integer area_id){
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		//Cursor cursor = db.rawQuery("select * from tb_info where read =? and area_id=?",new Object[]{read.toString(),area_id.toString()});
		Cursor cursor = db.rawQuery("select * from tb_info",null);
		
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			int rf = cursor.getInt(cursor.getColumnIndex("rf"));
			meter.add(new WaterMeter(id,time,total,status,rf));
		}
		cursor.close();
		return meter;
	}
	/**
	 * 数据上传后重置数据库表格
	 */
	public void reset_data(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		//db.execSQL("update tb_info set read=0");
		db.execSQL("update tb_info set floor = '2' ");
		db.execSQL("update tb_info set floor = '1' where keyid < 10");
		db.execSQL("update tb_info set floor = '3' where keyid > 25");
		
		//db.execSQL("update tb_info set read=0 where read ");
	}
	/**
	 * 清空水表数据
	 */
	public void clearWaterMeter(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			db.execSQL("delete from tb_info");
			db.execSQL("delete from tb_config");
			db.execSQL("delete from tb_big");
			db.execSQL("delete from tb_user");
			db.execSQL("delete from tb_dma");
		} catch (SQLException e) {
			Log.d("sql", "sql error when delete tables");
		}

	}
	public void getAutoCount(){
		//SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		//db.rawQuery("create index userindex on tb_info (id)", null);
		//db.rawQuery("insert into tb_info(id,read) values(?,1)", new String[]{"98"});
		
		 //db.rawQuery("delete from tb_info where id in (select id from tb_info GROUP by id having count(id)>1)", null);
		/*Cursor cursor = db.rawQuery("select count(*) from tb_info where id in (select id from tb_info GROUP by id having count(id)>1)", null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		Log.i("debug",result + "");*/
		Log.i("debug","asdfasf");
	}
	/**
	 * 获取记录总数
	 * @return
	 */
	public long getCount(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_info", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		area_id,remarke,floor,door
		*/
		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String keyid = cursor.getString(cursor.getColumnIndex("keyid"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String read = cursor.getString(cursor.getColumnIndex("read"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			
			Log.i("debug",keyid+","+ id+","+time+",read=" + read+",floor=" + floor);
		}
		
		return 1;
	}
	/*
	 *this.id = id;
	this.remarke = remarke;
	this.floor = floor;
	this.door = door;
	this.time = time;
	this.action_id = action_id;
	this.action = action;
	
	 */
	
	public String TestDebug(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToNext()){
			String mid = cursor.getString(cursor.getColumnIndex("id"));
			String remarke = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			String action = cursor.getString(cursor.getColumnIndex("action"));
			//return (new WaterMeter(mid,remarke,floor,door, action_id, action)).toString();
			cursor.close();
			return "tb_info [id=" + mid + "," + remarke + " " + floor
			+ " " + door + ", " + action_id + " " + action +"]";
		}
		cursor.close();
		return "空白";
	}
	/**
	 * 查询记录
	 * @param id 记录ID
	 * @return
	 
	public WaterMeter find(Integer action_id,Integer action){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from tb_info where action_id=? and action=?", new String[]{action_id.toString(),action.toString()});
		if(cursor.moveToFirst()){
			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			int amount = cursor.getInt(cursor.getColumnIndex("amount"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			return new Person(personid, name, phone, amount);
		}
		cursor.close();
		return null;
	}*/
	/**
	 * 分页获取记录
	 * @param offset 跳过前面多少条记录
	 * @param maxResult 每页获取多少条记录
	 * @return
	 */
	public List<WaterMeter> getScrollData(int offset, int maxResult){
		List<WaterMeter> persons = new ArrayList<WaterMeter>();
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from person order by personid asc limit ?,?",
//				new String[]{String.valueOf(offset), String.valueOf(maxResult)});
//		while(cursor.moveToNext()){
//			int personid = cursor.getInt(cursor.getColumnIndex("personid"));
//			int amount = cursor.getInt(cursor.getColumnIndex("amount"));
//			String name = cursor.getString(cursor.getColumnIndex("name"));
//			String phone = cursor.getString(cursor.getColumnIndex("phone"));
//			//persons.add(new WaterMeter(personid, name, phone, amount));
//		}
//		cursor.close();
		return persons;
	}
	/**
	 * 分页获取记录
	 * @param offset 跳过前面多少条记录
	 * @param maxResult 每页获取多少条记录
	 * @return
	 */
	public Cursor getCursorScrollData(int offset, int maxResult){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select personid as _id,name,phone,amount from person order by personid asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxResult)});
		return cursor;
	}
	public static final void Data_initFile() throws Exception{
		 File filex = new File(Environment.getExternalStorageDirectory(),"wecan");
		 if(!filex.exists()){
			 filex.mkdir();
		 }
		 filex = new File(Environment.getExternalStorageDirectory(),"wecan/small/upload");
		 if(!filex.exists()){
		 	filex.mkdir();
		 }
		 filex = new File(Environment.getExternalStorageDirectory(),"wecan/small/download");
		 if(!filex.exists()){
		 	filex.mkdir();
		 }
	}
	/**
	 * 数据库导出到XML
	 * @param type	操作人员类型
	 * @param flag	水表类型
	 * @param wms
	 * @param bms
	 * @param cgs
	 * @param users
	 * @param out
	 * @throws Exception
	 */
	public static void DB_ExportConfigToXML(List<WaterMeter> wms, List<Configs> cgs, OutputStream out) throws Exception{
		
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.text("\n"); 
		serializer.startTag(null, "data");
		serializer.text("\n"); 

		serializer.startTag(null, "tb_infos");
		serializer.text("\n"); 
		for(WaterMeter wm : wms){
			serializer.startTag(null, "tb_info");
			serializer.attribute(null, "id", wm.id);
			serializer.attribute(null, "unit", wm.unit);
			serializer.attribute(null, "address", wm.address);
			serializer.attribute(null, "floor", wm.floor);
			serializer.attribute(null, "door", wm.door);
			serializer.attribute(null, "action_id", wm.action_id);
			serializer.endTag(null, "tb_info");
			serializer.text("\n"); 
		}
		serializer.endTag(null, "tb_infos");
		serializer.text("\n"); 
		
		serializer.startTag(null, "tb_configs");
		serializer.text("\n"); 
		for(Configs cg : cgs){
			serializer.startTag(null, "tb_config");
			serializer.attribute(null, "id", cg.id);
			serializer.attribute(null, "address", cg.address);
			serializer.attribute(null, "type", cg.type +"");
			if (cg.f_id != null)
				serializer.attribute(null, "f_id", cg.f_id);
			else
				serializer.attribute(null, "f_id", "null");
			if (cg.u_id != null)
				serializer.attribute(null, "u_id", cg.u_id);
			else
				serializer.attribute(null, "u_id", "null");
			serializer.attribute(null, "action", Integer.toString(cg.action));
			serializer.endTag(null, "tb_config");
			serializer.text("\n"); 
		}
		serializer.endTag(null, "tb_configs");
		serializer.text("\n"); 
		
		serializer.endTag(null, "data");
		serializer.text("\n"); 
		serializer.endDocument();
		out.flush();
		out.close();
	}
	
	
	public static void DB_ExportMeterToXML(List<WaterMeter> wms, OutputStream out) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.text("\n"); 
		serializer.startTag(null, "data");
		serializer.text("\n"); 
		serializer.startTag(null, "tb_smalls");
		serializer.text("\n"); 
		for(WaterMeter wm : wms){
			serializer.startTag(null, "tb_small");
			serializer.attribute(null, "id", wm.id);
			serializer.attribute(null, "value", wm.total);
			serializer.attribute(null, "date", wm.time.substring(0, 10));
			serializer.attribute(null, "instant", Integer.toString(wm.rf));		// add by jtao
			serializer.attribute(null, "status", Integer.toString(wm.status));	// add by jtao
			serializer.endTag(null, "tb_small");
			serializer.text("\n"); 
		}
		serializer.endTag(null, "tb_smalls");
		serializer.text("\n"); 
		serializer.endTag(null, "data");
		serializer.text("\n"); 
		serializer.endDocument();
		out.flush();
		out.close();
	}
	
	//* @param type	操作人员类型
	//* @param flag	水表类型
	public int exportMeterData(){
		int bflag = 0;
		
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/upload/meter.xml");
		FileOutputStream outStream = null;

		try {
			outStream = new FileOutputStream(xmlFile);
		} catch (FileNotFoundException e) {
			bflag = 1;
			return bflag;
		}
		
		try {
			DB_ExportMeterToXML(getSmallListWaterForXML(), outStream);
				
		} catch (Exception e) {
			bflag = 2;
			return bflag;
		}
		
		return bflag;
		
	}
	
	public int exportConfigData() {
		int bflag = 0;
		
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/upload/data.xml");
		FileOutputStream outStream = null;

		try {
			outStream = new FileOutputStream(xmlFile);
		} catch (FileNotFoundException e) {
			bflag = 1;
			return bflag;
		}
		
		try {
			DB_ExportConfigToXML(getListWaterForXML(), getListConfigsForXML(), outStream);
				
		} catch (Exception e) {
			bflag = 2;
			return bflag;
		}
		
		return bflag;
	}
	
	public void initXMLSaveToDB(InputStream xml,int type) throws Exception{
		List<WaterMeter> wms = new ArrayList<WaterMeter>();
		List<DMAMeter>   bms = new ArrayList<DMAMeter>();
		List<Configs>    cgs = new ArrayList<Configs>();
		List<Users>      users = new ArrayList<Users>();
		
		WaterMeter  wm = null;
		DMAMeter 	bm = null;
		Users 		user  = null;
		Configs		cg  = null;
		
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				//单个类型判断
				if("tb_info".equals(pullParser.getName())){
					wm = new WaterMeter(type);
					wm.id = new String(pullParser.getAttributeValue(0)).trim();
					wm.unit = new String(pullParser.getAttributeValue(1)).trim();
					wm.address = new String(pullParser.getAttributeValue(2)).trim();
					wm.floor = new String(pullParser.getAttributeValue(3)).trim();
					wm.door = new String(pullParser.getAttributeValue(4)).trim();
					wm.action_id = new String(pullParser.getAttributeValue(5)).trim();
				}
				if("tb_big".equals(pullParser.getName())){
					bm = new DMAMeter();
					bm.setID(new String(pullParser.getAttributeValue(0)).trim());
					bm.setAddr(new String(pullParser.getAttributeValue(1)).trim());
					/**
					 *  Id       大表编号
						Type     0，抄表人员，1稽查人员
						gTime    采集时间
						Time    大表数据时间
						Total    累计流量（小数点后三位）
						Status   水表状态(0-7)
						Min     最小瞬时流量
						Max    最大瞬时流量
						Value   压力值（数组）
					 */
				}
				if("tb_user".equals(pullParser.getName())){
					user = new Users();
					user.id = new String(pullParser.getAttributeValue(0)).trim();
					user.pwd = new String(pullParser.getAttributeValue(1)).trim();
					user.type = new Integer(pullParser.getAttributeValue(2));
					
				}
				if("tb_config".equals(pullParser.getName())){
					cg = new Configs();
					cg.id = new String(pullParser.getAttributeValue(0)).trim();
					cg.address = new String(pullParser.getAttributeValue(1)).trim();
					cg.type = Integer.valueOf(pullParser.getAttributeValue(2));
					cg.f_id = new String(pullParser.getAttributeValue(3)).trim();
					if ("null".equals(cg.f_id))
						cg.f_id = null;
					cg.u_id = new String(pullParser.getAttributeValue(4)).trim();
					if ("null".equals(cg.u_id))
						cg.f_id = null;
					cg.action = Integer.parseInt(pullParser.getAttributeValue(5));
					
				}
				break;
				
			case XmlPullParser.END_TAG:
				if("tb_info".equals(pullParser.getName())){
					wms.add(wm);
					wm = null;
				}
				if("tb_big".equals(pullParser.getName())){
					bms.add(bm);
					bm = null;
				}
				if("tb_config".equals(pullParser.getName())){
					cgs.add(cg);
					cg = null;
				}
				if("tb_user".equals(pullParser.getName())){
					users.add(user);
					user = null;
				}
				
				break;
			}
			event = pullParser.next();
		}
		
		switch(type){
			case 0://抄表人员
				if(wms.size() > 0)
					saveWaterMeters(wms);
				
				Log.i("debug", bms.size()+"List " + wms.size());
				if(bms.size() > 0){
					saveBigMeters(bms,type);
				}
				
				if(cgs.size() > 0){
					ServiceInstall svinstall = new ServiceInstall(context);
					svinstall.insertMeters(cgs);
				}
				break;
			case 1://稽查人员
			case 2://安装人员
				ServiceInstall svinstall = new ServiceInstall(context);
				if(wms.size() > 0){
					svinstall.insertWaterMeters(wms);
					svinstall.print_info();
				}
				
				if(cgs.size() > 0){
					
					svinstall.insertMeters(cgs);
				}
				
				if(bms.size() > 0){
					saveBigMeters(bms,type);
				}
				break;
			default:
				
				break;
		}
		if(users.size() > 0)
			saveUsers(users);
		Log.i("debug", "List " + wms.size());
		//saveConfigs(cgs);
		//selectConfigs();
		//Log.i("debug", "selectConfigs " + );
		//File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/upload/updata.xml");
		//FileOutputStream outStream = new FileOutputStream(xmlFile);
		///Data_SaveToXML(wms,bms,cgs,users,outStream);
		//return wmList;
	}
	
	/**
	 * 初始化数据
	 * @param type 0 抄表人员，1 稽查人员，2 安装人员
	 * @return 1文件打开失败      2文件读取失败
	 */
	public int initXMLData(int type){
		int bflag = 0;
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/download/data.xml");
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			bflag = 1;
		}
		//Data_SaveToDB(inStream);
		try {
			initXMLSaveToDB(inStream,type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bflag = 2;
		}
		return bflag;
	}
	public List<String> GetFileInfo(){
		List<String> strs = new ArrayList<String>();
		try {
            CheckedInputStream cis = null;
            long fileSize = 0;
            File file = new File(Environment.getExternalStorageDirectory(), "wecan/back/collector.bin");
            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(file), new CRC32());
 
                fileSize = file.length();
                
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
                System.exit(1);
            }
 
            byte[] buf = new byte[128];
            //int index = 0;
            String ret = "";
            while(cis.read(buf) >= 0) {
            	ret = "";
            	for (int i = 0; i < buf.length; i++) {
        			String hex = Integer.toHexString(buf[i] & 0xFF);
        			if (hex.length() == 1) {
        				hex = "0" + hex;
        			}
        			ret += hex.toUpperCase();
        		}
            	//buf = null;
            	strs.add(ret);
            	//Log.i("debug", index + " " + ret);
            	//index ++ ;
            }
 
            long checksum = cis.getChecksum().getValue();
            ret = NumToStr(checksum) + NumToStr(fileSize);
            strs.add(ret);
            //System.out.println(checksum + " " + fileSize + " " + fileName);
            Log.i("debug", "Len:" + ret);
            //3034966787 30732 /storage/sdcard0/wecan/back/collector.bin
 
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
		return strs;
	}
	public String GetFileCheckNum(){
		String str = "";
		try {
            CheckedInputStream cis = null;
            long fileSize = 0;
            File file = new File(Environment.getExternalStorageDirectory(), "wecan/back/collector.bin");
            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(file), new CRC32());
 
                fileSize = file.length();
                
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
                System.exit(1);
            }
 
            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {
            }
 
            long checksum = cis.getChecksum().getValue();
            str = NumToStr(checksum);
          
            //System.out.println(checksum + " " + fileSize + " " + fileName);
            Log.i("debug", "Len:" + str);
            //3034966787 30732 /storage/sdcard0/wecan/back/collector.bin
 
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
		return str;
	}
	public String NumToStr(long size){
		String str = "";
		str = String.format("%08X", size);
		str = str.substring(6,8) + str.substring(4,6) + str.substring(2,4) + str.substring(0,2);
		return str;
	}
}
