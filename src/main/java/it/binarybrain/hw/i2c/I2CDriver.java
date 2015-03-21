package it.binarybrain.hw.i2c;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class I2CDriver extends Thread {
	private final long waitForRequestsTimeoutMs=1000;

	private BlockingQueue<I2CRequest> requests=new LinkedBlockingQueue<I2CRequest>();
	private AtomicBoolean exit=new AtomicBoolean(false);
	private AtomicBoolean reopen=new AtomicBoolean(false);
	private String i2cDevicePath=null;
	private int i2cFileDescriptor=-1;
	private int nativeExitCode=0;
	private boolean deviceFileIsOpen=false;
	private boolean debug=false;
	//private Level logLevel=Level.INFO;

	private native int nativeOpenDeviceFile(String i2cDevicePath);
	private native void nativeCloseDeviceFile();
	private native void nativeWriteByte(byte deviceAddress, byte memoryAddress,byte value);
	private native byte nativeReadByte(byte deviceAddress, byte memoryAddress);
	static{
		System.loadLibrary("bbi2c");
	}
	
	/*
	 * CONSTRUCTORS
	 */
	public I2CDriver(String i2cDevicePathArg){
		this(i2cDevicePathArg,false);
	}
	public I2CDriver(String i2cDevicePathArg,boolean debugArg){
		i2cDevicePath=i2cDevicePathArg;
		debug=debugArg;
	}
	
	
	/*
	 * DRIVER METHODS
	 */
	private void open() throws IOException {
		if(deviceFileIsOpen == false){
			i2cFileDescriptor=nativeOpenDeviceFile(i2cDevicePath);
			if(nativeExitCode!=0){
				i2cFileDescriptor=-1;
				throw new IOException();
			}
			deviceFileIsOpen=true;
		}
	}
	
	private void close() throws IOException {
		if(i2cFileDescriptor>=0){
			nativeCloseDeviceFile();
			if(nativeExitCode!=0){
				throw new IOException("error during driver close.");
			}
			deviceFileIsOpen=false;
		}
	}
	
	private void writeByte(byte deviceAddress, byte memoryAddress, byte value) throws IOException {
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted write on a not yet initialized i2c device.");
		nativeWriteByte(deviceAddress,memoryAddress,value);
		if(nativeExitCode!=0)
			throw new IOException();
	}
	
	private byte readByte(byte deviceAddress,byte memoryAddress) throws IOException {
		byte readValue;
		if(i2cFileDescriptor<0)
			throw new IllegalStateException("attempted read on a not yet initialized i2c device.");
		readValue = nativeReadByte(deviceAddress,memoryAddress);
		if(nativeExitCode!=0)
			throw new IOException();
		return readValue;
	}
	
	
	/*
	 * LIFECYCLE METHODS
	 */
	@Override
	public void run(){
		Thread.currentThread().setName("driver_"+this.i2cDevicePath);
		if(debug) System.out.println("[driver] thread started.");
		while(!exit.get()){
			try{
				if(debug) System.out.println("[driver] opening i2c virtual file.");
				open();
				if(debug) System.out.println("[driver] fle opened. Serving requests.");
				serveRequests();
			}catch(IOException e){ e.printStackTrace(); }
			if(debug) System.out.println("[driver] request serving over. Attempting virtual file close. (exit value: "+String.valueOf(exit.get())+")");
			try{
				close();
			}catch(IOException e){ e.printStackTrace(); }
			if(debug) System.out.println("[driver] virtual file closed.");
			reopen.set(false);
		}
	}

	//Waits for and serves request messages, until thread exit or device file reopen request
	private void serveRequests(){
		I2CRequest request=null;
		I2CResponse response=null;
		while(!exit.get()&&!reopen.get()){
			I2CCommunicator sender=null;
			if(debug) System.out.println("[driver] waiting for new requests....");
			try{
				request=requests.poll(waitForRequestsTimeoutMs,TimeUnit.MILLISECONDS);
			}catch(InterruptedException e){}
			if(request!=null){
				if(debug) System.out.println("[driver] request received! Serving.");
				sender=request.getSender();
				response=serveSingleRequest(request);
				if(debug) System.out.println("[driver] operations executed. Sending feedback to requester..");
				sender.postReply(response);
			}
		}
	}
	
	//when a request is received, this function processes it and produces a response
	private I2CResponse serveSingleRequest(I2CRequest request){
		I2CResponse response=null;
		switch(request.getType()){
		case I2CREQUEST_READ:
			if(debug) System.out.println("[driver] read request received.");
			try{
				readByte( (byte)(request.getSlaveAddress()&0xFF) , (byte)(request.getData()&0xFF) );
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			}catch(IOException e){
				reopen.set(true);
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ERROR,0);
			}
			break;
		case I2CREQUEST_WRITE:
			if(debug) System.out.println("[driver] write request received.");
			try{
				writeByte( (byte)(request.getSlaveAddress()&0xFF) , (byte)(request.getMemoryAddress()&0xFF), (byte)(request.getData()&0xFF) );
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			}catch(IOException e){
				reopen.set(true);
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ERROR,0);
			}
			break;
		case I2CREQUEST_REOPEN_DRIVER:
			if(debug) System.out.println("[driver] reopen file request received.");
			reopen.set(true);
			response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			break;
		case I2CREQUEST_CLOSE_DRIVER:
			if(debug) System.out.println("[driver] close request received.");
			exit.set(true);
			response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			break;
		case I2CREQUEST_CHANGE_LOGLEVEL:
			/*logLevel=request.getLogLevel();
			response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);*/
			break;
		}
		return response;
	}
	
	
	//Function called by I2CCommunicator whenever it needs to send a request to the driver
	public void queueRequest(I2CRequest request){
		if(debug) System.out.println("[driver, queueRequest] putting new message in the queue.");
		requests.add(request);
	}
	
	
	public void signalExit(){
		if(debug) System.out.println("[driver] exit signaled!!!.");
		exit.set(true);
		interrupt();
	}
	
	public boolean getDebug(){
		return debug;
	}
}
