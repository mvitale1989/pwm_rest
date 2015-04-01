package it.binarybrain.rest;

import it.binarybrain.hw.i2c.PCA9685;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Path("merio")
public class RestServiceResource {
	//private static Set<PCA9685> pca9685s = Collections.synchronizedSet(new HashSet<PCA9685>());
	//private static Logger logger = LogManager.getLogger(RestServiceResource.class);
	//private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pwm_rest");
	//private static EntityManager em = emf.createEntityManager();
	
	//public RestServiceResource(){
		//logger.info("REST service object instantiated.");
		//loadConfiguredDevices();
	//}
	
	/*private void loadConfiguredDevices(){
		logger.info("loading configured devices from database.");
		pca9685s.clear();
		List<PCA9685> result = null;
		em.getTransaction().begin();
		try{
			TypedQuery<PCA9685> tq = em.createQuery("SELECT a from PCA9685 a",PCA9685.class);
			result = tq.getResultList();
			em.getTransaction().commit();
			for(PCA9685 pca: result){
				pca9685s.add(pca);
			}
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
			result=null;
		}
	}*/
	
	
	
    @GET
    public String getSavedPCAs() {
    	StringBuilder response=new StringBuilder();
    	//for(PCA9685 pca: pca9685s){
    		//response.append(pca.toString());
    		response.append(" <3\n");
    	//}
        return response.toString();
    }

}