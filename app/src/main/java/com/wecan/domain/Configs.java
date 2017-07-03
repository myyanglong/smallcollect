package com.wecan.domain;

import android.database.sqlite.SQLiteDatabase;

/**
 * tb_config 终端配置信息
 *  Id        终端编号（0.集中器、1.采集器、2.中继器、3.虚拟设备）
	Type      0添加，1替换，2删除
	U_id      替换的终端编号
	F_id      父类终端编号（无父类为'0'）
 */

public class Configs {
	
	public String id;
	public int type;
	public int tag = 0;
	public int action = 0;
	public String u_id;
	public String f_id;
	public String address;
	
	public Configs(){}
	
	public Configs(String id,int type,String u_id,String f_id){
		this.id = id;
		this.type = type;
		this.u_id = u_id;
		this.f_id = f_id;
	}
	
}
