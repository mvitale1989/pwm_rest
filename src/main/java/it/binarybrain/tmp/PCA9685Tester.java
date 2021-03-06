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
		String i2cVirtualDevice="/dev/i2c-1";
		byte i2cDeviceAddress=(byte)0x40;
		I2CCommunicator i2cCommunicator=null;
		PCA9685 pca9685=null;
		Servo servo1,servo2;
		try{
			logger.info("STARTING I2C AND PCA9685 TESTS");
			logger.info("Instantiating PCA9685 device on bus "+i2cVirtualDevice+", on address "+Integer.toHexString(i2cDeviceAddress)+"...");
			pca9685=new PCA9685(i2cVirtualDevice,i2cDeviceAddress);
			logger.info("Instantiating two servo instances. Assuming d-c 0.05 (1ms) min position, d-c 0.1 (2ms) max position, 0 min angle, 90 max angle ignoring degree-per-seconds).");
			servo1 = new Servo(0F,90F,0.05F,0.1F);
			servo2 = new Servo(0F,90F,0.05F,0.1F);
			pca9685.addPWMControllable(servo1,0);
			pca9685.addPWMControllable(servo2,1);
			logger.info("TESTING PERSISTENCE");
			savePCA(pca9685);
			List<PCA9685> pcas = getPCA9685s();
			logger.info("PCAs contained ");
			for(PCA9685 pca: pcas){
				logger.info(pca.toString());
			}
			logger.info("PERSISTENCE TEST SUCCESSFUL");
			try{ Thread.sleep(2000); }catch(InterruptedException e){}
			logger.info("initializing PCA9685 driver for device at address 0x"+Integer.toHexString(i2cDeviceAddress)+"...");
			pca9685.init();
			logger.info("setting PCA9685 frequency to 50Hz...");
			pca9685.setGlobalFrequency(50);
			//pca9685.setPWMFrequency(50);
			logger.info("TESTING SERVO CLASS");
				logger.info("servo1 to angle 0...");
				servo1.rotateToAngle(0);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				logger.info("servo1 to angle 45...");
				servo1.rotateToAngle(45);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				logger.info("servo1 to angle 90...");
				servo1.rotateToAngle(90);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				logger.info("servo2 to angle 0...");
				servo1.rotateToAngle(0);
				servo2.rotateToAngle(0);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				logger.info("servo2 to angle 45...");
				servo2.rotateToAngle(45);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				logger.info("servo2 to angle 90...");
				servo2.rotateToAngle(90);
				try{ Thread.sleep(2000); }catch(InterruptedException e){}
				servo2.rotateToAngle(0);
			logger.info("SERVO CLASS TESTED SUCCESSFULLY");
			logger.info("TESTING PCA9685 CLASS");
			pca9685.test();
			logger.info("PCA9685 CLASS TESTED SUCCESSFULY");
			logger.info("instantiating extra communicator for further testing...");
			i2cCommunicator = new I2CCommunicator(i2cVirtualDevice);
			logger.info("testing driver reopen command...");
			i2cCommunicator.requestReopen();
			logger.info("dumping device memory...");
			Map<Integer,Integer> memory = pca9685.dumpMemory();
			Iterator<Integer> it = memory.keySet().iterator();
			while(it.hasNext()){
				Integer value=it.next();
				logger.info("Address "+Integer.toHexString(value)+", contents: "+memory.get(value));
			}
			logger.info("TESTING SUCCESSFUL");			
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			logger.fatal("UNTRACKED EXCEPTION! Printing stack trace!");
			e.printStackTrace();
			logger.fatal("Stack trace end.");
		}
		I2CDriver.getInstance().signalExitToAll();
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
			TypedQuery<PCA9685> tq = em.createQuery("SELECT a from PCA9685 a",PCA9685.class);
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
