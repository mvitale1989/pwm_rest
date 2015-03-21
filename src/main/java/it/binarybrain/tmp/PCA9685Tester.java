package it.binarybrain.tmp;
import it.binarybrain.hw.i2c.I2CDriver;
import it.binarybrain.hw.i2c.PCA9685Driver;

import java.io.IOException;

public class PCA9685Tester {

	public static void main(String[] args) {
		//System.out.println("Library path:"+System.getProperty("java.library.path"));
		boolean debug=true;
		String i2cDevicePath="/dev/i2c-1";
		byte i2cDeviceAddress=(byte)0x40;
		I2CDriver i2cDriver=null;
		PCA9685Driver pwmDriver=null;
		try{
			System.out.println("\nSTARTING I2C AND PCA9685 TESTS.....");
			System.out.println("Initializing new i2cDriver thread for the virtual device "+i2cDevicePath+"...");
			i2cDriver=new I2CDriver(i2cDevicePath,debug);
			System.out.println("Starting control thread...");
			i2cDriver.start();
			System.out.println("Initializing PCA9685 driver for device at address 0x"+Integer.toHexString(i2cDeviceAddress)+"...");
			pwmDriver=new PCA9685Driver(i2cDriver,i2cDeviceAddress,debug);
			System.out.println("Setting PCA9685 frequency to 50Hz...");
			pwmDriver.setPWMFrequency(50);
			System.out.println("Starting PCA9685Driver testing routine...");
			pwmDriver.test();
			System.out.println("Dumping device memory...");
			pwmDriver.dumpMemory();
			System.out.println("\nTESTING SUCCESSFUL\n\n");
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		i2cDriver.signalExit();
	}

}
