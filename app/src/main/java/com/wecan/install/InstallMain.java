package com.wecan.install;


import com.wecan.smallcollect.R;
import com.wecan.domain.ConfigAdapter;
import com.wecan.domain.Configs;
import com.wecan.domain.DMAMeter;
import com.wecan.domain.WaterMeter;
import com.wecan.service.WaterMeterService;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InstallMain extends Activity implements OnClickListener,OnItemClickListener,OnItemLongClickListener{
    
    //private String TAG_ARROW = "service_arrow";
    //private String TAG_ITEM = "service_item";
    
    private RelativeLayout s_back,s_add;
    private ListView s_list;
    private ConfigAdapter adapter;
    public Configs cgtemp = new Configs();
    
    private WaterMeterService service = new WaterMeterService(this);
    
    private PopupWindow popupMater ;
    private LinearLayout s_layout_add, s_layout_addwater, s_layout_addbigwater, s_layout_exp_data,s_layout_init,s_layout_exp_meter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_layout);
        findViews() ;
        initViews() ;
        initPopwindow() ;
    }
    

    private void findViews() {
        
    	s_list = (ListView) findViewById(R.id.s_list);
        s_back = (RelativeLayout) findViewById(R.id.s_back);
        s_add = (RelativeLayout) findViewById(R.id.s_add);
    }

    
    private void initViews() {
    	adapter = new ConfigAdapter(this,service.selectConfigs(3));
    	s_list.setAdapter(adapter);
        s_list.setOnItemClickListener(this);
        s_list.setOnItemLongClickListener(this);
       
        s_back.setOnClickListener(this);
        s_add.setOnClickListener(this);
    }
    private void initPopwindow() {
		// TODO Auto-generated method stub
		View contentView ;
		contentView = getLayoutInflater().inflate(R.layout.s_layout_popwindow, null);
		
		popupMater = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		popupMater.setFocusable(true);//取得焦点
		popupMater.setBackgroundDrawable(new BitmapDrawable());
		popupMater.setAnimationStyle(R.style.animation);
		
		s_layout_add = (LinearLayout)contentView.findViewById(R.id.s_layout_add);
		s_layout_add.setOnClickListener(this);
		
		s_layout_addwater = (LinearLayout)contentView.findViewById(R.id.s_layout_addwater);
		s_layout_addwater.setOnClickListener(this);
		
		s_layout_addbigwater = (LinearLayout)contentView.findViewById(R.id.s_layout_addbigwater);
		s_layout_addbigwater.setOnClickListener(this);
		
		s_layout_init = (LinearLayout)contentView.findViewById(R.id.s_layout_init);
		s_layout_init.setOnClickListener(this);
		
		s_layout_exp_data = (LinearLayout)contentView.findViewById(R.id.s_layout_exp_data);
		s_layout_exp_data.setOnClickListener(this);
		
		s_layout_exp_meter = (LinearLayout)contentView.findViewById(R.id.s_layout_exp_meter);
		s_layout_exp_meter.setOnClickListener(this);
		
	}
    public void updateListView(){
    	s_list.setAdapter(new ConfigAdapter(this,service.selectConfigs(3)));
    }
   
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.s_back:
				this.finish();
				break;
			case R.id.s_add:
				//this.finish();
				popupMater.showAtLocation(this.findViewById(R.id.s_layout), Gravity.BOTTOM, 0, 0);
				break;
			case R.id.s_layout_addwater:
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				
				final Context ctx = this;
				
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(R.layout.dialog, null); 
				
				final TextView tmpTextView = (TextView) textEntryView.findViewById(R.id.textView1); 
		        tmpTextView.setVisibility(8);		// Invisible
				
		        final EditText editTextAddress = (EditText) textEntryView.findViewById(R.id.editTextName); 
		        editTextAddress.setVisibility(8);		// Invisible
		        
		        final TextView tmpIdView = (TextView) textEntryView.findViewById(R.id.s_id); 
		        tmpIdView.setText("临时表号：");
		        
		        final EditText editTextNum = (EditText)textEntryView.findViewById(R.id.editTextNum);
		        editTextNum.setKeyListener(new DigitsKeyListener(false,true));
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("请输入10位表号").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
		                .setNegativeButton("取消", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	WaterMeter  wm = new WaterMeter(1);
					   
		            	wm.id = editTextNum.getText().toString();
		            	wm.unit = "临时区";
		            	wm.address = "1栋";
						wm.floor = "1";
						wm.door = wm.id;
						wm.read = 0;
						wm.action_id = "0";
					 
						if ((wm.id == null) || wm.id.length() != 10) {
							Toast.makeText(ctx, "非法表号，添加失败！"	, Toast.LENGTH_LONG).show();
						} else if (service.select_data(wm.id) != null) {
							Toast.makeText(ctx, "表号已存在，添加失败！"	, Toast.LENGTH_LONG).show();
						} else {
							service.add_water(wm);
							Toast.makeText(ctx, "临时表号添加成功！"	, Toast.LENGTH_LONG).show();
						}
		            }
		        });
		        
		        builder.show();
				break;
			case R.id.s_layout_addbigwater:
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				
				final Context ctx0 = this;
				
				factory = LayoutInflater.from(this);
				final View textEntryView0 = factory.inflate(R.layout.dialog, null); 
				((TextView)textEntryView0.findViewById(R.id.textView1)).setVisibility(8);		// Invisible
		        ((EditText)textEntryView0.findViewById(R.id.editTextName)).setVisibility(8);		// Invisible
		 
		        ((TextView)textEntryView0.findViewById(R.id.s_id)).setText("临时表号：");		      
		        ((EditText)textEntryView0.findViewById(R.id.editTextNum)).setKeyListener(new DigitsKeyListener(false,true));
		        
		        builder = new AlertDialog.Builder(this);
		        builder.setTitle("请输入10位表号").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView0)
		                .setNegativeButton("取消", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	DMAMeter bm = new DMAMeter();
						bm.setID(((EditText)textEntryView0.findViewById(R.id.editTextNum)).getText().toString());
						bm.setAddr("临时区");
						if ((bm.getID() == null) || bm.getID().length() != 10) {
							Toast.makeText(ctx0, "非法表号，添加失败！"	, Toast.LENGTH_LONG).show();
						} else if (service.select_bigdata(bm.getID()) != null) {
							Toast.makeText(ctx0, "表号已存在，添加失败！"	, Toast.LENGTH_LONG).show();
						} else {
							service.add_bigwater(bm);
							Toast.makeText(ctx0, "临时表号添加成功！"	, Toast.LENGTH_LONG).show();
						}
		            }
		        });
		        
		        builder.show();
				break;
			case R.id.s_layout_add:
				if(popupMater.isShowing()) 
					popupMater.dismiss();
