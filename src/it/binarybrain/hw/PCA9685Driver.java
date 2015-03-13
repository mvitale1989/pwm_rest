package it.binarybrain.hw;

import java.io.IOException;

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

	I2CDriver driver=null;
	boolean initializationCalled=false;
	boolean debug=false;
	
	public PCA9685Driver(I2CDriver driverArg,boolean debugArg){
		driver=driverArg;
		debug=debugArg;
	}
	
	public void init() throws IOException {
		/*bool PCA9685driver::init_device(){
        	bool success=false;
        	try{
                	if(debug) std::cout<<"Initializing device....";
                	set_all_PWM(0, 0);
                	write_byte(REG_MODE2,BIT_OUTDRV);
                	write_byte(REG_MODE1,BIT_ALLCALL);
                	usleep(5000);//wait for oscillator
                	unsigned char mode1 = read_byte(REG_MODE1);
                	mode1 = mode1 & ~BIT_SLEEP;//wake up (reset sleep)
                	write_byte(REG_MODE1, mode1);
                	usleep(5000);//wait for oscillator
                	if(debug) std::cout<<"complete.";
                	success=true;
        	}catch(std::exception& e){
                	std::cout<<e.what()<<std::endl;
        	}
        	std::cout<<std::endl;
    		return success;
			}
		 	*/
		initializationCalled=true;
		if(debug)
			System.out.print("Initializing device...");
		setAllPWMSteps(0, 0);
		driver.writeByte( (byte)(REG_MODE2), (byte)(BIT_OUTDRV) );
		driver.writeByte( (byte)(REG_MODE1), (byte)(BIT_ALLCALL) );
		try{
			Thread.sleep(5);//wait for oscillator
		}catch(InterruptedException e){}
		int mode1=driver.readByte((byte)REG_MODE1);
		mode1=mode1 & ~BIT_SLEEP;//wake up, reset sleep
		driver.writeByte((byte)REG_MODE1,(byte)mode1);
		try{
			Thread.sleep(5);//wait for oscillator
		}catch(InterruptedException e){}
		if(debug)
			System.out.println("success.");
	}
	
	public void setPWMFrequency(int hertz) throws IOException {
		/*
		 * 	bool success=false;
	try{
	    int prescale = 25000000;//25MHz
	    prescale /= 4096;//12-bit
	    prescale /= hertz;
	    prescale -= 1;
	    if (debug){
	      std::cout<<"Setting PWM frequency to "<<hertz<<"Hz; ";
	      std::cout<<"estimated pre-scale: "<<prescale<<". Sending commands...";
	    }
	    int oldmode = read_byte(REG_MODE1);
	    int newmode = (oldmode & 0x7F) | 0x10;//sleep
	    write_byte(REG_MODE1, newmode);//go to sleep
	    write_byte(REG_PRESCALE,prescale);
	    write_byte(REG_MODE1, oldmode);
	    usleep(5000);
	    write_byte(REG_MODE1, oldmode | 0x80);
	    success=true;
	    if(debug) std::cout<<"done.\n";
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;
		 */
		ensureInitialization();
	    int prescale = 25000000;//25MHz
	    prescale /= 4096;//12-bit
	    prescale /= hertz;
	    prescale -= 1;
	    if(debug){
	    	System.out.print("Setting PWM frequency to "+Integer.toString(hertz)+"; estimated pre-scale: "+Integer.toString(prescale)+". Initializing...");
	    }
	    int oldmode = driver.readByte( (byte)REG_MODE1 );
	    int newmode = (oldmode & 0x7F) | 0x10;//sleep
	    driver.writeByte((byte)REG_MODE1, (byte)newmode);//go to sleep
	    driver.writeByte((byte)REG_PRESCALE,(byte)(prescale&0xFF) );
	    driver.writeByte((byte)REG_MODE1, (byte)oldmode);
	    try{
	    	Thread.sleep(5);
	    }catch(InterruptedException e){}
	    driver.writeByte((byte)REG_MODE1, (byte)(oldmode | 0x80) );
	    System.out.print("PCA9685 device initialization complete.");
	}
	
	public void setChannelPWMSteps(int channel,int on,int off) throws IOException {
		/*
		 * 	bool success=false;
	try{
		if(debug) std::cout<<"Setting pwm of channel "<<channel<<". ON: "<<on<<" OFF: "<<off<<std::endl;
		write_byte(REG_LED0_ON_L+4*channel, on&0xFF);
		write_byte(REG_LED0_ON_H+4*channel, (on >> 8)&0xFF );
		write_byte(REG_LED0_OFF_L+4*channel, off&0xFF);
		write_byte(REG_LED0_OFF_H+4*channel, (off >> 8)&0xFF );
		success=true;
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;
		 */
		ensureInitialization();
		if(debug)
			System.out.println("Channel "+Integer.toString(channel)+", setting pwm steps to: ON="+Integer.toString(on)+" OFF="+Integer.toString(off));
		driver.writeByte( (byte)(REG_LED0_ON_L+4*channel) , (byte)(on&0xFF) );
		driver.writeByte( (byte)(REG_LED0_ON_H+4*channel) , (byte)((on>>8)&0xFF) );
		driver.writeByte( (byte)(REG_LED0_OFF_L+4*channel) , (byte)(off&0xFF) );
		driver.writeByte( (byte)(REG_LED0_OFF_H+4*channel) , (byte)((off>>8)&0xFF) );
	}
	
	public void setAllPWMSteps(int on,int off) throws IOException {
		/*
		 * 	bool success=false;
	try{
		if(debug) std::cout<<"Setting pwm of all channels. ON: "<<on<<" OFF: "<<off<<std::endl;
		write_byte(REG_ALL_LED_ON_L, on & 0xFF);
		write_byte(REG_ALL_LED_ON_H, on >> 8);
		write_byte(REG_ALL_LED_OFF_L, off & 0xFF);
		write_byte(REG_ALL_LED_OFF_H, off >> 8);
		success=true;
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;
		 */
		ensureInitialization();
		if(debug)
			System.out.println("Setting pwm steps of all channels to: ON="+Integer.toString(on)+" OFF="+Integer.toString(off));
		driver.writeByte( (byte)(REG_ALL_LED_ON_L) , (byte)(on&0xFF) );
		driver.writeByte( (byte)(REG_ALL_LED_ON_H) , (byte)((on>>8)&0xFF) );
		driver.writeByte( (byte)(REG_ALL_LED_OFF_L) , (byte)(off&0xFF) );
		driver.writeByte( (byte)(REG_ALL_LED_OFF_H) , (byte)((off>>8)&0xFF) );
	}
	
	public void setChannelDutyCycle(int channel,int percent){
		System.out.println("setChannelDutyCycle: UNIMPLEMENTED");
	}
	
	public void setAllDutyCycle(int percent){
		System.out.println("setAllDutyCycle: UNIMPLEMENTED");
	}
	
	public void dumpMemory() throws IOException {
		ensureInitialization();
		int reg=0;
		for(reg = 0; reg<0x46; reg++) {
			int register_content = driver.readByte((byte)(reg&0xFF));
			System.out.println("Address: "+Integer.toHexString(reg)+"; contents="+Integer.toHexString(register_content));
		}
	}
	
	public void test() throws IOException {
		ensureInitialization();
		try{
			System.out.println("Commencing PCA9685 test operations.");
			System.out.println("Setting all channels pwm steps to 0-150...");
			setAllPWMSteps(0,150);
			Thread.sleep(1000);
			System.out.println("Setting all channels pwm steps to 0-600...");
			setAllPWMSteps(0,600);
			Thread.sleep(1000);
			for(int i=0;i<16;i++){
				System.out.println("Setting channel "+Integer.toString(i)+" pwm steps to 0-150...");
				this.setChannelPWMSteps(i, 0, 150);
				Thread.sleep(500);
			}
		}catch(InterruptedException e){}
		System.out.println("PC9685 test operations ended. Turning all pwm off.");
		this.setAllPWMSteps(0, 0);
	}
	
	public boolean isInitialized(){
		return initializationCalled;
	}
	
	public void ensureInitialization() throws IOException {
		if(!initializationCalled)
			init();
	}
}
