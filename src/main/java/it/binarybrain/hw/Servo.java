package it.binarybrain.hw;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="Servos")
public class Servo extends PWMControllable {
	
	private Float minAngle;
	private Float maxAngle;
	private Float minAngleDutyCycle;
	private Float maxAngleDutyCycle;
	private Float degreePerSecond;
	
	public Servo(){}
	public Servo(PWMController controller){ this.controller = controller; }
	public Servo(PWMController controller,Servo servo){
		this.controller = controller;
		this.minAngle = servo.getMinAngle();
		this.maxAngle = servo.getMaxAngle();
		this.minAngleDutyCycle = servo.getMinAngleDutyCycle();
		this.maxAngleDutyCycle = servo.getMaxAngleDutyCycle();
		this.degreePerSecond = servo.getDegreePerSecond();
	}
	public Servo(PWMController controller,float minAngle,float maxAngle,float minAngleDutyCycle,float maxAngleDutyCycle,float degreePerSecond){
		this.controller = controller;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.minAngleDutyCycle = minAngleDutyCycle;
		this.maxAngleDutyCycle = maxAngleDutyCycle;
		this.degreePerSecond = degreePerSecond;
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
	public void setDutyCycle(float dc) throws IOException {
		controller.setDutyCycle(this, dc);
	}
	
	public void rotateToAngle(float angle){
		// TODO: fare i calcoli in base ai dati del motore e settare il duty cycle di conseguenza
	}
}
