package it.binarybrain.hw.i2c;

import it.binarybrain.hw.i2c.I2CRequest.I2CRequestType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class I2CCommunicator {

	private I2CDriver driver=null;
	private long waitForResponseTimeoutMs=1000;
	BlockingQueue<I2CResponse> responses=new LinkedBlockingQueue<I2CResponse>();
	Logger logger= LogManager.getLogger(I2CCommunicator.class);

	public I2CCommunicator(I2CDriver driverArg){
		if(driverArg==null)
			throw new IllegalArgumentException("passed null as I2CCommunicator driver argument.");
		driver=driverArg;
		logger.trace("communicator instantiated.");
	}

	public Integer readByte(int i2cSlaveAddress,int i2cMemoryAddress){
		logger.trace("called readByte function of the communicator. Creating request for driver. (slave address: "+
				Integer.valueOf(i2cSlaveAddress)+", memory address: "+Integer.valueOf(i2cMemoryAddress)+")");
		I2CRequest request=new I2CRequest(this,I2CRequestType.I2CREQUEST_READ,i2cSlaveAddress,i2cMemoryAddress,0);
		I2CResponse response=null;
		logger.trace("request created. Queueing request to the driver.");
		driver.queueRequest(request);
		logger.info("readByte request queued. Waiting for response. (timeout: "+Long.valueOf(waitForResponseTimeoutMs)+")");
		try{
			response=responses.poll(waitForResponseTimeoutMs,TimeUnit.MILLISECONDS);
		}catch(InterruptedException e){}
		if(response!=null&&response.getType()==I2CResponse.I2CResponseType.I2CRESPONSE_READ_VALUE){
			logger.trace("READ response received from driver.");
			return response.getReadValue();
		}else{
			logger.warn("driver did not respond, or response is of the wrong type.");
			return null;
		}
	}
	
	public boolean writeByte(int i2cSlaveAddress,int i2cMemoryAddress,int value){
		logger.trace("called writeByte function of the communicator. Creating request for driver. (slave address: "+
				Integer.valueOf(i2cSlaveAddress)+", memory address: "+Integer.valueOf(i2cMemoryAddress)+", value= "+
				Integer.valueOf(value)+")");
		I2CRequest request=new I2CRequest(this,I2CRequestType.I2CREQUEST_WRITE,i2cSlaveAddress,i2cMemoryAddress,value);
		I2CResponse response=null;
		boolean success=false;
		logger.trace("request created. Queueing request to the driver.");
		driver.queueRequest(request);
		logger.info("writeByte request queued. Waiting for response. (timeout: "+Long.valueOf(waitForResponseTimeoutMs)+")");
		try{
			response=responses.poll(waitForResponseTimeoutMs,TimeUnit.MILLISECONDS);
		}catch(InterruptedException e){}
		if(response!=null){
			logger.trace("WRITE response received from driver.");
			success=true;
		}else
			logger.warn("driver did not respond, or response is of the wrong type.");
		return success;
	}
	
	public void postReply(I2CResponse response){
		logger.trace("putting response in communicator's queue...");
		responses.offer(response);
	}
	
}
