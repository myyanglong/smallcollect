package com.wecan.login;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.wecan.smallcollect.R;
import com.wecan.activity.MainActivity;
import com.wecan.domain.PreferencesService;
import com.wecan.service.WaterMeterService;

public class LoginMain extends Activity implements OnClickListener {
	private EditText login_pwd,login_name;
	private Button login;
	private CheckBox delData;
	private int flags = 0;
	private WaterMeterService service;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        findView();
        initData();
    }
	private void findView() {
		login_name = (EditText) findViewById(R.id.login_name);
		login_pwd = (EditText) findViewById(R.id.login_pwd);
		login = (Button) findViewById(R.id.login);
		delData = (CheckBox) this.findViewById(R.id.delData);
	}
    private void initData() {
    	login.setOnClickListener(this);
    	delData.setOnCheckedChangeListener(listener);
//    	service = new WaterMeterService(this);
//    	service.initXMLData(10);
//    	PreferencesService service = new PreferencesService(this);
//    	service.save_userinfo("admin","123456",0,0);
	}
    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.delData){
				if(isChecked){
					flags = 1;
				}
				else {
					flags = 0;
				}
			}
		}
	};
	public static boolean isWifiConnect(Context context) {//判断WiFi是否打开并连接成功
		ConnectivityManager manager = (ConnectivityManager) 
				context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(info.isConnected() == false){
			Toast toast = Toast.makeText(context, "Wifi is no Connect",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);//选择Toast出现的位置
			toast.show();
		}
		return info.isConnected();
	}
	@Override
	public void onClick(View v) {
		isWifiConnect(this);
		String urladdress = "http://192.168.0.127:8050/apihandler.ashx?action=login&una=%1$s&pwd=%2$s&version=%3$s";
		if(v.getId() == R.id.login){
			String loginUrl = String.format(urladdress, login_name.getText().toString(),
					login_pwd.getText().toString(), "0.0.01");
			//System.out.println(loginUrl);
			Log.i("debug", loginUrl);
			if(flags == 1){
				login_name.setText("");
				login_pwd.setText("");
			}
			new DownloadFileAsync().execute(loginUrl);
		}
	}
	public void show(String show){
		Toast.makeText(this, show, Toast.LENGTH_SHORT).show();
	}
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_DOWNLOAD_PROGRESS:   
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在登陆...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            return mProgressDialog;
        default:
            return null;
        }
    }
	class DownloadFileAsync extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
         
        protected String doInBackground(String... aurl) {
    		String filename = "";
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";
            try {
                URL url = new URL(aurl[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url
                        .openConnection();
                httpURLConnection.setConnectTimeout(6*1000);
                httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                DataOutputStream dos = new DataOutputStream(
                        httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                        + filename + "\"" + end);
                
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();
                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String result="";
                String sl;
                while((sl = br.readLine()) != null)
                result = result+sl; 
                br.close(); 
                is.close(); 
                return result; 
            } catch (Exception e) {
                e.printStackTrace();
                return "network service procedure error!";
            } 
        }
        protected void onProgressUpdate(String... progress) {
        }
        protected void onPostExecute(String unused) {
        	Log.i("debug",unused);
        	dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        	System.out.println(unused);
        	if(unused.equals("network service procedure error!")){
        		show(unused);
        		Log.i("debug","network service procedure error!");
        	}else {
	        	try {
					JSONObject jsonObject = new JSONObject(unused);
					boolean b = jsonObject.getBoolean("success");
					String message = jsonObject.getString("message");
					String data = jsonObject.getString("data");
					JSONObject jsonObject1 = new JSONObject(data);
					int type = jsonObject1.getInt("type");
//					int type = 1;
					System.out.println(type);
					int flag = jsonObject1.getInt("flag");
//					int flag = 0;
					System.out.println(flag);
					
					PreferencesService preferences = new PreferencesService(LoginMain.this);
					preferences.save_userinfo(login_name.getText().toString(),
							login_pwd.getText().toString(), type , flag);
					
					if(!b){
						show(message);
						Intent myIntent = new Intent(LoginMain.this, MainActivity.class);
						startActivity(myIntent);
		     			finish();
					}else {
						show(message);
						Log.i("debug",message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
        	}
        }
    }
}