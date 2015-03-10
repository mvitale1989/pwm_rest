package it.binarybrain.hw;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class I2CDriver implements Closeable {

	int i2cFileDescriptor=-1;
	boolean debug=false;

	//TODO integrare modalita debug nei metodi nativi
	private native int openFileDescriptor(String i2cDevicePath,int deviceAddress);
	private native void closeFileDescriptor(int fileDescriptor);
	private native void writeByte(int fileDescriptor,byte address,byte value);
	private native byte readByte(int fileDescriptor,byte address);
	public class DeviceNotOpenException extends Exception{}
	static{
		System.loadLibrary("bbi2c");
	}
	
	public I2CDriver(){
		this(false);
	}
	
	public I2CDriver(boolean debugArg){
		debug=debugArg;
	}
	
	
	public void init(String i2cDevicePath,int deviceAddress) throws FileNotFoundException {
		File i2cDeviceFile = new File(i2cDevicePath);
		if(!i2cDeviceFile.exists())
			throw new FileNotFoundException();
		try{
			i2cFileDescriptor=openFileDescriptor(i2cDevicePath,deviceAddress);
		}catch(Exception e){
			i2cFileDescriptor=-1;
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		if(i2cFileDescriptor>=0)
			closeFileDescriptor(i2cFileDescriptor);
	}
	
	public void writeByte(byte address,byte value) throws DeviceNotOpenException {
		if(i2cFileDescriptor<0)
			throw new DeviceNotOpenException();
		writeByte(i2cFileDescriptor,address,value);
	}
	
	public byte readByte(byte address) throws DeviceNotOpenException {
		if(i2cFileDescriptor<0)
			throw new DeviceNotOpenException();
		return readByte(i2cFileDescriptor,address);
	}
}
