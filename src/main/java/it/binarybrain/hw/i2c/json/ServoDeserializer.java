package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.Servo;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ServoDeserializer implements JsonDeserializer<Servo> {

	@Override
	public Servo deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Servo servo;
		if( json instanceof JsonObject){
			servo = new Servo();
			JsonObject obj = (JsonObject) json;
			if( obj.has("id") )
				servo.setId( obj.get("id").getAsLong() );
			if( obj.has("degreePerSecond") )
				servo.setDegreePerSecond( obj.get("degreePerSecond").getAsFloat() );
			if( obj.has("minAngle") )
				servo.setMinAngle( obj.get("minAngle").getAsFloat() );
			if( obj.has("maxAngle") )
				servo.setMaxAngle( obj.get("maxAngle").getAsFloat() );
			if( obj.has("minAngleDutyCycle") )
				servo.setMinAngleDutyCycle( obj.get("minAngleDutyCycle").getAsFloat() );
			if( obj.has("maxAngleDutyCycle") )
				servo.setMaxAngleDutyCycle( obj.get("maxAngleDutyCycle").getAsFloat() );
		}else{
			servo = null;
		}
		/*JsonObject obj = new JsonObject();
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
		return obj;*/
		return servo;
	}

}
