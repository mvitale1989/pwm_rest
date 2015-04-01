package it.binarybrain.hw;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name="Servos")
public class Servo extends PWMControllable {
	
	private Float minAngle;
	private Float maxAngle;
	private Float minAngleDutyCycle;
	private Float maxAngleDutyCycle;
	private Float degreePerSecond;
	
	@Transient
	Logger logger = LogManager.getLogger(Servo.class);
	
	public Servo(){}
	public Servo(Servo servo){
		this.minAngle = servo.getMinAngle();
		this.maxAngle = servo.getMaxAngle();
		this.minAngleDutyCycle = servo.getMinAngleDutyCycle();
		this.maxAngleDutyCycle = servo.getMaxAngleDutyCycle();
		this.degreePerSecond = servo.getDegreePerSecond();
	}
	public Servo(float minAngle,float maxAngle,float minAngleDutyCycle,float maxAngleDutyCycle){
		this(minAngle,maxAngle,minAngleDutyCycle,maxAngleDutyCycle,0);
	}
	public Servo(float minAngle,float maxAngle,float minAngleDutyCycle,float maxAngleDutyCycle,float degreePerSecond){
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
	
	public void rotateToAngle(float angle) throws IOException {
		float targetAngle,rotationRangePercent,dutyCycle;
		targetAngle = Math.min(Math.max(minAngle,angle),maxAngle);
		rotationRangePercent = (targetAngle-minAngle)/(maxAngle-minAngle);
		dutyCycle = rotationRangePercent * (maxAngleDutyCycle-minAngleDutyCycle) + minAngleDutyCycle;
		setDutyCycle(dutyCycle);
	}
}
