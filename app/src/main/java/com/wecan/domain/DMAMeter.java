package com.wecan.domain;

import java.io.Serializable;

public class DMAMeter implements Serializable {
	private static final long serialVersionUID = 4014460743820453958L;
	
	private String ID;
	private String Addr;
	private String GTime;
	private int	  Staus;
	private int    Bubbles;
	private String Time;	
	private String PTotal;
	private String NTotal;
	private float InsFlow;	
	private float Max;
	private int  MaxTm;
	private float Min;
	private int MinTm;
	private int Press;
	private float Temp;
	
	public DMAMeter() {
		this.ID = "0000000000";
		this.Addr = "重庆伟岸研发三楼";
		this.GTime = "15-05-06 10:38:06";
		this.Staus = 0;
		this.Bubbles = 0;
		this.Time = "15-05-06 10:36:04 100";
		this.PTotal = "0.0";
		this.NTotal = "0.0";
		this.Max = 0;
		this.MaxTm = 0;
		this.Min = 0;
		this.MinTm = 0;
		this.Press = 0;
		this.Temp = (float)25.6;
	}
	
	public final void setID(String value) {
        this.ID = value;
    }
    public final String getID() {
        return this.ID;
    }    
    
    public final void setAddr(String value) {
    	this.Addr = value;
    }
    public final String getAddr()    {
        return this.Addr;
    }  
    
    public final void setStaus(int value) {
    	this.Staus = value;
    }

    public final int getStaus()    {
        return this.Staus;
    }
    
    public final void setGTime(String value) {
    	this.GTime = value;
    }

    public final String getGTime()    {
        return this.GTime;
    }
    
    public final void setBubbles(int value) {
    	this.Bubbles = value;
    }

    public final int getBubbles()    {
        return this.Bubbles;
    }
    
    public final void setTime(String value)    {
    	this.Time = value;
    }

    public final String getTime()    {
        return this.Time;
    }  
    
    public final void setPTotal(String value) {
    	this.PTotal = value;
    }

    public final String getPTotal()    {
        return this.PTotal;
    }
    
    public final void setNTotal(String value) {
    	this.NTotal = value;
    }

    public final String getNTotal()  {
        return this.NTotal;
    }
    
    public final void setInsFlow(float value) {
    	this.InsFlow = value;
    }

    public final float getInsFlow()  {
        return this.InsFlow;
    }
    
    public final void setMax(float value) {
    	this.Max = value;
    }

    public final float getMax()  {
        return this.Max;
    }
    
    public final void setMaxTm(int value) {
    	this.MaxTm = value;
    }

    public final int getMaxTm()  {
        return this.MaxTm;
    }
    
    
    public final void setMin(float value) {
    	this.Min = value;
    }

    public final float getMin()  {
        return this.Min;
    }
    
    public final void setMinTm(int value) {
    	this.MinTm = value;
    }

    public final int getMinTm()  {
        return this.MinTm;
    }
    
    public final void setPress(int value) {
    	this.Press = value;
    }

    public final int getPress()  {
        return this.Press;
    }
    
    public final void setTemp(float value) {
    	this.Temp = value;
    }

    public final float getTemp()  {
        return this.Temp;
    }
}



