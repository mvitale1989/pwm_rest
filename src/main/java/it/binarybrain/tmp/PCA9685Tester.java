package it.binarybrain.tmp;

import it.binarybrain.hw.Servo;
import it.binarybrain.hw.i2c.I2CCommunicator;
import it.binarybrain.hw.i2c.I2CDriver;
import it.binarybrain.hw.i2c.PCA9685;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PCA9685Tester {
	private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pwm_rest");
	private final static EntityManager em = emf.createEntityManager();
	private final static Logger logger = LogManager.getLogger(PCA9685Tester.class);
	
	public static void main(String[] args) {
		boolean debug=true;
		String i2cDevicePath="/dev/i2c-1";
		byte i2cDeviceAddress=(byte)0x40;
		I2CDriver i2cDriver=null;
		I2CCommunicator i2cCommunicator=null;
		PCA9685 pca9685=null;
		try{
			logger.info("STARTING I2C AND PCA9685 TESTS.....");
			logger.info("initializing new i2cDriver thread for the virtual device "+i2cDevicePath+"...");
			i2cDriver=new I2CDriver(i2cDevicePath,debug);
			logger.info("starting control thread...");
			i2cDriver.start();
			logger.info("initializing PCA9685 driver for device at address 0x"+Integer.toHexString(i2cDeviceAddress)+"...");
			pca9685=new PCA9685(i2cDriver,i2cDeviceAddress);
			logger.info("setting PCA9685 frequency to 50Hz...");
			pca9685.setPWMFrequency(50);
			logger.info("instantiating extra communicator for further testing...");
			i2cCommunicator = new I2CCommunicator(i2cDriver);
			logger.info("testing driver reopen command...");
			i2cCommunicator.requestReopen();
			logger.info("starting PCA9685Driver testing routine...");
			pca9685.test();
			logger.info("dumping device memory...");
			Map<Integer,Integer> memory = pca9685.dumpMemory();
			Iterator<Integer> it = memory.keySet().iterator();
			while(it.hasNext()){
				Integer value=it.next();
				logger.info("Address "+Integer.toHexString(value)+", contents: "+memory.get(value));
			}
			logger.info("TESTING SUCCESSFUL");
			logger.info("TESTING PERSISTENCE");
			savePCA(pca9685);
			List<PCA9685> pcas = getPCA9685s();
			logger.info("PCAs contained ");
			for(PCA9685 pca: pcas){
				logger.info(pca.toString());
			}
			
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		i2cDriver.signalExit();
		em.close();
		emf.close();
	}
	

	public static void savePCA(PCA9685 pca9685){
		logger.info("SAVING CONFIGURATION");
		em.getTransaction().begin();
		try{
			logger.info("saving pca with address "+Integer.toString( pca9685.getDeviceAddress() ) + "to database.");
			em.merge(pca9685);
			em.getTransaction().commit();
			logger.info("pca saved.");
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}
	
	public static List<PCA9685> getPCA9685s(){
		logger.info("getting all pca9685 saved in database.");
		List<PCA9685> result = null;
		em.getTransaction().begin();
		try{
			TypedQuery<PCA9685> tq = em.createQuery("SELECT a from PCA9685s a",PCA9685.class);
			result = tq.getResultList();
			em.getTransaction().commit();
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
			result=null;
		}
		return result;
	}
	
	public static List<Servo> getServos(){
		List<Servo> result = null;
		em.getTransaction().begin();
		try{
			TypedQuery<Servo> tq = em.createQuery("SELECT a from Servos a",Servo.class);
			result = tq.getResultList();
			em.getTransaction().commit();
		}catch(PersistenceException e){
			e.printStackTrace();
			em.getTransaction().rollback();
			result=null;
		}
		return result;
	}
	
}
