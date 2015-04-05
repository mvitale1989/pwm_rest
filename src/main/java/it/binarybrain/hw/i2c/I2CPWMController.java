package it.binarybrain.hw.i2c;

import it.binarybrain.hw.PWMController;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.google.gson.annotations.Expose;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class I2CPWMController extends PWMController {
	@Expose protected Integer deviceAddress;
	@Expose protected String i2cVirtualDevice;
	
	public I2CPWMController(){}
	public I2CPWMController(String i2cVirtualDevice,int deviceAddress){
		this.i2cVirtualDevice = i2cVirtualDevice;
		this.deviceAddress = deviceAddress;
	}
	
	public Integer getDeviceAddress() {
		return deviceAddress;
	}
	public void setDeviceAddress(Integer deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	public String getI2cVirtualDevice() {
		return i2cVirtualDevice;
	}
	public void setI2cVirtualDevice(String i2cVirtualDevice) {
		this.i2cVirtualDevice = i2cVirtualDevice;
	}
	



}
