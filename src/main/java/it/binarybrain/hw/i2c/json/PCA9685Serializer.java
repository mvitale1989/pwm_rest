package it.binarybrain.hw.i2c.json;

import it.binarybrain.hw.i2c.PCA9685;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PCA9685Serializer implements JsonSerializer<PCA9685>{

	@Override
	public JsonElement serialize(PCA9685 src, Type typeOfSrc, 
			JsonSerializationContext context) {
		return null;
	}

}
