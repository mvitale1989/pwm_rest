package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.i2c.PCA9685;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PCA9685Deserializer implements JsonDeserializer<PCA9685> {

	@Override
	public PCA9685 deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		PCA9685 device = new PCA9685();
		PWMControllableDeserializer pcs = new PWMControllableDeserializer();
		if( json instanceof JsonObject ){
			JsonObject obj = (JsonObject) json;
			if( obj.has("id") )
				device.setId( obj.get("id").getAsLong() );
			if( obj.has("address") )
				device.setDeviceAddress( obj.get("address").getAsInt() );
			if( obj.has("virtualDevice") )
				device.setI2cVirtualDevice( obj.get("virtualDevice").getAsString() );
			if( obj.has("channels") ){
				for(JsonElement element: obj.get("channels").getAsJsonArray()){
					if( element.getAsJsonObject().has("channelNumber") ){
						PWMControllable pwmControllable = pcs.deserialize(element, PCA9685Serializer.pwmControllableType, context);
						try{
							device.addPWMControllable(pwmControllable, element.getAsJsonObject().get("channelNumber").getAsInt() );
						}catch(IOException e){}
					}
				}
			}
		}else{
			device = null;
		}
		return device;
	}

}
