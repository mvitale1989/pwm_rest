package it.binarybrain.tmp;

import it.binarybrain.hw.i2c.I2CDriver;
import it.binarybrain.hw.i2c.PCA9685Driver;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PCA9685Tester {

	public static void main(String[] args) {		
		Logger logger = LogManager.getLogger(PCA9685Tester.class);
		boolean debug=true;
		String i2cDevicePath="/dev/i2c-1";
		byte i2cDeviceAddress=(byte)0x40;
		I2CDriver i2cDriver=null;
		PCA9685Driver pwmDriver=null;
		try{
			logger.info("STARTING I2C AND PCA9685 TESTS.....");
			logger.info("initializing new i2cDriver thread for the virtual device "+i2cDevicePath+"...");
			i2cDriver=new I2CDriver(i2cDevicePath,debug);
			logger.info("starting control thread...");
			i2cDriver.start();
			logger.info("initializing PCA9685 driver for device at address 0x"+Integer.toHexString(i2cDeviceAddress)+"...");
			pwmDriver=new PCA9685Driver(i2cDriver,i2cDeviceAddress);
			logger.info("setting PCA9685 frequency to 50Hz...");
			pwmDriver.setPWMFrequency(50);
			logger.info("starting PCA9685Driver testing routine...");
			pwmDriver.test();
			logger.info("dumping device memory...");
			Map<Integer,Integer> memory = pwmDriver.dumpMemory();
			Iterator<Integer> it = memory.keySet().iterator();
			while(it.hasNext()){
				Integer value=it.next();
				logger.info("Address "+Integer.toHexString(value)+", contents: "+memory.get(value));
			}
			logger.info("TESTING SUCCESSFUL");
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		i2cDriver.signalExit();
	}

}
