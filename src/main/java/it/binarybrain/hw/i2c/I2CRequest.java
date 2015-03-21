package it.binarybrain.hw.i2c;


public class I2CRequest {
	I2CCommunicator sender=null;
	I2CRequestType requestType=null;
	int i2cSlaveAddress=0;
	int i2cMemoryAddress=0;
	int data=0;
	//Level logLevel=null;
	
	public enum I2CRequestType {
		I2CREQUEST_WRITE, I2CREQUEST_READ, I2CREQUEST_CLOSE_DRIVER, I2CREQUEST_REOPEN_DRIVER, I2CREQUEST_CHANGE_LOGLEVEL;
	}
	
	public I2CRequest(I2CCommunicator senderArg,I2CRequestType requestTypeArg,int i2cSlaveAddressArg,int i2cMemoryAddressArg,int dataArg){
		if(senderArg==null)
			throw new IllegalArgumentException("passed null as sender in I2CRequest. It should be the proprietary I2CCommunicator's reference.");
		if(requestTypeArg==null)
			throw new IllegalArgumentException("passed null as requestType in I2CRequest.");
		sender=senderArg;
		i2cSlaveAddress=i2cSlaveAddressArg;
		i2cMemoryAddress=i2cMemoryAddressArg;
		requestType=requestTypeArg;
		data=dataArg;
		//logLevel=logLevelArg;
	}
	
	public I2CRequestType getType(){
		return requestType;
	}
	
	public I2CCommunicator getSender(){
		return sender;
	}
	
	public int getSlaveAddress(){
		return i2cSlaveAddress;
	}
	
	public int getMemoryAddress(){
		return i2cMemoryAddress;
	}
	
	public int getData(){
		return data;
	}
	
	/*public Level getLogLevel(){
		return logLevel;
	}*/
}
