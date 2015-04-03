package it.binarybrain.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("devices")
public class RestServiceResource {
	ObjectMapper mapper = new ObjectMapper();
	
    @GET
    @Produces("application/json")
    public String getSavedPCAs() {
    	/*StringBuilder response=new StringBuilder();
    	for( PCA9685 pca: PCA9685Manager.getInstance().getPCA9685s() ){
    		response.append(pca.toString());
    		response.append(" <3\n");
    	}
        return response.toString();*/
    	String reply=null;
    	try{
    		reply = mapper.writeValueAsString( PCAServoManager.getInstance().getPCA9685s() );
    	}catch(JsonProcessingException e){
    		e.printStackTrace();
    		reply = "{}";
    	}
    	return reply;
    }

}