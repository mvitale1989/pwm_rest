package it.binarybrain.hw.i2c;

import it.binarybrain.hw.i2c.I2CRequest.I2CRequestType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class I2CCommunicator {

	private I2CDriver driver=null;
	private long waitForResponseTimeoutMs=1000;
	BlockingQueue<I2CResponse> responses=new LinkedBlockingQueue<I2CResponse>();
	boolean debug=false;
	
	public I2CCommunicator(I2CDriver driverArg){
		if(driverArg==null)
			throw new IllegalArgumentException("passed null as I2CCommunicator driver argument.");
		driver=driverArg;
		debug=driver.getDebug();
		if(debug) System.out.println("[communicator] communicator instantiated.");
	}
	
	public Integer readByte(int i2cSlaveAddress,int i2cMemoryAddress){
		if(debug) System.out.println("[communicator] called readByte function of the communicator. Creating request...");
		I2CRequest request=new I2CRequest(this,I2CRequestType.I2CREQUEST_READ,i2cSlaveAddress,i2cMemoryAddress,0);
		I2CResponse response=null;
		if(debug) System.out.println("[communicator] request created. Queueing request to the driver.");
		driver.queueRequest(request);
		if(debug) System.out.println("[communicator] request queued. Waiting for response...");
		try{
			response=responses.poll(waitForResponseTimeoutMs,TimeUnit.MILLISECONDS);
		}catch(InterruptedException e){}
		if(response!=null&&response.getType()==I2CResponse.I2CResponseType.I2CRESPONSE_READ_VALUE)
			return response.getReadValue();
		else
			return null;
	}
	
	public boolean writeByte(int i2cSlaveAddress,int i2cMemoryAddress,int value){
		if(debug) System.out.println("[communicator] called writeByte function of the communicator. Creating request...");
		I2CRequest request=new I2CRequest(this,I2CRequestType.I2CREQUEST_WRITE,i2cSlaveAddress,i2cMemoryAddress,value);
		I2CResponse response=null;
		boolean success=false;
		if(debug) System.out.println("[communicator] request created. Queueing request to the driver.");
		driver.queueRequest(request);
		if(debug) System.out.println("[communicator] request queued. Waiting for response...");
		try{
			response=responses.poll(waitForResponseTimeoutMs,TimeUnit.MILLISECONDS);
		}catch(InterruptedException e){}
		if(response!=null)
			success=true;
		return success;
	}
	
	public void postReply(I2CResponse response){
		if(debug) System.out.println("[communicator, postReply] received reply from driver!");
		responses.offer(response);
	}
	
}
