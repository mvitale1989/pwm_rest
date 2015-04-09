package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.Servo;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PWMControllableSerializer implements JsonSerializer<PWMControllable> {

	@Override
	public JsonElement serialize(PWMControllable src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		//common properties
		obj.addProperty("id", src.getId());
		obj.addProperty("controllerId", src.getController().getId());
		try{
			obj.addProperty( "channelNumber", src.getController().getPWMControllableChannel(src) );
		}catch(IOException e){}
		//subclass specific properties
		if(src instanceof it.binarybrain.hw.Servo){
			Servo servo = (Servo) src;
			obj.addProperty("type", "servo");
			obj.addProperty("degreePerSecond", servo.getDegreePerSecond());
			obj.addProperty("minAngle", servo.getMinAngle());
			obj.addProperty("maxAngle", servo.getMaxAngle());
			obj.addProperty("minAngleDutyCycle", servo.getMinAngleDutyCycle());
			obj.addProperty("maxAngleDutyCycle", servo.getMaxAngleDutyCycle());
		}
		return obj;
	}

}
