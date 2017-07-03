package com.wecan.install;

import com.wecan.domain.Configs;
import com.wecan.domain.WaterMeter;

public class InstallListView {
	public String address;
	public String id;
	public String action_id;
	public String f_id;
	public int type = 0;
	public int action_type = 0;
	public boolean flag = false;//选中判断
	public boolean bflag = false;
	
	public InstallListView(WaterMeter wm){
		this.address = wm.unit +" " + wm.address + " " + wm.floor + "-" + wm.door;
		this.id = wm.id;
		this.action_type = wm.action_type;
		this.type = wm.tag;
		this.action_id = wm.action_id;
	}

	public InstallListView(Configs cg, int i) {
		// TODO Auto-generated constructor stub
		this.action_type = cg.type;
		this.id = cg.id;
		this.type = cg.tag;
		this.bflag = true;
		this.address = cg.address;
		this.f_id = cg.f_id ;
	}
}