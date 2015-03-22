package it.binarybrain.tmp;

import it.binarybrain.hw.i2c.I2CDriver;
import it.binarybrain.hw.i2c.PCA9685Driver;

import java.io.IOException;

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
			logger.info("\nSTARTING I2C AND PCA9685 TESTS.....");
			logger.info("Initializing new i2cDriver thread for the virtual device "+i2cDevicePath+"...");
			i2cDriver=new I2CDriver(i2cDevicePath,debug);
			logger.info("Starting control thread...");
			i2cDriver.start();
			logger.info("Initializing PCA9685 driver for device at address 0x"+Integer.toHexString(i2cDeviceAddress)+"...");
			pwmDriver=new PCA9685Driver(i2cDriver,i2cDeviceAddress,debug);
			logger.info("Setting PCA9685 frequency to 50Hz...");
			pwmDriver.setPWMFrequency(50);
			logger.info("Starting PCA9685Driver testing routine...");
			pwmDriver.test();
			logger.info("Dumping device memory...");
			pwmDriver.dumpMemory();
			logger.info("\nTESTING SUCCESSFUL\n\n");
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		i2cDriver.signalExit();
	}

}
