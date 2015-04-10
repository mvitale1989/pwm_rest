package it.binarybrain.rest;

import it.binarybrain.hw.i2c.PCA9685;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PCAServoManager {
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pwm_rest");
	private static EntityManager em = emf.createEntityManager();
	private static PCAServoManager instance = new PCAServoManager();
	private Set<PCA9685> pca9685s;
	private Logger logger = LogManager.getLogger(this.getClass());
	
	private PCAServoManager(){
		logger.info("instantiating PCA9685Manager singleton...");
		pca9685s = Collections.synchronizedSet(new HashSet<PCA9685>());
		load();
		logger.info("PCA9685Manager instantiated.");
	}
	
	public static PCAServoManager getInstance(){
		return instance;
	}
	
	public Set<PCA9685> getPCA9685s(){
		return pca9685s;
	}
	
	public void save(){
		logger.info("saving current devices to database.");
		em.getTransaction().begin();
		try{
			for(PCA9685 pca: pca9685s){
				em.merge(pca);
				em.detach(pca);
			}
			em.getTransaction().commit();
			logger.info("devices saved successfully.");
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public void load(){
		logger.info("loading configured devices from database.");
		pca9685s.clear();
		List<PCA9685> result = null;
		em.getTransaction().begin();
		try{
			TypedQuery<PCA9685> tq = em.createQuery("SELECT a from PCA9685 a",PCA9685.class);
			result = tq.getResultList();
			em.getTransaction().commit();
			for(PCA9685 pca: result){
				logger.info("PCA found.");
				pca9685s.add(pca);
			}
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
			result=null;
		}
	}
	
	public PCA9685 getById(Long id){
    	PCA9685 device=null;
    	for(PCA9685 pca: pca9685s){
    		if(pca.getId() == id){
    			device=pca;
    			break;
    		}
    	}
		return device;
	}
	
}
