/**
 *
 */
package com.wecan.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import android.os.IBinder;
import android.widget.Toast;

public class UartService extends Service {
	
	@Override  
	public void onCreate() { 
		super.onCreate();  

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);        
    }  
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }
	 
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	RfServ.startRfModule(this);
        return super.onStartCommand(intent, flags, startId);  
    }  
	
	
	@Override  
    public IBinder onBind(Intent intent) { 
        return null;  
    } 

	
	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            	RfServ.startRfModule(context);
                Toast.makeText(context, "设备已经打开",Toast.LENGTH_LONG).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
               ;
            }
        }
    };
}