//				final EditText inputServer = new EditText(this);
//				inputServer.setKeyListener(new DigitsKeyListener(false,true));
				factory = LayoutInflater.from(this);
				final View textEntryView1 = factory.inflate(R.layout.dialog, null); 
		        final EditText editTextAddress1 = (EditText) textEntryView1.findViewById(R.id.editTextName); 
		        final EditText editTextNum1 = (EditText)textEntryView1.findViewById(R.id.editTextNum);
		        editTextNum1.setKeyListener(new DigitsKeyListener(false,true));
		        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		        builder1.setTitle("请输入设备信息").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView1)
		                .setNegativeButton("取消", null);
		        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		               //Log.i("debug","Input " + inputServer.getText().toString());
		               cgtemp.id = editTextNum1.getText().toString();
		               cgtemp.address = editTextAddress1.getText().toString();
		               cgtemp.type = 3;		// 虚拟设备
		               if(service.addConfigs(cgtemp))
		            	   updateListView();
		               else
		            	   alertdialog();
		             }
		        });
		        
		        builder1.show();
				break;
			case R.id.s_layout_exp_data:
				if(service.exportConfigData() == 0)
					Toast.makeText(this,
				    		"文件生成成功,请查看目录\nwecan\\small\\upload\\data.xml"
			    			, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this,
				    		"文件生成失败！"
			    			, Toast.LENGTH_LONG).show();
				KeyLock();
				break;
			case R.id.s_layout_exp_meter:
				if(service.exportMeterData() == 0)
					Toast.makeText(this,
				    		"文件生成成功,请查看目录\nwecan\\small\\upload\\meter.xml"
			    			, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this,
				    		"文件生成失败！"
			    			, Toast.LENGTH_LONG).show();
				KeyLock();
				break;
			case R.id.s_layout_init:
				KeyLock();
				service.clearWaterMeter();
				switch(service.initXMLData(2)){
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
				if(popupMater.isShowing()) 
					popupMater.dismiss();
				updateListView();
				break;
			default:
				break;
				
		}
		unKeyLock();
	}
	public void KeyLock() {
		s_layout_exp_data.setClickable(false);
		s_layout_exp_meter.setClickable(false);
		s_layout_init.setClickable(false);
	}
	public void unKeyLock() {
		s_layout_exp_data.setClickable(true);
		s_layout_exp_meter.setClickable(true);
		s_layout_init.setClickable(true);
	}
    @Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int index, long id) {
		// TODO Auto-generated method stub
    	cgtemp =(Configs) adapter.getAdapter().getItem(index);
		
		Log.i("debug","onItemLongClick "+ cgtemp.f_id);
		
		new AlertDialog.Builder(this)
			.setTitle("设备删除")
			.setIcon(null)
			.setCancelable(false)
			.setMessage("确定删除设备"+cgtemp.id+"？")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							service.deleteConfigs(cgtemp.id);
							updateListView();
						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							dialog.dismiss();
						}
					}).create().show();
		
		return true;
	}
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int index, long id) {
		// TODO Auto-generated method stub
		cgtemp =(Configs) adapter.getAdapter().getItem(index);
		Intent intent = new Intent(this, InstallSingle.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
    	Bundle bundle = new Bundle();
//    	bundle.putString("f_id", cgtemp.id);
    	bundle.putString("id", cgtemp.id);
    	bundle.putString("f_id", "0");
    	bundle.putString("address", cgtemp.address);
    	bundle.putInt("type", cgtemp.type);
    	intent.putExtras(bundle);
    	startActivity(intent);
	}
	private void alertdialog(){
		//Toast.makeText(InstallMain.this,"测试状态  ", Toast.LENGTH_LONG).show();
		new AlertDialog.Builder(this)
		.setTitle("提示信息")
		.setIcon(null)
		.setCancelable(false)
		.setMessage("设备 "+cgtemp.id+" 已存在")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						
					}
				})
		.create().show();
	}
}

