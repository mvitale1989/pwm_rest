package it.binarybrain.hw.i2c;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class I2CDriver {
	static I2CDriver instance;
	ConcurrentMap<String,I2CBusDriver> busDrivers;
	Logger logger = LogManager.getLogger(I2CDriver.class);
	
	private I2CDriver(){
		busDrivers = new ConcurrentHashMap<String,I2CBusDriver>();
	}
	
	public static I2CDriver getInstance(){
		if(instance==null)
			instance = new I2CDriver();
		return instance;
	}
	
	public boolean isDriverRunning(String i2cVirtualDevice){
		removeStoppedDrivers();
		return busDrivers.containsKey(i2cVirtualDevice);
	}
	
	public I2CBusDriver startDriver(String i2cBus){
		return startDriver(i2cBus,true);
	}
	
	public I2CBusDriver startDriver(String i2cVirtualDevice,boolean waitForThreadStart){
		removeStoppedDrivers();
		I2CBusDriver newDriver = new I2CBusDriver(i2cVirtualDevice);
		newDriver.start();
		if(waitForThreadStart){
			while(!newDriver.isAlive()){
				try{ Thread.sleep(1); }catch(InterruptedException e){}
			}
		}
		busDrivers.put(i2cVirtualDevice,newDriver);
		return newDriver;
	}
	
	public void stopDriver(String i2cVirtualDevice){
		if(isDriverRunning(i2cVirtualDevice)){
			busDrivers.get(i2cVirtualDevice).signalExit();
			busDrivers.remove(i2cVirtualDevice);
		}
	}
	
	public Set<String> listDrivers(){
		removeStoppedDrivers();
		return busDrivers.keySet();
	}
	
	public void removeStoppedDrivers(){
		Iterator<String> it = busDrivers.keySet().iterator();
		while(it.hasNext()){
			I2CBusDriver driver = busDrivers.get(it.next());
			if(driver!=null&& !driver.isAlive() )
				it.remove();
		}
	}
	
	public I2CBusDriver getDriver(String i2cVirtualDevice){
		if( isDriverRunning(i2cVirtualDevice) )
			return busDrivers.get(i2cVirtualDevice);
		else
			return startDriver(i2cVirtualDevice,true);
	}
	
	public ConcurrentMap<String,I2CBusDriver> getDrivers(){
		return busDrivers;
	}
	
	public void signalExitToAll(){
		Set<String> keys = busDrivers.keySet();
		for(String s: keys){
			getDriver(s).signalExit();
		}
	}

}
