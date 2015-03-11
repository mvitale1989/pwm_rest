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
	private native int openFileDescriptor(String i2cDevicePath,int deviceAddress);
	private native void closeFileDescriptor(int fileDescriptor);
	private native void writeByte(int fileDescriptor,byte address,byte value);
	private native byte readByte(int fileDescriptor,byte address);
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
	public void init(String i2cDevicePath,int deviceAddress) throws FileNotFoundException {
		/*File i2cDeviceFile = new File(i2cDevicePath);
		if(!i2cDeviceFile.exists())
			throw new FileNotFoundException();*/
		i2cFileDescriptor=openFileDescriptor(i2cDevicePath,deviceAddress);
		if(nativeExitCode!=0){
			System.err.println("ERRORE. NAtiveExitCode: "+Integer.toString(nativeExitCode));
			i2cFileDescriptor=-1;
			throw new FileNotFoundException();
		}
	}
	
	@Override
	public void close() throws IOException {
		if(i2cFileDescriptor>=0)
			closeFileDescriptor(i2cFileDescriptor);
	}
	
	public void writeByte(byte address,byte value) {
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted write on a not yet initialized i2c device.");
		writeByte(i2cFileDescriptor,address,value);
		if(nativeExitCode!=0){
			System.err.println("write error.");
		}
	}
	
	public byte readByte(byte address)  {
		byte readValue;
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted read on a not yet initialized i2c device.");
		readValue = readByte(i2cFileDescriptor,address);
		if(nativeExitCode!=0){
			System.err.println("read error.");
			return 0;
		}
		return readValue;
	}
}
