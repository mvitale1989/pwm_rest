package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.Servo;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class PWMControllableDeserializer implements
		JsonDeserializer<PWMControllable> {
	
	public static Type servoType = new TypeToken<Servo>(){}.getType();

	@Override
	public PWMControllable deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		PWMControllable pwmControllable=null;
		if( json instanceof JsonObject && json.getAsJsonObject().has("type") ){
			JsonObject obj = (JsonObject) json;
			switch( obj.get("type").getAsString() ){
				case "Servo":
					pwmControllable = new ServoDeserializer().deserialize(obj, servoType, context);
					break;
				default:
					pwmControllable=null;
						
			}
		}else{
			pwmControllable=null;
		}
		return pwmControllable;
	}
}
