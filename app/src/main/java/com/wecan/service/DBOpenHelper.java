package com.wecan.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建wecan_small.db表
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "wecan_small.db", null, 3);//<包>/databases/
	}
	/* tb_info 数据表格主要存放水表详细信息
	 * 
	 * id, time,total,status,rf,read=1//数据采集用到的字段
	 * keyid 表主键自增长
	 * id		水表ID（表号）
	 * remake	备注（如哪一栋楼）
	 * floor	楼层号
	 * door		门牌号
	 * time		采集时间
	 * action	检测action_id是否能接收
	 * total	总流量
	 * status	水表状态
	 * rf		RF状态
	 * read		采集状态（上传后自动复位为0）
	 * area_id	区域id，小区编号
	 * 
	 * action_type;//设置的类型     0 集中器 1 采集器 2 中继器
	 * action_id;
	public String ac_c;
	public String ac_z;
	public int    tag = 0;
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	
	/**
	 * tb_config 终端配置信息
	 *  Id        终端编号（集中器、采集器、中继器）
		Type      类型 集中器，采集器，中继器 
		U_id      替换的终端编号
		F_id      父类终端编号（无父类为0）
		action    0添加，1替换，2删除
	 */
	/**
	 *  Id       大表编号
	 *  address  地址
		Type     0，抄表人员，1稽查人员
		gTime    采集时间
		Time    大表数据时间
		Total    累计流量（小数点后三位）
		Status   水表状态(0-7)
		Min     最小瞬时流量
		Max    最大瞬时流量
		Value   压力值（数组）
		read    是否读取
	 */

	@Override
	public void onCreate(SQLiteDatabase db) {//是在数据库每一次被创建的时候调用的
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_info(keyid integer primary key autoincrement, id,area_id,remarke,floor,door,time," +
				"action,total,status,rf,read,action_type,action_id,ac_c,ac_z,tag)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_config(keyid integer primary key autoincrement,id,type,u_id,f_id,action,tag,address)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_big(keyid integer primary key autoincrement,id,address,type,gtime,time,total,status,min,max,value,read)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_user(keyid integer primary key autoincrement,id,pwd,type)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_err(keyid integer primary key autoincrement,id,img,dsc,time)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_dma(keyid integer primary key autoincrement,id,address,gtime,status,bubbles,time,ptotal,ntotal,insflow,max,maxtm,min,mintm,press,temp)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_dma(keyid integer primary key autoincrement,id,address,gtime,status,bubbles,time,ptotal,ntotal,insflow,max,maxtm,min,mintm,press,temp)");
	}

}
