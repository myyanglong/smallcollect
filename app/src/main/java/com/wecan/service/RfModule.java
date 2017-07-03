package com.wecan.service;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

class RfModule {
	private static RfModule rfm;
	private Context parent;
	
	private static final int RF_FREQ_DEFAULT  = 486700000;
	private static final int RF_RATE_DEFAULT  = 1500;
	
	private static byte CTL_CMD_LP      = 0;
  	private static byte CTL_CMD_RECV    = 1;
  	private static byte CTL_CMD_SEND	= 2;
  	private static byte CTL_CMD_WAKEUP  = 3;
  	private static byte CTL_CMD_DATA    = 4;
  	
  	private static final int BUF_SIZE  = 256;
	
	private D2xxManager ftD2xx = null;
    private FT_Device ftDev = null;
    
    private int rfFreq;
	private int rfBaud;
    
    private RfModule(Context parent) {
    	this.parent = parent;
    	try {
            ftD2xx = D2xxManager.getInstance(parent);
            rfFreq = RF_FREQ_DEFAULT;
            rfBaud = RF_RATE_DEFAULT;
        } catch (D2xxManager.D2xxException ex) {
        }
    }
    
    public static RfModule getInstance (Context parent) {
		if (rfm == null) {
			rfm = new RfModule(parent);
			rfm.openDevice(parent);
		}
		
		return rfm;
	}
    
    public boolean openDevice(Context parent) {
        if(ftDev != null) {
        	ftDev.close();
        }

        int devCount = 0;
        devCount = ftD2xx.createDeviceInfoList(parent);


        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);

        if(devCount <= 0) {
            return false;
        }

        ftDev = ftD2xx.openByIndex(parent, 0);

        
    	if(ftDev.isOpen()) {
            SetConfig(115200, (byte)8, (byte)1, (byte)0, (byte)0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }
    	
    	return true;
    }
    
    
    public void close() {
    	if (ftDev != null) 
			ftDev.close();
    }
    
    
    protected void finalize() throws Throwable  {
		if (ftDev != null)
			ftDev.close();
		
		super.finalize();
    }
	
