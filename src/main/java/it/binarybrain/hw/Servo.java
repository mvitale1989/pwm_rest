package it.binarybrain.hw;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Servos")
public class Servo extends PWMControllable {
	@Id @GeneratedValue
	private Long id;
	
	private Float minAngle;
	private Float maxAngle;
	private Float minAngleDutyCycle;
	private Float maxAngleDutyCycle;
	private Float degreePerSecond;
	
	public Servo(){}
	public Servo(PWMController controller){ this.controller = controller; }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PWMController getController() {
		return controller;
	}

	public void setController(PWMController controller) {
		this.controller = controller;
	}

	public Float getMinAngle() {
		return minAngle;
	}

	public void setMinAngle(Float minAngle) {
		this.minAngle = minAngle;
	}

	public Float getMaxAngle() {
		return maxAngle;
	}

	public void setMaxAngle(Float maxAngle) {
		this.maxAngle = maxAngle;
	}

	public Float getMinAngleDutyCycle() {
		return minAngleDutyCycle;
	}

	public void setMinAngleDutyCycle(Float minAngleDutyCycle) {
		this.minAngleDutyCycle = minAngleDutyCycle;
	}

	public Float getMaxAngleDutyCycle() {
		return maxAngleDutyCycle;
	}

	public void setMaxAngleDutyCycle(Float maxAngleDutyCycle) {
		this.maxAngleDutyCycle = maxAngleDutyCycle;
	}

	public Float getDegreePerSecond() {
		return degreePerSecond;
	}

	public void setDegreePerSecond(Float degreePerSecond) {
		this.degreePerSecond = degreePerSecond;
	}
	
	@Override
	public void setDutyCycle(float dc) {
		// TODO Auto-generated method stub
		// TODO DA FAREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
	}
	
	public void rotateToAngle(float angle){
		// TODO DA FAREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
	}
}
