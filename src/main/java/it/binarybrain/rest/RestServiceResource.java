package it.binarybrain.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("merio")
public class RestServiceResource {
	
    @GET
    public String sayhello() {
        return "I <3 U";
    }

}