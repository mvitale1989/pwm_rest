package it.binarybrain.hw.i2c.json;

import java.lang.reflect.Type;

import it.binarybrain.hw.i2c.PCA9685;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PCA9685Deserializer implements JsonDeserializer<PCA9685> {

	@Override
	public PCA9685 deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		return null;
	}

}
