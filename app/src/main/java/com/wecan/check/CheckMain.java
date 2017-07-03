package com.wecan.check;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.domain.Configs;
import com.wecan.domain.MyImageView;
import com.wecan.domain.WaterMeter;
import com.wecan.service.DBOpenHelper;

public class CheckMain extends Activity implements OnClickListener{

	private Button btn_save;
	private ImageView img;
	private EditText edit;
	private String img_name,dsc;
	private File mCurrentPhotoFile;
	private String mFileName;
	private boolean isexist = false;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	private	RelativeLayout	s_back;
	public Bundle bundle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.c_debug);
		
		initView();

	}
	private void initView(){
		btn_save = (Button)findViewById(R.id.btn_save);
		img = (ImageView)findViewById(R.id.img);
		edit = (EditText)findViewById(R.id.edit);
		s_back = (RelativeLayout)findViewById(R.id.s_back);

		btn_save.setOnClickListener(this);
		s_back.setOnClickListener(this);
		img.setOnClickListener(this);
		
		Intent intent = getIntent();
		bundle = intent.getExtras();
		
		isexist = dscSlectErrorInfo(bundle.getString("id"));
		if(isexist ==  true){
			if(img_name!=null){
				mCurrentPhotoFile = new File(new File(Environment.getExternalStorageDirectory(),"wecan/small/upload"), img_name);
				Bitmap rawBitmap1 = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath(), null); 
				img.setImageBitmap(rawBitmap1);
			}
			if(dsc!=null){
				edit.setText(dsc);
			}
		}
	}
	
	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			File filex = new File(Environment.getExternalStorageDirectory(),"wecan/small/upload");
			mCurrentPhotoFile = new File(filex, mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			//AbToastUtil.showToast(AddPhotoActivity.this,"未找到系统相机程序");
			Toast.makeText(this,"未找到系统相机程序", Toast.LENGTH_LONG).show();
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		if (resultCode != RESULT_OK){
			return;
		}
		switch (requestCode) {
			case CAMERA_WITH_DATA:
				//String currentFilePath2 = mCurrentPhotoFile.getPath();
				//img_name
				if(img_name !=null)
					dscDelPic(img_name);
				Bitmap rawBitmap1 = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath(), null); 
				img.setImageBitmap(rawBitmap1);
				img_name = mFileName;
				break;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_save:
				//Log.i("debug",edit.getText().toString());
				updateErrorDB(bundle.getString("id"),img_name,edit.getText().toString());
				setResult(RESULT_OK, null);
				finish();
				break;
			case R.id.img:
				doTakePhoto();
				break;
			case R.id.s_back:
				finish();
				break;
		}
	}
	public void dscDelPic(String file){
		File filex = new File(Environment.getExternalStorageDirectory(),"wecan/small/upload");
		new File(filex, file).delete();
	}
	public boolean dscSlectErrorInfo(String id){
		boolean flag = false;
		SQLiteDatabase db = new DBOpenHelper(this).getReadableDatabase();
		//db.execSQL("CREATE TABLE tb_err(keyid integer primary key autoincrement,id,img,dsc,time)");
		Cursor cursor = db.rawQuery("select * from tb_err where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			img_name = cursor.getString(cursor.getColumnIndex("img"));
			dsc = cursor.getString(cursor.getColumnIndex("dsc"));
			//Log.i("debug", img_name + "," + dsc);
			flag = true;
		}
		cursor.close();
		return flag;
	}
	public void dscUpdateErrorInfo(String id,String image,String des){
		SQLiteDatabase db = new DBOpenHelper(this).getReadableDatabase();
		String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(System.currentTimeMillis()));
		//Log.i("debug", image+ "," +des+ "," +time+ "," +id );
		db.execSQL("update tb_err set img=?,dsc=?,time=? where id=?", new Object[]{image,des,time,id});
	}
	public void dscInsertErrorInfo(String id,String image,String des){
		SQLiteDatabase db = new DBOpenHelper(this).getReadableDatabase();
		String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(System.currentTimeMillis()));
		db.execSQL("insert into tb_err(id,img,dsc,time) values(?,?,?,?)",
				new Object[]{id,image,des,time});
	}
	public void updateErrorDB(String id,String image,String des){
		if(isexist)
			dscUpdateErrorInfo(id,image,des);
		else
			dscInsertErrorInfo(id,image,des);
	}
}
