package it.binarybrain.hw;

import javax.persistence.OneToMany;


public abstract class PWMController {
	@OneToMany
	protected PWMControllable channels[];
	abstract public void addPWMControllable(PWMControllable device,int channel);
	abstract public void removePWMControllable(PWMControllable device);
	abstract public boolean setDutyCycle(PWMControllable device,float dc);
	abstract public boolean setFrequency(float hertz);
}
