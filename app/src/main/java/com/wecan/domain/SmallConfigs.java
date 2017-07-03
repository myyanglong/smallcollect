package com.wecan.domain;

public class SmallConfigs {
	public String id;
	public String address;
	public int meterCnt = 0;
	
	
	public SmallConfigs(){}
	
	public SmallConfigs(String id, String address, int meterCnt){
		this.id = id;
		this.address = address;
		this.meterCnt = meterCnt;
	}
}
