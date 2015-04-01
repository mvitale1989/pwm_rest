package it.binarybrain.hw;

import java.io.IOException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class PWMController {
	@Id @GeneratedValue
	protected Long id;
	
	@OneToMany(mappedBy="controller")
	protected List<PWMControllable> channels;
	
	abstract public void addPWMControllable(PWMControllable device,int channel) throws IOException;
	abstract public void removePWMControllable(PWMControllable device) throws IOException;
	abstract public int getPWMControllableChannel(PWMControllable device) throws IOException;
	abstract public void setDutyCycle(PWMControllable device,float dc) throws IOException;
	abstract public void setFrequency(PWMControllable device,float hertz) throws IOException;
	abstract public void setGlobalFrequency(float hertz) throws IOException;
}
