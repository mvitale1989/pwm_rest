package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.Servo;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ServoSerializer implements JsonSerializer<Servo> {

	@Override
	public JsonElement serialize(Servo src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		//common properties
		obj.addProperty("id", src.getId());
		obj.addProperty("controllerId", src.getController().getId());
		try{
			obj.addProperty( "channelNumber", src.getController().getPWMControllableChannel(src) );
		}catch(IOException e){}
		//subclass specific properties
		obj.addProperty("type", "servo");
		obj.addProperty("degreePerSecond", src.getDegreePerSecond());
		obj.addProperty("minAngle", src.getMinAngle());
		obj.addProperty("maxAngle", src.getMaxAngle());
		obj.addProperty("minAngleDutyCycle", src.getMinAngleDutyCycle());
		obj.addProperty("maxAngleDutyCycle", src.getMaxAngleDutyCycle());
		obj.addProperty("clockwiseRotation", src.getClockwiseRotation());
		return obj;
	}

}
