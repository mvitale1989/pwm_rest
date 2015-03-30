package it.binarybrain.hw;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class PWMControllable {
	@Id @GeneratedValue
	protected Long id;
	
	@ManyToOne
	protected PWMController controller;
	abstract public void setDutyCycle(float dc);
	
	public void setController(PWMController controller){ this.controller = controller; }
	public PWMController getController(){ return controller; }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
