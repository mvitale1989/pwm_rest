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

public class PCA9685Manager {
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pwm_rest");
	private static EntityManager em = emf.createEntityManager();
	private static PCA9685Manager instance = new PCA9685Manager();
	private Set<PCA9685> pca9685s;
	private Logger logger = LogManager.getLogger(this.getClass());
	
	private PCA9685Manager(){
		logger.info("instantiating PCA9685Manager singleton...");
		pca9685s = Collections.synchronizedSet(new HashSet<PCA9685>());
		load();
		logger.info("PCA9685Manager instantiated.");
	}
	
	public static PCA9685Manager getInstance(){
		return instance;
	}
	
	public Set<PCA9685> getPCA9685s(){
		return pca9685s;
	}
	
	public void save(){
		logger.info("SAVE CALLED");
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
	
}
