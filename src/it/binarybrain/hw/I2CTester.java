package it.binarybrain.hw;
import it.binarybrain.hw.I2CDriver.DeviceNotOpenException;

import java.io.FileNotFoundException;

public class I2CTester {

	public static void main(String[] args) {
		String i2cDevicePath="/dev/i2c-1";
		byte i2cAddress=(byte)0x40;
		System.out.println("/ - - - - - - - - - - - - - \\");
		System.out.println("| - - -I2CDRIVER TESTER - - |");
		System.out.println("\\ - - - - - - - - - - - - - /\n");
		I2CDriver i2c = new I2CDriver(true);
		try{
			System.out.println("Initializing device 0x"+Integer.toHexString(i2cAddress)+" on I2C bus "+i2cDevicePath+"...");
			i2c.init("/dev/i2c-1",(byte)0x40);
			System.out.println("Beginning memory dump of the first 16 bytes...");
			for(int i=0;i<16;i++){
				byte read=i2c.readByte((byte)i);
				System.out.println("Read byte at address "+Integer.toString(i)+": "+Byte.toString(read));
			}
			System.out.println("\nTESTING SUCCESSFUL\n\n");
		}catch(FileNotFoundException | DeviceNotOpenException e){
			e.printStackTrace();
		}
	}

}