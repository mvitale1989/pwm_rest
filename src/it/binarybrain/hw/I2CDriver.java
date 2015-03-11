package it.binarybrain.hw;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class I2CDriver implements Closeable {

	int i2cFileDescriptor=-1;
	boolean debug=false;

	public class DeviceNotOpenException extends Exception{ private static final long serialVersionUID = 7827078331011336573L; }
	public class I2CDriverException extends Exception{ private static final long serialVersionUID = -2926408543711449468L; }
	private native int openFileDescriptor(String i2cDevicePath,int deviceAddress,boolean debug);
	private native void closeFileDescriptor(int fileDescriptor,boolean debug);
	private native void writeByte(int fileDescriptor,byte address,byte value,boolean debug);
	private native byte readByte(int fileDescriptor,byte address,boolean debug);
	static{
		System.loadLibrary("bbi2c");
	}
	
	
	/*
	 * CONSTRUCTORS
	 */
	public I2CDriver(){
		this(false);
	}
	public I2CDriver(boolean debugArg){
		debug=debugArg;
	}
	
	
	/*
	 * DRIVER METHODS
	 */
	public void init(String i2cDevicePath,int deviceAddress) throws FileNotFoundException, I2CDriverException{
		File i2cDeviceFile = new File(i2cDevicePath);
		if(!i2cDeviceFile.exists())
			throw new FileNotFoundException();
		try{
			i2cFileDescriptor=openFileDescriptor(i2cDevicePath,deviceAddress,debug);
		}catch(Exception e){
			i2cFileDescriptor=-1;
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		if(i2cFileDescriptor>=0)
			closeFileDescriptor(i2cFileDescriptor,debug);
	}
	
	public void writeByte(byte address,byte value) throws DeviceNotOpenException {
		if(i2cFileDescriptor<0)
			throw new DeviceNotOpenException();
		writeByte(i2cFileDescriptor,address,value,debug);
	}
	
	public byte readByte(byte address) throws DeviceNotOpenException {
		if(i2cFileDescriptor<0)
			throw new DeviceNotOpenException();
		return readByte(i2cFileDescriptor,address,debug);
	}
}
