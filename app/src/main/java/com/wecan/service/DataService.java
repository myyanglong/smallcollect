package com.wecan.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.wecan.smallcollect.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class DataService {
	private Context context = null;
	public int DIALOG_DOWNLOAD_PROGRESS = 1;
	public String pathDown = "http://192.168.0.127:8050/apihandler.ashx?action=download&una=%1$s&pwd=%2$s";
	public String pathUp = "http://192.168.0.127:8050/apihandler.ashx?action=download&una=test2&pwd=123";
	private ProgressDialog pialog;
	public DataService(Context context){
		this.context = context;
	}
	public byte[] readStream(InputStream inStream) throws Exception{
	   ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	   byte[] buffer = new byte[1024];
	   int len= -1;
	   while((len=inStream.read(buffer))!=-1){
		    outStream.write(buffer, 0, len);
		    if(len<buffer.length){
		    	break;
		    }
	   }
	   outStream.close();
	   return outStream.toByteArray();
	}
	public void ServiceDataToXML(String adress){
	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String str = (String)msg.obj;
			switch(msg.what){
			case 0:
				ShowProgressDialog(str);
				break;
			case 1:
				UpdateProgressError(str);
				break;
			default:
				UpdateProgressMsg(str);
				break;
			}
			
		}    	
    };
    public void UpdateProgressError(String str){
    	pialog.dismiss();
    	Toast.makeText(context,str, Toast.LENGTH_LONG).show();
    	
    }
    public void UpdateProgressMsg(String str){
    	pialog.setMessage(str);
    }
	public void ShowProgressDialog(String str){
		pialog = new ProgressDialog(context);
		pialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pialog.setMessage(str);
		pialog.setCancelable(true);
		
		pialog.show();
	}
	public void startDownload() {
		String str = String.format(pathDown, "admin","123456");
		Log.i("debug", str);
        new DownloadFileAsync().execute(str);
        //pathAdress = path;
        handler.sendMessage(handler.obtainMessage(0, "数据下载中..."));
    }
	public void startUpload() {
		String str = String.format(pathUp, "admin","123456");
		Log.i("debug", str);
        new DownloadFileAsync().execute(str);
        //pathAdress = path;
        handler.sendMessage(handler.obtainMessage(0, "数据上传中..."));
    }
	class DownloadFileAsync extends AsyncTask<String, String, String> {
		HttpURLConnection conn;
		protected void onPreExecute() {
			super.onPreExecute();
		}
        protected String doInBackground(String... aurl){
        	
        	if(DIALOG_DOWNLOAD_PROGRESS == 0){
	        	String pathfilenam = pathUp;
	            String end = "\r\n";
	            String twoHyphens = "--";
	            String boundary = "******";
	            try {
	                conn = (HttpURLConnection) new URL(aurl[0]).openConnection();
	                conn.setConnectTimeout(5*1000);
	                conn.setChunkedStreamingMode(128 * 1024);
	                conn.setDoInput(true);
	                conn.setDoOutput(true);
	                conn.setUseCaches(false);
	                conn.setRequestMethod("POST");
	                conn.setRequestProperty("Connection", "Keep-Alive");
	                conn.setRequestProperty("Charset", "UTF-8");
	                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
	                
		            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		                dos.writeBytes(twoHyphens + boundary + end);
		
		                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
		                        + pathfilenam + "\"" + end);
		                dos.writeBytes(end);
		
		                // 将要上传的内容写入流中
		                File file = new File(pathfilenam);
		        		InputStream in = null;
		        		BufferedInputStream bin = null;
		        		int max = 1024;
		        		int YiJingDu = 0;
		        		byte[] buffer = null;
		        			in = new FileInputStream(file);
		        			int filelength = in.available();
		        			buffer = new byte[filelength]; 
		        			bin = new BufferedInputStream(in);
		        			while(true){
		        				if(max > filelength - YiJingDu){
		        					max = filelength - YiJingDu;
		        				}
		        					YiJingDu = YiJingDu + bin.read(buffer, YiJingDu, max);
		        					publishProgress(""+(int)((YiJingDu*100)/filelength));
		        					System.out.println(YiJingDu);
		        				if(YiJingDu >= filelength){
		        					break;
		        				}
		        			}
		        			bin.close();
		        			in.close();
		                // 写入文件
		                dos.write(buffer, 0, buffer.length);
		                System.out.println(buffer.length);
		                String res = new String(buffer, "UTF-8");
		            	System.out.println(res);
		                dos.writeBytes(end);
		                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
		                dos.flush();
		
		                InputStream is = conn.getInputStream();
		                InputStreamReader isr = new InputStreamReader(is, "utf-8");
		                BufferedReader br = new BufferedReader(isr);
		                // 上传返回值
		                String sl;
		                String result="";
		                while((sl = br.readLine()) != null)
		                result = result+sl; 
		                br.close(); 
		                is.close(); 
		                System.out.println(result);
		                return result; 
		            } catch (Exception e) {
		                e.printStackTrace();
		                return "网络出错!";
		            } 
	        	}
        	else if(DIALOG_DOWNLOAD_PROGRESS == 1){
				try {
						conn = (HttpURLConnection)new URL(aurl[0]).openConnection();
						conn.setRequestMethod("GET");
						conn.setConnectTimeout(20*1000);
						if(conn.getResponseCode()== 200){
							Log.i("debug","error:1");
							InputStream inStream = conn.getInputStream();
							byte[] data = readStream(inStream);
							Log.i("debug", data.length + " debug");
							String res = new String(data,"UTF-8");
							handler.sendMessage(handler.obtainMessage(1, "下载成功"));
							return new String(data,"UTF-8");
			      		}
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
        		handler.sendMessage(handler.obtainMessage(1, "网络连接失败，请检查网络连接！"));
	        	return null;
	        }
	        protected void onProgressUpdate(String... progress) {
	            Log.i("debug",progress[0]);
	        }
	        protected void onPostExecute(String unused) {
	            if(DIALOG_DOWNLOAD_PROGRESS == 1){
					try {
						FileOutputStream outstream = new FileOutputStream(
								new File(Environment.getExternalStorageDirectory(), "wecan/small/download/data.xml"));
						OutputStreamWriter writer = new  OutputStreamWriter(outstream);
		          	 	writer.write(unused);
		          	 	writer.flush();
		          	 	writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        }
	    }
}
