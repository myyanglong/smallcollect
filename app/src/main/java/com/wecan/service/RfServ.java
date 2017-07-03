package com.wecan.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.SparseArray;

import com.wecan.domain.DMAMeter;
import com.wecan.domain.WaterMeter;
import com.wecan.install.ServiceInstall;
import com.wecan.small.ServiceSmall;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class RfServ extends IntentService {
	private static final String ACTION_RF_START = "com.wcanws.action.RF_ACTION_START";
	private static final String ACTION_RF_INNET = "com.wcanws.action.RF_ACTION_INNET";
	private static final String ACTION_RF_COLLECT = "com.wcanws.action.RF_ACTION_COLLECT";
	private static final String ACTION_RF_COLLECTBIG = "com.wcanws.action.RF_ACTION_COLLECTBIG";
	public static final String EXTRA_RF_PARAM = "com.wcanws.extra.RF_PARAM";
	public static final String BROADCAST_RF_RESULT = "com.wcanws.broadcast.RF_RESULT";
	
	private static final int RF_CHANNEL0  = 486700000;
	private static final int RF_CHANNEL1  = 489100000;
	private static final int RF_CHANNEL2  = 490300000;
	private static final int RF_CHANNEL3  = 491500000;
	private static final int RF_CHANNEL4  = 492700000;
	
	private static final int RF_DMACHAN   = 488300000;
	
	private static final int RF_RATE_1K5  = 1500;
	private static final int RF_RATE_3K5  = 3500;
	
	private static final int[ ]dataChns = {RF_CHANNEL1, RF_CHANNEL2 ,RF_CHANNEL3, RF_CHANNEL4};  
	
	private static final byte RF_FRAME_HEAD = 'W';
	private static final byte CMD_CONFIG_BEGIN	=	0x10;
	private static final byte CMD_CONFIG_TS		=	0x11;
	private static final byte CMD_METER_COLLECT	=	0x20;
	private static final byte CMD_ACQUIRE_BEGIN	=	0x21;
	private static final byte CMD_METER_ACQUIRE	=	0x22;
	
	private static boolean rfmRunFlag;
	
	private RfModule rfm;
	
	PowerManager pm;
	WakeLock wl;
	
	@Override
	public void onCreate()	{
		rfm = RfModule.getInstance(this);
		rfm.enterSleep();
		
		if (wl == null) {
			pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			//wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
			wl.acquire();
		}
		
	    super.onCreate();
	}
	
	@Override
	public void onDestroy()	{
		rfm.enterSleep();
		rfm.close();
		
		if (wl != null && wl.isHeld()){
			wl.release();
			wl = null;
		}
		
	    super.onDestroy();
	}
	
	public static void startRfModule(Context context) {		
		Intent intent = new Intent(context, RfServ.class);
	    intent.setAction(ACTION_RF_START);
	    context.startService(intent);
	}
	
	public static void startInnet(Context context, String devId)
	{
		rfmRunFlag = true;
	    Intent intent = new Intent(context, RfServ.class);
	    intent.setAction(ACTION_RF_INNET);
	    intent.putExtra(EXTRA_RF_PARAM, devId);
	    context.startService(intent);
	}

	/**
	 *
	 * @param context 上下文对象
	 * @param devId 读取小表数据id
	 */
	public static void startCollect(Context context, String devId)
	{
		rfmRunFlag = true;
	    Intent intent = new Intent(context, RfServ.class);
	    intent.setAction(ACTION_RF_COLLECT);
	    intent.putExtra(EXTRA_RF_PARAM, devId);
	    context.startService(intent);
	}
	
	public static void startCollectBig(Context context) {
		rfmRunFlag = true;
		Intent intent = new Intent(context, RfServ.class);
	    intent.setAction(ACTION_RF_COLLECTBIG);
	    context.startService(intent);
	}
	
	public static void stopRfModule() {
		rfmRunFlag = false;
	}

	public RfServ() {
	    super("RfServ");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	    if (intent != null)  {
	        final String action = intent.getAction();
	        
	        if (ACTION_RF_START.equals(action)) {
	        	rfStart();	        	
	        } else if (ACTION_RF_INNET.equals(action)) {
	            final String devId = intent.getStringExtra(EXTRA_RF_PARAM);
	            rfInnet(devId);
	        } else if (ACTION_RF_COLLECT.equals(action)) {
				//读取数据
	        	final String devId = intent.getStringExtra(EXTRA_RF_PARAM);
		        rfCollect(devId);
	        } else if (ACTION_RF_COLLECTBIG.equals(action)) {
	        	rfCollectBig();
	        }
	    }
	}
	
	private void rfStart() {
		for (int cnt = 0; cnt < 2; cnt++) {	
			if (rfm.openDevice(this)) {
				rfm.enterSleep();
				return;
			} else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	
	private void broadInnetInfo(int what, String id, int all, int sucess) {
		Intent intent = new Intent("com.weian.service.BROAD_CAST");
		Bundle bundle = new Bundle();  
		bundle.putInt("cmd", what);
		if (id != null)
			bundle.putString("id", id);
		bundle.putInt("all", all);
		bundle.putInt("sucess", sucess);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	private void broadCollectInfo(int what, String id, String total, int status, int rf, int all, int sucess) {
		Intent intent = new Intent("com.weian.service.BROAD_CAST");	
		Bundle bundle = new Bundle();  
		bundle.putInt("cmd", what);
		bundle.putString("id_0", id);
		bundle.putString("t_0", total);
		bundle.putInt("s_0", status);
		bundle.putInt("rf_0", rf);
		bundle.putInt("all", all);
		bundle.putInt("sucess", sucess);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	private void rfInnet(String sId) {
		int devId = Integer.parseInt(sId);
		ServiceInstall serv = new ServiceInstall(this);
		List<WaterMeter> meterList = serv.selectWaterMeterFromID(sId);
		int all = 0, sucess = 0;		
		if ((serv.selectWaterMeterFromID(sId, false).size() <= 0) || (meterList.size() > 200)) {
			broadInnetInfo(1, null, 0, 0);
			return;
		}
		
		int tsWait;
		tsWait = (meterList.size() * 400 / 1000 + 2) & 0xffff;
		
		rfm.rfsetting(RF_CHANNEL0, RF_RATE_1K5, false);
		
		// wakeup up all the meter
		int i = 0;
		byte[] buf = new byte[64];
		buf[i++] = RF_FRAME_HEAD;
		buf[i++] = CMD_CONFIG_BEGIN;					
		buf[i++] = (byte)(tsWait & 0xff);
		buf[i++] = (byte)((tsWait >> 8) & 0xff);
		if (rfm.wakeupAndSend(buf, i, 200) == false) {
			broadInnetInfo(2, null, 0, 0);
			return;
		}
    	int ts = -1;
		for(WaterMeter meter: meterList) {
			if (rfmRunFlag == false)
				return;
			
			ts++;
			if (meter.tag == 2) continue;
			
			all++;
			int id = Integer.parseInt(meter.id);  
			i = 0;
			buf[i++] = RF_FRAME_HEAD;
			buf[i++] = CMD_CONFIG_TS;
			buf[i++] = (byte)(id & 0xff);
			buf[i++] = (byte)((id >> 8) & 0xff);
			buf[i++] = (byte)((id >> 16) & 0xff);
			buf[i++] = (byte)((id >> 24) & 0xff);
			buf[i++] = (byte)(ts & 0xff);;				// slot low byte
			buf[i++] = (byte)((ts >> 8) & 0xff);;		// slot high byte
			buf[i++] = (byte)(devId & 0xff);
			buf[i++] = (byte)((devId >> 8) & 0xff);
			buf[i++] = (byte)((devId >> 16) & 0xff);
			buf[i++] = (byte)((devId >> 24) & 0xff);
			
			if (rfm.send(buf,  i, 250) == false) 
				continue;		
			if (rfm.read(buf, 64, 200) < 6) 
				continue;
			if (buf[0] != RF_FRAME_HEAD) 
				continue;
			if (buf[1] != (byte)(0x80 | CMD_CONFIG_TS)) 
				continue;
			
			int tmpId = (buf[2] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff) << 16) | ((buf[5] & 0xff) << 24);
			if (tmpId != id)
				continue;
			
			sucess++;
			
			broadInnetInfo(0, meter.id, all, sucess);
		}
		
		broadInnetInfo(1, null, all, sucess);
		
		return ;
	}
	
	
	private void meterCollectBegin(int devId, byte turn)	{
		int i = 0;
		byte[] buf = new byte[16];
		
		buf[i++] = RF_FRAME_HEAD;
		buf[i++] = CMD_METER_COLLECT;			
		buf[i++] = (byte)(devId & 0xff);
		buf[i++] = (byte)((devId >> 8) & 0xff);
		buf[i++] = (byte)((devId >> 16) & 0xff);
		buf[i++] = (byte)((devId >> 24) & 0xff);
		buf[i++] = turn;
		
		rfm.rfsetting(RF_CHANNEL0, RF_RATE_1K5, false);
		rfm.wakeupAndSend(buf, i, 300);
	}

	private void meterAcquireBegin(int devId, int num)	{
		int tsWait, i = 0;
		byte[] buf = new byte[16];

		tsWait = (num * 400 / 1000 + 2) & 0xffff;		// add 1 seconds
		buf[i++] = RF_FRAME_HEAD;
		buf[i++] = CMD_ACQUIRE_BEGIN;			
		buf[i++] = (byte)(tsWait & 0xff);
		buf[i++] = (byte)((tsWait >> 8) & 0xff);
		buf[i++] = (byte)(devId & 0xff);
		buf[i++] = (byte)((devId >> 8) & 0xff);
		buf[i++] = (byte)((devId >> 16) & 0xff);
		buf[i++] = (byte)((devId >> 24) & 0xff);
		
		rfm.rfsetting(RF_CHANNEL0, RF_RATE_1K5, false);
		rfm.wakeupAndSend(buf, i, 300);
		
		try {
			Thread.sleep(1000);
		} catch ( InterruptedException e) {
		}
	}

	/**
	 *
	 * @param sId 读取数据ID
	 */
	private void rfCollect(String sId) {		
		int devId = Integer.parseInt(sId);
		ServiceSmall serv = new ServiceSmall(this);
		List<WaterMeter> meterList = serv.selectWaterMeterFromID(sId, false);
		if (meterList.size() <= 0) {
			broadCollectInfo(1, "", "", 0, 0, 0, 0);
			return;
		}
		
		SparseArray<WaterMeter> meterMap = new SparseArray<WaterMeter> ();
		for (WaterMeter meter : meterList) {
			meterMap.put(Integer.parseInt(meter.id), meter);
		}
		
		byte[] buf = new byte[64];
		int all = meterList.size(), sucess = 0, len, tmpId;
		int elasp = 300 * serv.selectWaterMeterFromID(sId).size() + 2000;
		long start;
		boolean first = true;
		for (byte cnt = 0; (cnt < 3) && (first || ((all - sucess) > 4)); cnt++) {
			first = false;
			meterCollectBegin(devId, cnt);
			rfm.rfsetting(dataChns[cnt], RF_RATE_1K5, true);
			start = SystemClock.uptimeMillis();
			while (SystemClock.uptimeMillis() - start < elasp) {
				if (rfmRunFlag == false)
					return;
				
				len = rfm.pull(buf, 64);
				if (len >= 16) {
					if (buf[0] != RF_FRAME_HEAD) continue;
					if (buf[1] != (byte)(0x80 | CMD_METER_COLLECT)) continue;
					if ((len > 16) && (rfm.calSum(buf, len - 1) != buf[len - 1])) continue;				
					tmpId = (buf[2] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff) << 16) | ((buf[5] & 0xff) << 24);
					WaterMeter tmpMeter = meterMap.get(tmpId);
					if (tmpMeter != null) {
						tmpMeter.status = buf[6] & 0xff;
						tmpMeter.rf = buf[7] & 0xff;
						tmpMeter.total = getTotalStr(buf, 8);
						if (tmpMeter.read == 0) {
							tmpMeter.read = 1;
							sucess++;
							broadCollectInfo(0, tmpMeter.id, tmpMeter.total, tmpMeter.status, 0, all, sucess);
						}
					}
				}
			}
		}
		
		
		if (((all - sucess) < 10) && (sucess < all)) {
			for(int cnt = 0; (cnt < 3) && (sucess < all); cnt++) {				
				meterAcquireBegin(devId, all - sucess);
				elasp = (all - sucess) * 400 + 2000;
				start = SystemClock.uptimeMillis();
				for (WaterMeter meter : meterList) {
					if (rfmRunFlag == false)
						return;
					
					if (meter.read != 0) continue;
					
					tmpId = Integer.parseInt(meter.id);
					int i = 0;
					buf[i++] = RF_FRAME_HEAD;
					buf[i++] = CMD_METER_ACQUIRE;
					buf[i++] = (byte)(tmpId & 0xff);
					buf[i++] = (byte)((tmpId >> 8) & 0xff);
					buf[i++] = (byte)((tmpId >> 16) & 0xff);
					buf[i++] = (byte)((tmpId >> 24) & 0xff);
					rfm.send(buf,  i, 250);
					len = rfm.read(buf, 64, 250);
					if (len >= 16) {
						if (buf[0] != RF_FRAME_HEAD) continue;
						if (buf[1] != (byte)(0x80 | CMD_METER_ACQUIRE)) continue;
						if ((len > 16) && (rfm.calSum(buf, len - 1) != buf[len - 1])) continue;					
						tmpId = (buf[2] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff) << 16) | ((buf[5] & 0xff) << 24);
						if (tmpId == Integer.parseInt(meter.id)) {
							meter.status = buf[6] & 0xff;
							meter.rf = buf[7] & 0xff;
							meter.total = getTotalStr(buf, 8);
							if (meter.read == 0) {
								meter.read = 1;
								sucess++;
								broadCollectInfo(0, meter.id, meter.total, meter.status, meter.rf, all, sucess);
							}
						}
					}
				}
				while (SystemClock.uptimeMillis() - start < elasp);
			}
		}
		
		broadCollectInfo(2, "", "", 0, 0, all, sucess);
		
		return ;
	}
	
	
	private void bigCommonAck(byte cmd, int id) {
		byte[] buf = new byte[7];
		
		buf[0] = (byte)0x06;
		buf[1] = (byte)(0x80 | cmd);
		buf[2] = (byte)((id >> 24) & 0xff);	
		buf[3] = (byte)((id >> 16) & 0xff);
		buf[4] = (byte)((id >> 8) & 0xff);
		buf[5] = (byte)(id & 0xff);
		buf[6] = rfm.calSum(buf, 6);
		rfm.send(buf, 7, 200);
	}
	
	private void bigDataAck(int id) {
		byte[] buf = new byte[40];
		
		buf[0] = (byte)37;
		buf[1] = (byte)0x91;
		buf[2] = (byte)((id >> 24) & 0xff);	
		buf[3] = (byte)((id >> 16) & 0xff);
		buf[4] = (byte)((id >> 8) & 0xff);
		buf[5] = (byte)(id & 0xff);
		buf[6] = 0;		// no valid settings
		buf[36] = 0x02;	// 发送间隔				
		buf[37] = rfm.calSum(buf, 37);
		rfm.send(buf, 38, 500);
	}
	
	private DMAMeter bigPacket2DMA(String id, String addr, byte[] buf) {
		DMAMeter bm = new DMAMeter();
		
		bm.setID(id);
		bm.setAddr(addr);
		
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.US);
		bm.setGTime(format.format(new Date(System.currentTimeMillis())));
		
		int temp = (((int)buf[6] & 0xff) << 8) | ((int)buf[7] & 0xff);
		bm.setStaus(temp);
		
		temp = (((int)buf[8] & 0xff) << 24) | (((int)buf[9] & 0xff) << 16) | (((int)buf[10] & 0xff) << 8) | ((int)buf[11] & 0xff) ;
		bm.setBubbles(temp);
		
		String str = String.format(Locale.US, "%02d-%02d-%02d %02d:%02d:%02d", buf[12], buf[13], buf[14], buf[15], buf[16], buf[17]);
		temp = (((int)buf[18] & 0xff) << 8) | ((int)buf[19] & 0xff);
		str += " " + String.format(" %d", temp);
		bm.setTime(str);
		
		bm.setPTotal(getTotalStr(buf, 20));
		bm.setNTotal(getTotalStr(buf, 28));
		bm.setInsFlow(arryToFloat(buf, 36));
		
		temp = (((int)buf[40] & 0xff) << 8) | ((int)buf[41] & 0xff);
		bm.setMaxTm(temp);
		bm.setMax(arryToFloat(buf, 42));
		
		temp = (((int)buf[46] & 0xff) << 8) | ((int)buf[47] & 0xff);
		bm.setMinTm(temp);
		float tmp = arryToFloat(buf, 48);
		bm.setMin(tmp);
		
		bm.setPress(buf[52]& 0xff);
		float ft;
		
		ft = ((((int)buf[53] & 0xff) << 8) | ((int)buf[54] & 0xff)) * (float)3.333;
		bm.setTemp(ft);
				
		return bm;
	}
	
	private void rfCollectBig() {
		byte[] buf = new byte[64];
    	byte cmd;
    	int len, id;
    	
    	List<DMAMeter> meterList = new WaterMeterService(this).getListBigMeter();
		SparseArray<DMAMeter> meterMap = new SparseArray<DMAMeter> ();
		for (DMAMeter meter : meterList) {
			meterMap.put(Integer.parseInt(meter.getID()), meter);
		}
    	
		rfm.rfsetting(RF_DMACHAN, RF_RATE_3K5, true);
		while (rfmRunFlag) {
			len = rfm.read(buf, 64, 200);
    		if (len < 7) continue;
    		cmd = buf[1];
    		switch(cmd) {
    		
    		case (byte)0x11:
    			if (len < 57)
    				break;
        		id =  ((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | (buf[5] & 0xff) ;
    			if (meterMap.get(id) != null ) { 
    				bigDataAck(id);
    				Intent intent = new Intent("com.weian.service.BROAD_CAST");    
    				intent.putExtra("DMAMeter", bigPacket2DMA(Integer.toString(id), meterMap.get(id).getAddr(), buf));	
					sendBroadcast(intent);
    			}
    			break;
    		
    		case (byte)0x13:
    			id =  ((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | (buf[5] & 0xff) ;
				if (meterMap.get(id) != null ) {
					bigCommonAck(cmd, id);
				}
    			break;
    		
    		case (byte)0x7f:
    			id =  ((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | (buf[5] & 0xff) ;
    			if (meterMap.get(id) != null ) {
    				bigCommonAck(cmd, id);
    			}
    			break;
    		}
    	}

	}

	
	private String getTotalStr(final byte[] buffer, final int index){
		String str,str1;
		
		str = String.format("%02X%02X%02X%02X", buffer[index], buffer[index + 1], buffer[index + 2], buffer[index + 3]);
		str1 = String.format("%02X%02X%02X%02X", buffer[index + 4], buffer[index + 5], buffer[index + 6], buffer[index + 7]);
		str = Long.parseLong(str, 16) + "." + String.format("%03d", Long.parseLong(str1, 16)*1000/Long.parseLong("100000000", 16));
		
		return str;
	}
	
	public static float arryToFloat(byte[] buf,int Pos)     
	{   
	    int accum = 0;   
	    
	    accum = (int)buf[Pos] & 0xff;
		accum |= ((int)buf[Pos+1] & 0xFF)<<8; 
		accum |= ((int)buf[Pos+2] & 0xFF)<<16;  
	    accum |= ((int)buf[Pos+3] & 0xFF)<<24;  
	    
	    return Float.intBitsToFloat(accum);   
	}  
	
}
