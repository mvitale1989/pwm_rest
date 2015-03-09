/*
 * PCA9685driver.h
 *
 *  Created on: 07/mar/2015
 *      Author: mario
 */

#include<string>
#include<iostream>

#ifndef PCA9685DRIVER_H_
#define PCA9685DRIVER_H_

class PCA9685driver {
private:
	static const int REG_MODE1 = 0x00;
	static const int REG_MODE2 = 0x01;
	static const int REG_SUBADR1 = 0x02;
	static const int REG_SUBADR2 = 0x03;
	static const int REG_SUBADR3 = 0x04;
	static const int REG_PRESCALE = 0xFE;
	static const int REG_LED0_ON_L = 0x06;
	static const int REG_LED0_ON_H = 0x07;
	static const int REG_LED0_OFF_L = 0x08;
	static const int REG_LED0_OFF_H = 0x09;
	static const int REG_ALL_LED_ON_L = 0xFA;
	static const int REG_ALL_LED_ON_H = 0xFB;
	static const int REG_ALL_LED_OFF_L = 0xFC;
	static const int REG_ALL_LED_OFF_H = 0xFD;

	static const int BIT_RESTART = 0x80;
	static const int BIT_SLEEP = 0x10;
	static const int BIT_ALLCALL = 0x01;
	static const int BIT_INVRT = 0x10;
	static const int BIT_OUTDRV = 0x04;

	std::string* i2c_bus;
	int i2c_file_descriptor;
	int i2c_address;
	bool debug;
public:
	PCA9685driver(std::string& i2c_bus_arg,int i2c_address_arg,bool debug_arg=true);
	virtual ~PCA9685driver();
	bool init();
	bool init_bus();
	bool init_device();
	int read_byte(int register_address);
	int write_byte(int register_address,unsigned char value);
	bool set_PWM_frequency(int hertz);
	bool set_PWM(int channel,int on,int off);
	bool set_all_PWM(int on,int off);
	void dump_memory();
	void test();
	void print_test();
};

#endif /* PCA9685DRIVER_H_ */
