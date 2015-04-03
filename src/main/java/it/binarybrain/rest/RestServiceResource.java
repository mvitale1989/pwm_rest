package it.binarybrain.rest;

import it.binarybrain.hw.i2c.PCA9685;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("merio")
public class RestServiceResource {
	
    @GET
    public String getSavedPCAs() {
    	StringBuilder response=new StringBuilder();
    	for(PCA9685 pca: PCA9685Manager.getInstance().getPCA9685s()){
    		response.append(pca.toString());
    		response.append(" <3\n");
    	}
        return response.toString();
    }

}