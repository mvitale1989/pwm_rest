package it.binarybrain.hw.i2c;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import it.binarybrain.hw.i2c.I2CResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class I2CDriver extends Thread {
	private final long waitForRequestsTimeoutMs=5000;
	private final long waitForReopenMs=200;

	private BlockingQueue<I2CRequest> requests=new LinkedBlockingQueue<I2CRequest>();
	private AtomicBoolean exit=new AtomicBoolean(false);
	private AtomicBoolean reopen=new AtomicBoolean(false);
	private String i2cDevicePath=null;
	private int i2cFileDescriptor=-1;
	private int nativeExitCode=0;
	private boolean deviceFileIsOpen=false;
	private boolean debug=false;
	private Logger logger=LogManager.getLogger(I2CDriver.class);

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
		logger.trace("driver thread started.");
		while(!exit.get()){
			try{
				logger.info("opening i2c virtual file.");
				open();
				logger.info("virtual fle opened.");
				serveRequests();
			}catch(IOException e){}
			logger.info("request serving over. Attempting virtual file close. (exit value: "+String.valueOf(exit.get())+")");
			try{
				close();
			}catch(IOException e){ e.printStackTrace(); }
			logger.info("virtual file closed.");
			reopen.set(false);
			if(!exit.get()){
				try{ Thread.sleep(waitForReopenMs); }catch(InterruptedException e){}
			}
		}
	}

	//Waits for and serves request messages, until thread exit or device file reopen request
	private void serveRequests(){
		I2CRequest request=null;
		I2CResponse response=null;
		while(!exit.get()&&!reopen.get()){
			I2CCommunicator sender=null;
			logger.trace("waiting for new requests....");
			try{
				request=requests.poll(waitForRequestsTimeoutMs,TimeUnit.MILLISECONDS);
			}catch(InterruptedException e){}
			if(request!=null){
				logger.info("request received! Serving.");
				sender=request.getSender();
				response=serveSingleRequest(request);
				logger.trace("operations executed. Sending feedback to requester..");
				sender.postReply(response);
			}
		}
	}
	
	//when a request is received, this function processes it and produces a response
	private I2CResponse serveSingleRequest(I2CRequest request){
		I2CResponse response=null;
		byte slaveAddress=(byte)(request.getSlaveAddress()&0xFF);
		byte memoryAddress=(byte)(request.getMemoryAddress()&0xFF);
		byte data=(byte)(request.getData()&0xFF);
		switch(request.getType()){
		case I2CREQUEST_READ:
			logger.info("read request received.");
			try{
				int value=readByte( slaveAddress , memoryAddress );
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_READ_VALUE,value&0xFF);
			}catch(IOException e){
				reopen.set(true);
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ERROR,0);
			}
			break;
		case I2CREQUEST_WRITE:
			logger.info("write request received.");
			try{
				writeByte( slaveAddress , memoryAddress , data );
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			}catch(IOException e){
				reopen.set(true);
				response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ERROR,0);
			}
			break;
		case I2CREQUEST_REOPEN_DRIVER:
			logger.info("reopen file request received.");
			reopen.set(true);
			response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			break;
		case I2CREQUEST_CLOSE_DRIVER:
			logger.info("close request received.");
			exit.set(true);
			response=new I2CResponse(I2CResponse.I2CResponseType.I2CRESPONSE_ACK,0);
			break;
		}
		return response;
	}
	
	
	//Function called by I2CCommunicator whenever it needs to send a request to the driver
	public void queueRequest(I2CRequest request){
		logger.trace("putting new message in driver's queue...");
		requests.add(request);
	}
	
	
	public void signalExit(){
		logger.info("sent exit signal to driver.");
		exit.set(true);
		interrupt();
	}
	
	public boolean getDebug(){
		return debug;
	}
}
