package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.i2c.PCA9685;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class PCA9685Serializer implements JsonSerializer<PCA9685>{

	@Override
	public JsonElement serialize(PCA9685 src, Type typeOfSrc, 
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		JsonArray arr = new JsonArray();
		PWMControllableSerializer pcs = new PWMControllableSerializer();
		Type pct = new TypeToken<PWMControllable>(){}.getType() ;
		obj.addProperty("id", src.getId());
		obj.addProperty("address", src.getDeviceAddress());
		obj.addProperty("virtualDevice", src.getI2cVirtualDevice());
		for(PWMControllable channel: src.getChannels()){
			if(channel!=null)
				arr.add( pcs.serialize(channel, pct, context));
		}
		obj.add("channels", arr);
		return obj;
	}

}
