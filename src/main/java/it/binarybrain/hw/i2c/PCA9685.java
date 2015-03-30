package it.binarybrain.hw.i2c;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.PWMController;
import it.binarybrain.hw.Servo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name="PCA9685s")
public class PCA9685 extends PWMController {
	
	@Id
	int deviceAddress=0;
	
	@Transient static final int REG_MODE1 = 0x00;
	@Transient static final int REG_MODE2 = 0x01;
	@Transient static final int REG_SUBADR1 = 0x02;
	@Transient static final int REG_SUBADR2 = 0x03;
	@Transient static final int REG_SUBADR3 = 0x04;
	@Transient static final int REG_PRESCALE = 0xFE;
	@Transient static final int REG_LED0_ON_L = 0x06;
	@Transient static final int REG_LED0_ON_H = 0x07;
	@Transient static final int REG_LED0_OFF_L = 0x08;
	@Transient static final int REG_LED0_OFF_H = 0x09;
	@Transient static final int REG_ALL_LED_ON_L = 0xFA;
	@Transient static final int REG_ALL_LED_ON_H = 0xFB;
	@Transient static final int REG_ALL_LED_OFF_L = 0xFC;
	@Transient static final int REG_ALL_LED_OFF_H = 0xFD;
	@Transient static final int BIT_RESTART = 0x80;
	@Transient static final int BIT_SLEEP = 0x10;
	@Transient static final int BIT_ALLCALL = 0x01;
	@Transient static final int BIT_INVRT = 0x10;
	@Transient static final int BIT_OUTDRV = 0x04;
	@Transient static final int MAX_PWM_STEPS = 4096;
	
	@Transient
	I2CCommunicator communicator=null;
	@Transient
	I2CDriver driver=null;
	
	@Transient
	boolean initialized=false;
	@Transient
	Logger logger = LogManager.getLogger(PCA9685.class);

	public PCA9685(I2CDriver driverArg,int deviceAddressArg) throws IOException {
		if(driverArg==null)
			throw new IllegalArgumentException("passed null as driver argument in PCA9685Driver constructor.");
		driver=driverArg;
		deviceAddress=deviceAddressArg;
		communicator=new I2CCommunicator(driver);
		channels=new ArrayList<PWMControllable>(16);
	}
	
	public int getDeviceAddress(){ return deviceAddress; }
	public void setDeviceAddress(int deviceAddress){ this.deviceAddress = deviceAddress; }
	
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
	
	public void setChannelDutyCycle(int channel,float dc) throws IOException {
		int offSteps;
		dc = (float) Math.max(0, Math.min(dc, 100.0 ));
		offSteps = Math.round( (dc * MAX_PWM_STEPS)/100 );
		if(offSteps >= MAX_PWM_STEPS)
			offSteps=MAX_PWM_STEPS-1;
		setChannelPWMSteps(channel, 0, offSteps);
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
				Thread.sleep(100);
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

	@Override
	public void setFrequency(float hertz) throws IOException {
		this.setPWMFrequency( Math.round(hertz) );
	}

	@Override
	public void addPWMControllable(PWMControllable device, int channel) throws IOException {
		if(channels.get(channel)!=null)
			throw new IOException("attempt to overwrite channel "+Integer.toString(channel)+" of PWM controller: channel already controlled! "+
								  "Remove it first.");
		channels.set(channel,device);
		device.setController(this);
	}

	@Override
	public void removePWMControllable(PWMControllable device) throws IOException {
		int channel = getChannel(device);
		channels.get(channel).setController(null);
		channels.set(channel,null);
	}

	@Override
	public void setDutyCycle(PWMControllable device, float dc) throws IOException {
		this.setChannelDutyCycle(getChannel(device),dc);
	}
	
	@Override
	public String toString(){
		StringBuilder output=new StringBuilder();
		output.append("PCA9685 at address "+Integer.toString(getDeviceAddress())+". Channels registered: ");
		for(int i=0;i<channels.size();i++){
			if(channels.get(i)!=null)
				output.append(Integer.toString(i)+" (id: "+Long.toString( ((Servo)channels.get(i)).getId() )+")");
		}
		return output.toString();
	}
}
