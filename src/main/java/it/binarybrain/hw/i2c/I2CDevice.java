package it.binarybrain.hw.i2c;


/*
 * NOT USED IN THIS PROJECT
 */

//@Entity
//@Inheritance(strategy=InheritanceType.JOINED)
public class I2CDevice {
	//@Id
	protected int deviceAddress;
	//@Id
	protected String i2cVirtualDevice;
	
	public I2CDevice(){}
	public I2CDevice(String i2cVirtualDevice,int deviceAddress){
		this.i2cVirtualDevice = i2cVirtualDevice;
		this.deviceAddress = deviceAddress;
	}
	
	public int getDeviceAddress() {
		return deviceAddress;
	}
	public void setDeviceAddress(int deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	public String getI2cVirtualDevice() {
		return i2cVirtualDevice;
	}
	public void setI2cVirtualDevice(String i2cVirtualDevice) {
		this.i2cVirtualDevice = i2cVirtualDevice;
	}

}
