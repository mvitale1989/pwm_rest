package it.binarybrain.rest;

import it.binarybrain.hw.i2c.PCA9685;

import java.lang.reflect.Type;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Path("devices")
public class RestServiceResource {
	Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	Type pcaList = new TypeToken<Set<PCA9685>> () {}.getType();
	
    @GET
    @Produces("application/json")
    public String getSavedPCAs() {
    	StringBuilder response=new StringBuilder();
    	Set<PCA9685> pcas = PCAServoManager.getInstance().getPCA9685s();
    	response.append( gson.toJson( pcas , pcaList ) );
    	return response.toString();
    }
    
    /*@POST
    @Produces("application/json")
    public String setPCA(String body){
    	StringBuilder response = new StringBuilder();
    	response.append("BODY: "+body);
    	PCA9685 pca = gson.fromJson(body, PCA9685.class);
    	response.append(pca.toString());
    	return response.toString();
    }*/

}