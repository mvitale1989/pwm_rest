package it.binarybrain.hw;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class PWMController {
	@Id @GeneratedValue
	protected Long id;
	
	@OneToMany//(mappedBy="controller")
	protected PWMControllable[] channels;
	abstract public void addPWMControllable(PWMControllable device,int channel) throws IOException;
	abstract public void removePWMControllable(PWMControllable device) throws IOException;
	abstract public void setDutyCycle(PWMControllable device,float dc) throws IOException;
	abstract public void setFrequency(float hertz) throws IOException;
	
	public int getChannel(PWMControllable device) throws IOException{
		int deviceIndex=-1;
		if(channels==null)
			throw new IOException("called getChannel on PWMController implementation before the channel vector was instantiated.");
		for(int i=0;i<channels.length;i++){
			if(channels[i]==device){
				deviceIndex=i;
				break;
			}
		}
		if(deviceIndex<0)
			throw new IOException("PWM device not declared inside PWM controller!");
		return deviceIndex;
	}
	
	public PWMControllable[] getAllChannels(){
		return channels;
	}
}
