package it.binarybrain.hw.i2c;

public class I2CResponse {
	
	I2CResponseType responseType=null;
	int readValue=0;
	
	public enum I2CResponseType {
		I2CRESPONSE_ACK, I2CRESPONSE_READ_VALUE, I2CRESPONSE_ERROR;
	}
	
	public I2CResponse(I2CResponseType responseTypeArg,int readValueArg){
		if(responseTypeArg==null)
			throw new IllegalArgumentException("passed null as responseType argument.");
		responseType=responseTypeArg;
		readValue=readValueArg;
	}
	
	public I2CResponseType getType(){
		return responseType;
	}
	
	public int getReadValue(){
		return readValue;
	}
	
}
