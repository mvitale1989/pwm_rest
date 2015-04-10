package it.binarybrain.rest;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.Servo;
import it.binarybrain.hw.i2c.PCA9685;
import it.binarybrain.hw.i2c.json.PCA9685Deserializer;
import it.binarybrain.hw.i2c.json.PCA9685Serializer;
import it.binarybrain.hw.i2c.json.PWMControllableDeserializer;
import it.binarybrain.hw.i2c.json.PWMControllableSerializer;
import it.binarybrain.hw.i2c.json.ServoDeserializer;

import java.lang.reflect.Type;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Path("devices")
public class DeviceRestService {
	Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.registerTypeAdapter(PCA9685.class, new PCA9685Serializer())
			.registerTypeAdapter(PWMControllable.class, new PWMControllableSerializer())
			.registerTypeAdapter(PCA9685.class, new PCA9685Deserializer())
			.registerTypeAdapter(PWMControllable.class, new PWMControllableDeserializer())
			.registerTypeAdapter(Servo.class,new ServoDeserializer())
			.create();
	Type pcaList = new TypeToken<Set<PCA9685>> () {}.getType();
	Type pca = new TypeToken<PCA9685> () {}.getType();
	
    @GET
    @Produces("application/json")
    public String getDevices() {
    	StringBuilder response=new StringBuilder();
    	Set<PCA9685> pcas = PCAServoManager.getInstance().getPCA9685s();
    	response.append( gson.toJson( pcas , pcaList ) );
    	return response.toString();
    }
    
    @GET
    @Produces("application/json")
    @Path("{id: [0-9]+}")
    public String getDevice(@PathParam("id") Long id) {
    	StringBuilder response=new StringBuilder();
    	PCA9685 device = PCAServoManager.getInstance().getById(id);
    	response.append( gson.toJson( device ) );
    	return response.toString();
    }
    
    @POST
    @Consumes("application/json")
    public String setPCA(String body){
    	//StringBuilder response = new StringBuilder();
    	//response.append("BODY: "+body);
    	PCA9685 pca = gson.fromJson(body, PCA9685.class);
    	PCAServoManager.getInstance().getPCA9685s().add(pca);
    	//response.append(pca.toString());
    	return Response.ok().build().toString();
    }

}