	private void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
        case 7:
            dataBits = D2xxManager.FT_DATA_BITS_7;
            break;
        case 8:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        default:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        }

        switch (stopBits) {
        case 1:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        case 2:
            stopBits = D2xxManager.FT_STOP_BITS_2;
            break;
        default:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        }

        switch (parity) {
        case 0:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        case 1:
            parity = D2xxManager.FT_PARITY_ODD;
            break;
        case 2:
            parity = D2xxManager.FT_PARITY_EVEN;
            break;
        case 3:
            parity = D2xxManager.FT_PARITY_MARK;
            break;
        case 4:
            parity = D2xxManager.FT_PARITY_SPACE;
            break;
        default:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
        case 0:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        case 1:
            flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
            break;
        case 2:
            flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
            break;
        case 3:
            flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
            break;
        default:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }
	
	public byte calSum(byte[] buf, int len) {
		byte sum = 0;
		int i;
		
		for (i = 0; i < len; i++) {
			sum += buf[i];
		}

		return sum;
	}    
    
    private void clearBuffer() {
        ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
    }
    
    private boolean controlSend(byte cmd, byte[] data, int dataLen, long time) {
    	if((ftDev == null) || (ftDev.isOpen() == false))  {
    		openDevice(parent);
            return false;
        }
    	
    	if (dataLen + 5 > BUF_SIZE)
    		return false;
    	
    	synchronized (ftDev) {
            if(ftDev.isOpen() == false) {
                return false;
            }
            
            clearBuffer();
            
            byte[] tmpBuf = new byte[BUF_SIZE];
            int tmp;
    		
    		tmpBuf[0] = (byte)0xb3;
    		tmpBuf[1] = (byte)((dataLen + 1) & 0xff);
    		tmpBuf[2] = cmd;
    		if ((dataLen > 0) && (data != null)) {
    			System.arraycopy(data, 0, tmpBuf, 3, dataLen);
    		}
    		tmpBuf[3 + dataLen] = calSum(tmpBuf, dataLen + 3);
    		tmpBuf[4 + dataLen] = (byte)0x3b;
    		ftDev.write(tmpBuf, dataLen + 5);
    		
    		if (ftDev.read(tmpBuf, 1, time) < 1) 
				return false;
			
			if (tmpBuf[0] != (byte)0xb3) 
				return false;
			
			if (ftDev.read(tmpBuf, 1, 100) < 1) 
				return false;
			
			if (tmpBuf[0] + 2 > BUF_SIZE) 
				return false;

			tmp = tmpBuf[0];
			if (ftDev.read(tmpBuf, tmp + 2, 100) < (tmp + 2) || tmpBuf[0] != (byte)(0x80 | cmd)) {
				clearBuffer();
    			return false;
			}
    		
    		return true; 	
    	}
	}
    
    public boolean rfsetting(int freq, int baud, boolean recvNow){
		rfFreq = freq;
		rfBaud = baud;
		
		if (recvNow) {
			byte[] buf = new byte[8];
			int i = 0;
			
			buf[i++] = (byte)(rfFreq & 0xff);
			buf[i++] = (byte)((rfFreq >> 8) & 0xff);
			buf[i++] = (byte)((rfFreq >> 16) & 0xff);
			buf[i++] = (byte)((rfFreq >> 24) & 0xff);
			buf[i++] = (byte)(rfBaud & 0xff);
			buf[i++] = (byte)((rfBaud >> 8) & 0xff);
			
			return controlSend((byte)CTL_CMD_RECV, buf, i, 100);
		}
		
		return true;
	} 
    
    
    public boolean send(byte packet[], int len, long timeout) {
    	int i = 0;
		byte[] buf = new byte[BUF_SIZE];
		
		if ((len == 0) || (packet == null))
			return true;
		
		if ((len + 6) > buf.length) {
			return false;
		}
		
		i = 0;
		buf[i++] = (byte)(rfFreq & 0xff);
		buf[i++] = (byte)((rfFreq >> 8) & 0xff);
		buf[i++] = (byte)((rfFreq >> 16) & 0xff);
		buf[i++] = (byte)((rfFreq >> 24) & 0xff);
		buf[i++] = (byte)(rfBaud & 0xff);
		buf[i++] = (byte)((rfBaud >> 8) & 0xff);
		if ((len > 0) && (packet != null))
			System.arraycopy(packet, 0, buf, i, len);
		
		return controlSend((byte)CTL_CMD_SEND, buf, len + i, timeout);
	}
	
	public int read(byte[] packet, int len, long timeout) {
		byte[] tmpBuf = new byte[BUF_SIZE];
		int ret = 0;
		int tmp;
		
		if((ftDev == null) || (ftDev.isOpen() == false))  {
    		openDevice(parent);
            return 0;
        }
			
		synchronized (ftDev) {	
			if(ftDev.isOpen() == false) {
                return 0;
            }
			
			if (ftDev.read(tmpBuf, 1, timeout) < 1) 
				return 0;
			
			if (tmpBuf[0] != (byte)0xb3)
				return 0;
			
			if (ftDev.read(tmpBuf, 1, 100) < 1) 
				return 0;
			
			if (tmpBuf[0] + 2 > BUF_SIZE)
				return 0;

			tmp = tmpBuf[0];
			if (ftDev.read(tmpBuf, tmp + 2, 100) < (tmp + 2) || tmpBuf[0] != (byte)(0x80 | CTL_CMD_DATA)) {
				clearBuffer();
    			return 0;
			}
			
			ret = (tmp - 1) < len? tmp - 1: len;
			System.arraycopy(tmpBuf, 1, packet, 0, ret);
			return ret;
		}
	}
	
	public int pull(byte[] packet, int len) {
		byte[] tmpBuf = new byte[BUF_SIZE];
		int ret = 0;
		int tmp;
		
		if((ftDev == null) || (ftDev.isOpen() == false))  {
    		openDevice(parent);
            return 0;
        }
			
		synchronized (ftDev) {	
			if(ftDev.isOpen() == false)
                return 0;
			
			if (ftDev.getQueueStatus() < 1) 
				return 0;
			ftDev.read(tmpBuf, 1);
			if (tmpBuf[0] != (byte)0xb3)
				return 0;
			if (ftDev.read(tmpBuf, 1, 20) < 1) 
				return 0;
			if (tmpBuf[0] + 2 > BUF_SIZE)
				return 0;
			tmp = tmpBuf[0];
			if (ftDev.read(tmpBuf, tmp + 2, 100) < (tmp + 2) || tmpBuf[0] != (byte)(0x80 | CTL_CMD_DATA)) {
				clearBuffer();
    			return 0;
			}
			
			ret = (tmp - 1) < len? tmp - 1: len;
			System.arraycopy(tmpBuf, 1, packet, 0, ret);
			return ret;
		}
	}
	
	public boolean wakeupAndSend(byte[] packet, int len, long timeout){
		int i = 0;
		byte[] buf = new byte[BUF_SIZE];
		
		if ((len + 6) > buf.length) {
			return false;
		}		
		
		buf[i++] = (byte)(rfFreq & 0xff);
		buf[i++] = (byte)((rfFreq >> 8) & 0xff);
		buf[i++] = (byte)((rfFreq >> 16) & 0xff);
		buf[i++] = (byte)((rfFreq >> 24) & 0xff);
		buf[i++] = (byte)(rfBaud & 0xff);
		buf[i++] = (byte)((rfBaud >> 8) & 0xff);
		
		if (!controlSend((byte)CTL_CMD_WAKEUP, buf, i, 3500))
			return false;

		return send(packet, len, timeout);
	}
	
	public boolean enterSleep() {
		return controlSend((byte)CTL_CMD_LP, null, 0, 20);
	}
}

