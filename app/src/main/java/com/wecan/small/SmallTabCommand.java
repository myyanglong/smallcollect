package com.wecan.small;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.domain.PreferencesService;
import com.wecan.service.WaterMeterService;

/**
 * 小表采集/数据同步界面
 */
public class SmallTabCommand extends Activity implements OnClickListener{
	private LinearLayout ly_command_init,ly_command_export_data,ly_command_export_meter;
	private PreferencesService preservice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_tab_command);
		
		findView();
		initdata();
	}
	private void findView() {
		// TODO Auto-generated method stub
		ly_command_export_data = ((LinearLayout) findViewById(R.id.ly_command_export_data));
		ly_command_export_meter = ((LinearLayout) findViewById(R.id.ly_command_export_meter));
		ly_command_init = ((LinearLayout) findViewById(R.id.ly_command_init));
	}
	private void initdata() {
		// TODO Auto-generated method stub
		ly_command_export_data.setOnClickListener(this);
		ly_command_init.setOnClickListener(this);
		ly_command_export_meter.setOnClickListener(this);
		
		preservice = new PreferencesService(this);
			
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		WaterMeterService service = new WaterMeterService(this);
		switch(v.getId()){
		case R.id.ly_command_export_data:
			if(service.exportConfigData() == 0)
				Toast.makeText(this,
			    		"文件生成成功,请查看目录\nwecan\\small\\upload\\data.xml"
		    			, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this,
			    		"文件生成失败！"
		    			, Toast.LENGTH_LONG).show();
			break;
			
			case R.id.ly_command_export_meter:
				if(service.exportMeterData() == 0)
					Toast.makeText(this,
				    		"文件生成成功,请查看目录\nwecan\\small\\upload\\meter.xml"
			    			, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this,
				    		"文件生成失败！"
			    			, Toast.LENGTH_LONG).show();
				break;
			
			case R.id.ly_command_init:
				service.clearWaterMeter();
				switch(service.initXMLData(preservice.getUserType())){
					case 0:
						Toast.makeText(this,
					    		"初始化成功"
				    			, Toast.LENGTH_LONG).show();
						break;
					case 1:
						Toast.makeText(this,
					    		"打开文件失败，请先下载数据"
				    			, Toast.LENGTH_LONG).show();
						break;
					default:
						Toast.makeText(this,
					    		"读取数据失败，请重新下载数据"
				    			, Toast.LENGTH_LONG).show();
						break;
				}
				break;
		}
	}
}
