package it.binarybrain.hw;

import java.io.IOException;

import javax.persistence.CascadeType;
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
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	protected PWMController controller;
	
	abstract public void setDutyCycle(float dc) throws IOException;
		
	public PWMController getController(){
		return controller;
	}
	
	public void setController(PWMController controller) {
		this.controller = controller;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
