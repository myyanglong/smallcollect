package com.wecan.domain;

import java.util.ArrayList;
import java.util.List;

import com.wecan.install.InstallListView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IntentInfo extends Intent {
	private Context context;
	public String temp = null;
	public IntentInfo(String str){
		temp = "01" + str;
		temp = "685741" + String.format("%s%02X",temp,getNum(HexString2Bytes(temp),temp.length()/2))  + "1516";
	}
	public IntentInfo(int index,String str){
		setAction("com.weian.activity.BROAD_CAST");
		switch(index){
			case 0:
				//小表空闲模式
				//putExtra("rev","68574100001516");
			case 1:
				putExtra("rev","685741" + str + "1516");
				//putExtra("rev","685741020004" + "000101020A" + "1516");
				break;
			default:
				putExtra("rev",str);
				break;
		}
		
	}
	/**
	 * 发送模式
	 * @param str1	02%s0001
	 * @param str2	内容
	 */
	public IntentInfo(String str1,String str2){
		setAction("com.weian.activity.BROAD_CAST");
		String str = String.format("%s%02X%s", str1,str2.length()/2 + 3 ,strToPDAstr(str2,"00000004",0,true));
		str = String.format("%s%02X",str,getNum(HexString2Bytes(str),str.length()/2));	
		str = "685741" + str + "1516";
		putExtra("rev",str);
	}
	/**
	 * 唤醒模式
	 * @param str1	信道、信道组 0001
	 * @param str2	源地址
	 * @param str3	目的地址
	 */
	public IntentInfo(String str1,String str2,String str3){
		setAction("com.weian.activity.BROAD_CAST");
		
		String str = "685741" + strAddNum("03" + str1 + str2 + str3) + "1516";
		putExtra("rev",str);
		//putExtra(str, null);
	}
	//List<String> buildlist = new ArrayList<String>();
	//buildlist.add("全部楼栋");
	/**
	 * 列表发送数据（用于发送设置的设备或水表号）
	 * @param context
	 * @param list
	 * @param temp
	 * @param type
	 */
	//List<InstallListView> list = new ArrayList<InstallListView>();
	public IntentInfo(Context context,List<InstallListView> list,String temp,int command,boolean flag){
		String strlist = "";
		String gc = String.format("02%s0001",new PreferencesService(context).getGrpChnl(true));
		temp = String.format("%08X", Integer.parseInt(temp));
		setAction("com.weian.activity.BROAD_CAST");
		
		for(InstallListView ob:list){
			//str = String.format("%08X", Integer.parseInt(str));
			String str;
			if(flag)
				str = "01";//String.format("%02d", ob.action_type);
			else
				str = "02";
			if(ob.bflag)
				str = "03";
			str = str + strToBCD(ob.id);
			strlist = strlist + initSendData(str,gc,temp,command,false)  + ",";
		}
		strlist = strlist + initSendData("FF",gc,temp,command,false);
		
		putExtra("list",strlist);
		//putExtra(str, null);
	}
	/**
	 * 发送数据
	 * @param bflag   	True 唤醒， False 发送数据 
	 * @param context   上下文对象
	 * @param content	发送内容
	 * @param temp		发送目的地址
	 * @param type		控制指令0-7
	 */
	public IntentInfo(Context context,String content,String temp,int type,boolean bflag) {
		String gc;
		setAction("com.weian.activity.BROAD_CAST");
		this.context = context;
		
		if(bflag)
			gc = String.format("03%s",new PreferencesService(context).getGrpChnl(true));
		else
			gc = String.format("02%s0001",new PreferencesService(context).getGrpChnl(true));
		/*
		String str = strToPDAstr(content,temp,type,bflag);
		//0CE0 01 0000 0000 ED 8
		str = String.format("%s%s",gc,str);
		str = String.format("685741%s1516",strAddNum(str));*/
		putExtra("rev",initSendData(content,gc,temp,type,bflag));
	}
	public IntentInfo(String content,String temp,int type,boolean flag) {
		String str = null;
		setAction("com.weian.activity.BROAD_CAST");
		if(flag)
			str = "AC0D";
		else
			str = "F055";
		switch(type){
			case 0://空闲模式
				//B30703E073021DB80BF23B
				str = strAddNum("B30100") + "3B";
				break;
			case 2:
				content = "50" + getIDFromInt(temp,true) + "00000000" + content;
				str = strAddNum(String.format("B3%02X%02XE073021D%s%s",content.length()/2 + 7,type,str,content)) + "3B";
				break;
			case 1:
				//B30701E012271DAC0DAA3B
				str = strAddNum(String.format("B307%02XE073021D%s",type,str)) + "3B";
				break;
			case 3://唤醒
				str = strAddNum(String.format("B307%02XE073021D%s",type,str)) + "3B";
				break;
			default:
				break;
		}
		putExtra("rev",str);
	}
	/**
	 * 发送写配置的01
	 * @param num_1 自身的水表数目
	 * @param num_2 中级负责的水表数目
	 * @param num_3 中继数目
	 * @param temp 目的地址
	 */
	public IntentInfo(int num_1,int num_2,int num_3,String temp) {
		String str = null;
		setAction("com.weian.activity.BROAD_CAST");
		
		str = String.format("50%s000000004101%s",getIDFromInt(temp,true), getIDFromInt(num_1+"",false) + getIDFromInt(num_2+"",false) + getIDFromInt(num_3+"",false));
		str = strAddNum(String.format("B3%02X02E073021DF055%s",str.length()/2 + 7,str)) + "3B";
		putExtra("rev",str);
	}
	public IntentInfo(String temp,boolean bfalg) {
		String str = null;
		setAction("com.weian.activity.BROAD_CAST");
		
		str = String.format("50%s000000004104",getIDFromInt(temp,true));
		str = strAddNum(String.format("B3%02X02E073021DF055%s",str.length()/2 + 7,str)) + "3B";
		putExtra("rev",str);
	}
	/**
	 * 发送配置的02、03
	 * @param il
	 * @param temp
	 * @param index
	 */
	public IntentInfo(InstallListView il,String temp,int index){
		String str = "";
		int mode = 2;
		setAction("com.weian.activity.BROAD_CAST");

		if(!il.bflag)//中继器
			mode = 3;
		str = String.format("50%s0000000041%02x%s",getIDFromInt(temp,true),mode,getIDFromInt(index + "",false) + getIDFromInt(il.id,true));
		str = strAddNum(String.format("B3%02X02E073021DF055%s",str.length()/2 + 7,str)) + "3B";
		putExtra("list",str);
	}
	public String getIDFromInt(String str,boolean flag){
		String temp = ""; 
		Log.i("debug", str + " " + flag);
		if(flag)
			str = String.format("%08X",Long.parseLong(str));
		else
			str = String.format("%04X",Long.parseLong(str));
		
		for(int i=0;i< str.length()/2;i++){
			temp = str.substring(i*2,(i+1)*2) + temp;
		}
		return temp;
	}
	public String initDoubleSendData(String content,String gc,String temp,int type,boolean flag){
		String str = null;
		switch(type){
			case 0://空闲模式
				//B30703E073021DB80BF23B
				str = strAddNum("B30100") + "3B";
				break;
			case 2:
				temp = String.format("%08X", Integer.parseInt(temp));
				temp = temp.substring(6,8) + temp.substring(4,6) + temp.substring(2,4) + temp.substring(0,2);
				content = content + temp;
				//B30703E073021DB80BF23B
				//B30D02E073021DB80B5720240000001D3B
				if(flag) 
					//3000
					str = strAddNum(String.format("B3%02X%02XE073021DB80B%s",content.length()/2 + 7,type,content)) + "3B";
				else//3500
					str = strAddNum(String.format("B3%02X%02X00000000AC0D%s",content.length() + 7,type,content)) + "3B";
				break;
			case 1:
				//B30701E012271DAC0DAA3B
			case 3://唤醒
				if(flag)
					//3000
					str = strAddNum(String.format("B307%02XE073021DB80B",type)) + "3B";
				else//3500
					str = strAddNum(String.format("B307%02XE012271DAC0D",type)) + "3B";
				break;
			default:
				break;
		}
		Log.i("debug", str);
		return str;
	}
	public String initSendData(String content,String gc,String temp,int type,boolean flag){
		temp = String.format("%08X", Integer.parseInt(temp));
		temp = temp.substring(6,8) + temp.substring(4,6) + temp.substring(2,4) + temp.substring(0,2);
		return String.format("685741%s1516",strAddNum(String.format("%s%s",gc,strToPDAstr(content,temp,type,flag))));
	}
	public String strToBCD(String temp){ 
		String str = String.format("%08X", Integer.parseInt(temp));
		return str.substring(6, 8) + str.substring(4, 6)+ str.substring(2, 4) + str.substring(0, 2);
	}
	public String strToPDAstr(String str,String send,int type,boolean bflag){
		if(type == 2 || type == 12){
			if(type == 12){
				str = "02" + strToBCD(str);
				type = 2;
			}
			else
				str = "01" + strToBCD(str);
		}
		String ret = String.format("E%X%s00000000%s",type,send,str);
		ret = String.format("%02X%s",ret.length()/2 + 1,ret);
		if(bflag)
			return String.format("%02X%s",ret.length()/2 + 1,strAddNum(ret));
		else
			return strAddNum(ret);
	}
	private byte getNum(final byte[] buffer, final int size){
		byte num = 0;
		for(int i= 0;i<size;i++){
			num +=buffer[i];
		}
		return num;
	}
	public String strAddNum(String str){
		byte[] temp = HexString2Bytes(str);
		String ret = String.format("%s%02X",str, getNum(temp,temp.length));
		//Log.i("debug", "strAddNum:"+str+","+ temp.length + "," + str.length()/2);
		return ret;
	}
	public static String Bytes2HexString(byte[] b, int size) {
		String ret = "";
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		_b0 = (byte)(_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		byte ret = (byte)(_b0 ^ _b1);
		return ret;
	}
	public static byte[] HexString2Bytes(String src){
		int len = src.length()/2;
	    byte[] ret = new byte[len];
	    byte[] tmp = src.getBytes();

	    for(int i=0; i<len; i++){
	      ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
	    }
	    return ret;
	}
	public String StrIntentInfo(String temp,List<InstallListView> list,String id,int begin,int mode){
		String str = "";
		int index = begin;
		for(InstallListView il:list){
			str = String.format("50%s0000000041%02x%s",id,mode,getIDFromInt(index + "",false) + getIDFromInt(il.id,true));
			temp = temp + "," + strAddNum(String.format("B3%02X02E073021DF055%s",str.length()/2 + 7,str)) + "3B";
			index++;
		}
		return temp;
	}
	//双工协议
	public IntentInfo(String temp,int cmd){
		setAction("com.weian.activity.BROAD_CAST");
		putExtra("cmd",cmd);
		putExtra("temp",temp);
	}
	public IntentInfo(String temp,String id,int cmd){
		setAction("com.weian.activity.BROAD_CAST");
		putExtra("cmd",cmd);
		putExtra("temp",temp);
		putExtra("bigid",id);
	}
	/**
	 * 写配置函数，cmd为1  list为指令列表
	 * @param temp  目的地址
	 * @param water 水表自身水表号
	 * @param miss  中继器水表号
	 * @param meter 中继器
	 */
	public IntentInfo(String temp,int style,List<InstallListView> water,List<InstallListView> miss,List<InstallListView> meter){
		String str=null,sendid = getIDFromInt(temp,true);
		setAction("com.weian.activity.BROAD_CAST");
		putExtra("cmd",1);
		putExtra("temp",temp);
		if(style != 0)
			str = String.format("50%s000000004101%s",sendid, getIDFromInt(water.size()+"",false) + getIDFromInt(miss.size()+"",false) + getIDFromInt(meter.size()+"",false));
		else
			str = String.format("50%s000000004101%s",sendid, getIDFromInt(water.size()+"",false) + "0000" + getIDFromInt(meter.size()+"",false));
		str = strAddNum(String.format("B3%02X02E073021DF055%s",str.length()/2 + 7,str)) + "3B";
		str = StrIntentInfo(str,meter,sendid,0,2);
		str = StrIntentInfo(str,water,sendid,0,3);
		if(style != 0)
			str = StrIntentInfo(str,miss,sendid,water.size(),3);
		String str_end = String.format("50%s000000004104",getIDFromInt(temp,true));
		str =str + "," + strAddNum(String.format("B3%02X02E073021DF055%s",str_end.length()/2 + 7,str_end)) + "3B";
		putExtra("list",str);
	}
}
