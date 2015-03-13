package it.binarybrain.hw;
import java.io.IOException;

public class I2CTester {

	public static void main(String[] args) {
		boolean debug=true;
		String i2cDevicePath="/dev/i2c-1";
		byte i2cAddress=(byte)0x40;
		I2CDriver i2c = new I2CDriver(debug);
		PCA9685Driver pwmDriver=null;
		try{
			System.out.println("\nSTARTING I2C AND PCA9685 TESTS.....");
			System.out.println("Initializing device 0x"+Integer.toHexString(i2cAddress)+" on I2C bus "+i2cDevicePath+"...");
			i2c.init(i2cDevicePath,i2cAddress);
			System.out.println("Assigning driver to device controller...");
			pwmDriver=new PCA9685Driver(i2c,debug);
			System.out.println("Initializing device...");
			pwmDriver.init();
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
		try{
			i2c.close();
		}catch(IOException e){}
	}

}
