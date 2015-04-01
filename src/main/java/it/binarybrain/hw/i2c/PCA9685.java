package it.binarybrain.hw.i2c;

import it.binarybrain.hw.PWMControllable;
import it.binarybrain.hw.Servo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name="PCA9685s")
public class PCA9685 extends I2CPWMController {

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
	static final int MAX_PWM_STEPS = 4096;
	static final int N_CHANNELS = 16;
	
	@Transient
	I2CCommunicator communicator=null;
	@Transient
	boolean initialized=false;
	@Transient
	Logger logger = LogManager.getLogger(PCA9685.class);

	public PCA9685(){}
	public PCA9685(String i2cVirtualDevice,int deviceAddress) throws IOException {
		super(i2cVirtualDevice,deviceAddress);
		if(i2cVirtualDevice==null)
			throw new IllegalArgumentException("passed null as driver argument in PCA9685Driver constructor.");
		communicator=new I2CCommunicator(i2cVirtualDevice);
		channels=new Vector<PWMControllable>(N_CHANNELS);
		for(int i=0;i<N_CHANNELS;i++)
			channels.add(null);
	}
	
	public int getDeviceAddress(){ return deviceAddress; }
	public void setDeviceAddress(int deviceAddress){ this.deviceAddress = deviceAddress; }
	public I2CBusDriver getDriver(){ return communicator.getDriver(); }
	public void setI2cVirtualDevice(String i2cVirtualDevice){ communicator.setI2cVirtualDevice(i2cVirtualDevice); }
	
	
	/*
	 * PCA9685 specific functions
	 */
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
		dc = (float) Math.max(0, Math.min(dc, 1.0 ));
		offSteps = Math.round( (dc * MAX_PWM_STEPS) );
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
			for(int i=0;i<N_CHANNELS;i++){
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

	
	/*
	 * PWM CONTROLLER implementation functions
	 */
	public void setFrequency(PWMControllable device, float hertz)
			throws IOException {
		logger.warn("setFrequency(PWMControllable,float): PWM frequency is not channel bound. Setting global frequency instead.");
		setGlobalFrequency(hertz);
	}

	public void setGlobalFrequency(float hertz) throws IOException {
		this.setPWMFrequency( Math.round(hertz) );
	}

	public void addPWMControllable(PWMControllable device, int channel) throws IOException {
		if(channels.get(channel)!=null)
			throw new IOException("attempt to overwrite channel "+Integer.toString(channel)+" of PWM controller: channel already controlled! "+
								  "Remove it first.");
		channels.set(channel,device);
		device.setController(this);
	}

	public void removePWMControllable(PWMControllable device) throws IOException {
		int channel = getPWMControllableChannel(device);
		channels.get(channel).setController(null);
		channels.set(channel,null);
	}
	
	public int getPWMControllableChannel(PWMControllable device) throws IOException{
        int deviceIndex=-1;
        if(channels==null)
                throw new IOException("called getPWMControllableChannel on PWMController implementation before the channel vector was instantiated.");
        for(int i=0;i<channels.size();i++){
                if(channels.get(i)==device){
                        deviceIndex=i;
                        break;
                }
        }
        if(deviceIndex<0)
                throw new IOException("PWM device not declared inside PWM controller!");
        return deviceIndex;

	}

	public void setDutyCycle(PWMControllable device, float dc) throws IOException {
		this.setChannelDutyCycle(getPWMControllableChannel(device),dc);
	}
	

	
	/*
	 * OTHER FUNCTIONS
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
