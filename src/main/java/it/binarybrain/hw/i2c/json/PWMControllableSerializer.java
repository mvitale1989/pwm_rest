package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.Servo;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class PWMControllableSerializer implements JsonSerializer<PWMControllable> {

	public static Type servoType = new TypeToken<Servo>(){}.getType();
	public static ServoSerializer servoSerializer = new ServoSerializer();
	
	@Override
	public JsonElement serialize(PWMControllable src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = null;
		if(src instanceof it.binarybrain.hw.Servo){
			Servo servo = (Servo) src;
			obj = (JsonObject) servoSerializer.serialize(servo, servoType, context);
		}else
			obj=null;
		return obj;
	}

}
