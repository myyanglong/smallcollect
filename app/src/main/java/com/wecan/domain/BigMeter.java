package com.wecan.domain;

public class BigMeter {
/**
 * Id       大表编号
Type     0，下载，1安装
gTime    采集时间
Time    大表数据时间
Total    累计流量（小数点后三位）
Status   水表状态(0-7)
Min     最小瞬时流量
Max    最大瞬时流量
Value   压力值（数组）
 */
	public String id;
	public String address;
	public String gtime;
	public String time;
	public int	  type;
	public int	  status;
	public String total;
	public String max;
	public String min;
	public String value;
	public int read=0;
	public BigMeter(){
		this.read = 1;
	}
	public BigMeter(int type) {
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	public BigMeter(String id, String address, int type, String gtime, String time, String total, int status, 
			String min, String max, String value, int read){
		this.type = type;
		this.id = id;
		this.address = address;
		this.gtime = gtime;
		this.time = time;
		this.total = total;
		this.status = status;
		this.max = max;
		this.min = min;
		this.value = value;
		this.read =	read;
	}
	public BigMeter(String id, String address) {
		this.id = id;
		this.address = address;
		// TODO Auto-generated constructor stub
	}
}
