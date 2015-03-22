package it.binarybrain.hw.i2c;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PCA9685Driver {
	
	static final int REG_MODE1 = 0x00;
	static final int REG_MODE2 = 0x01;
	static final int REG_SUBADR1 = 0x02;
	static final int REG_SUBADR2 = 0x03;
	static final int REG_SUBADR3 = 0x04;
	static final int REG_PRESCALE = 0xFE;
	static final int REG_LED0_ON_L = 0x06;
	static final int REG_LED0_ON_H = 0x07;
	static final int REG_LED0_OFF_L = 0x08;
	static final int REG_LED0_OFF_H = 0x09;
	static final int REG_ALL_LED_ON_L = 0xFA;
	static final int REG_ALL_LED_ON_H = 0xFB;
	static final int REG_ALL_LED_OFF_L = 0xFC;
	static final int REG_ALL_LED_OFF_H = 0xFD;
	static final int BIT_RESTART = 0x80;
	static final int BIT_SLEEP = 0x10;
	static final int BIT_ALLCALL = 0x01;
	static final int BIT_INVRT = 0x10;
	static final int BIT_OUTDRV = 0x04;

	I2CCommunicator communicator=null;
	I2CDriver driver=null;
	int deviceAddress=0;
	boolean initialized=false;
	Logger logger = LogManager.getLogger(PCA9685Driver.class);

	public PCA9685Driver(I2CDriver driverArg,int deviceAddressArg) throws IOException {
		if(driverArg==null)
			throw new IllegalArgumentException("passed null as driver argument in PCA9685Driver constructor.");
		driver=driverArg;
		deviceAddress=deviceAddressArg;
		communicator=new I2CCommunicator(driver);
	}
	
	public void init() throws IOException {
		initialized=true;
		try{
			logger.trace("initializing device.");
			setAllPWMSteps(0, 0);
			communicator.writeByte( deviceAddress, REG_MODE2, BIT_OUTDRV );
			communicator.writeByte( deviceAddress, REG_MODE1, BIT_ALLCALL );
			try{
				Thread.sleep(5);//wait for oscillator
			}catch(InterruptedException e){}
			int mode1= communicator.readByte( deviceAddress, REG_MODE1 );
			mode1=mode1 & ~BIT_SLEEP;//wake up, reset sleep
			communicator.writeByte( deviceAddress, REG_MODE1, mode1 );
			try{
				Thread.sleep(5);//wait for oscillator
			}catch(InterruptedException e){}
			logger.trace("initialization successful.");
		}catch(IOException | NullPointerException e){
			initialized=false;
			throw e;
		}
	}
	
	public void setPWMFrequency(int hertz) throws IOException {
		ensureInitialization();
	    int prescale = 25000000;//25MHz
	    prescale /= 4096;//12-bit
	    prescale /= hertz;
	    prescale -= 1;
	    logger.trace("setting PWM frequency to "+Integer.toString(hertz)+"; estimated pre-scale: "+Integer.toString(prescale)+". Initializing...");
	    int oldmode = communicator.readByte( deviceAddress, REG_MODE1 );
	    int newmode = (oldmode & 0x7F) | 0x10;//sleep
	    communicator.writeByte( deviceAddress, REG_MODE1, newmode);//go to sleep
	    communicator.writeByte( deviceAddress,REG_PRESCALE, prescale );
	    communicator.writeByte( deviceAddress,REG_MODE1, oldmode);
	    try{
	    	Thread.sleep(5);
	    }catch(InterruptedException e){}
	    communicator.writeByte( deviceAddress , REG_MODE1, (oldmode | 0x80) );
	    logger.info("PCA9685 device initialization complete.");
	}
	
	public void setChannelPWMSteps(int channel,int on,int off) throws IOException {
		ensureInitialization();
		logger.trace("channel "+Integer.toString(channel)+", setting pwm steps to: ON="+Integer.toString(on)+" OFF="+Integer.toString(off));
		communicator.writeByte( deviceAddress, REG_LED0_ON_L+4*channel , (on&0xFF) );
		communicator.writeByte( deviceAddress, REG_LED0_ON_H+4*channel , ((on>>8)&0xFF) );
		communicator.writeByte( deviceAddress, REG_LED0_OFF_L+4*channel , (off&0xFF) );
		communicator.writeByte( deviceAddress, REG_LED0_OFF_H+4*channel , ((off>>8)&0xFF) );
	}
	
	public void setAllPWMSteps(int on,int off) throws IOException {
		ensureInitialization();
		logger.trace("setting pwm steps of all channels to: ON="+Integer.toString(on)+" OFF="+Integer.toString(off));
		communicator.writeByte( deviceAddress, REG_ALL_LED_ON_L , (on&0xFF) );
		communicator.writeByte( deviceAddress, REG_ALL_LED_ON_H , ((on>>8)&0xFF) );
		communicator.writeByte( deviceAddress, REG_ALL_LED_OFF_L , (off&0xFF) );
		communicator.writeByte( deviceAddress, REG_ALL_LED_OFF_H , ((off>>8)&0xFF) );
	}
	
	public void setChannelDutyCycle(int channel,int percent){
		logger.warn("setChannelDutyCycle: UNIMPLEMENTED");
	}
	
	public void setAllDutyCycle(int percent){
		logger.warn("setAllDutyCycle: UNIMPLEMENTED");
	}
	
	public Map<Integer,Integer> dumpMemory() throws IOException {
		ensureInitialization();
		Map<Integer,Integer> memory = new HashMap<Integer,Integer>();
		int reg=0;
		for(reg = 0; reg<0x46; reg++) {
			int register_content = communicator.readByte( deviceAddress, reg );
			memory.put(reg, register_content);
		}
		return memory;
	}
	
	public void test() throws IOException {
		ensureInitialization();
		try{
			logger.info("Commencing PCA9685 test operations.");
			logger.info("Setting all channels pwm steps to 0-150...");
			setAllPWMSteps(0,150);
			Thread.sleep(2000);
			logger.info("Setting all channels pwm steps to 0-600...");
			setAllPWMSteps(0,600);
			Thread.sleep(2000);
			for(int i=0;i<16;i++){
				logger.info("Setting channel "+Integer.toString(i)+" pwm steps to 0-150...");
				this.setChannelPWMSteps(i, 0, 150);
				Thread.sleep(500);
			}
		}catch(InterruptedException e){}
		logger.info("PC9685 test operations ended. Turning all pwm off.");
		this.setAllPWMSteps(0, 0);
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	public void ensureInitialization() throws IOException {
		if(!initialized)
			init();
	}
}
