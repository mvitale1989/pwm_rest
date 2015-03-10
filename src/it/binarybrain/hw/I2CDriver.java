package it.binarybrain.hw;

public class I2CDriver {
	static{
		System.loadLibrary("bbi2c");
	}
	
	int i2cFileDescriptor=-1;
	boolean debug=false;
		
	private native int openFileDescriptor(String i2cDevicePath,int deviceAddress);
	public native void writeByte(byte address,byte value);
	public native byte readByte(byte address);
	
	public void init(String i2cDevicePath,int deviceAddress){
		//TODO check i2cDevicePath file existence
		try{
			i2cFileDescriptor=openFileDescriptor(i2cDevicePath,deviceAddress);
		}catch(Exception e){
			System.out.println("EXCEPTION!!");
		}
	}
	
	public void test(){
		System.out.println("I am the i2c driver.");
	}
}
