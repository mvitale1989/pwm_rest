package it.binarybrain.hw;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;

public class I2CDriver implements Closeable {

	private int i2cFileDescriptor=-1;
	private int nativeExitCode=0;
	private boolean debug=false;

	//public class DeviceNotOpenException extends Exception{ private static final long serialVersionUID = 7827078331011336573L; }
	//public class I2CDriverException extends Exception{ private static final long serialVersionUID = -2926408543711449468L; }
	private native int nativeOpenDeviceFile(String i2cDevicePath,int deviceAddress);
	private native void nativeCloseDeviceFile();
	private native void nativeWriteByte(byte address,byte value);
	private native byte nativeReadByte(byte address);
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
	public void init(String i2cDevicePath,int deviceAddress) throws IOException {
		i2cFileDescriptor=nativeOpenDeviceFile(i2cDevicePath,deviceAddress);
		if(nativeExitCode!=0){
			i2cFileDescriptor=-1;
			throw new IOException();
		}
	}
	
	@Override
	public void close() throws IOException {
		if(i2cFileDescriptor>=0)
			nativeCloseDeviceFile();
	}
	
	public void writeByte(byte address,byte value)throws IOException {
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted write on a not yet initialized i2c device.");
		nativeWriteByte(address,value);
		if(nativeExitCode!=0)
			throw new IOException();
	}
	
	public byte readByte(byte address) throws IOException {
		byte readValue;
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted read on a not yet initialized i2c device.");
		readValue = nativeReadByte(address);
		if(nativeExitCode!=0)
			throw new IOException();
		return readValue;
	}
}
