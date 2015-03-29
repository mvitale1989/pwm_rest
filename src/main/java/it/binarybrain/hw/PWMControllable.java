package it.binarybrain.hw;

import javax.persistence.ManyToOne;


public abstract class PWMControllable {
	@ManyToOne
	protected PWMController controller;
	abstract public void setDutyCycle(float dc);
	
	public void setController(PWMController controller){ this.controller = controller; }
	public PWMController getController(){ return controller; }
}
