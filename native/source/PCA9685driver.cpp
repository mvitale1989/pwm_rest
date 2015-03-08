/*
 * PCA9685driver.cpp
 *
 *  Created on: 07/mar/2015
 *      Author: mario
 */

#include "PCA9685driver.h"
#include <iostream>
#include <iomanip>

#include <errno.h>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>



PCA9685driver::PCA9685driver(std::string& i2c_bus_arg,int i2c_address_arg,bool debug_arg){
	i2c_bus=new std::string( i2c_bus_arg.c_str() );
	i2c_address=i2c_address_arg;
	i2c_file_descriptor=-1;
	debug=debug_arg;
	if(debug) std::cout<<"Istanzio driver. Parametri:\nBus: "<<(*i2c_bus)<<"\nDevce address: "<<i2c_address<<"\n";
}

PCA9685driver::~PCA9685driver() {
	if(i2c_file_descriptor>=0)
		close(i2c_file_descriptor);
	if(debug) std::cout<<"Distruggo driver.\n";
}

bool PCA9685driver::init(){
	return init_bus() && init_device();
}

bool PCA9685driver::init_bus(){
	bool initialization_successful=true;
	std::cout<<"Provo ad aprire "<<(*i2c_bus)<<"....";
	i2c_file_descriptor = open(i2c_bus->c_str(),O_RDWR);
	if ( i2c_file_descriptor < 0) {
		std::cout<<"failed to open the bus. ("<<strerror(errno)<<")\n";
	    initialization_successful=false;
	    return initialization_successful;
	}
	std::cout<<"successo.\nProvo a mettermi in comunicazione con lo slave "<<i2c_address<<"....";
	if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_address) < 0) {
	    std::cout<<"Failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
	    initialization_successful=false;
	    return initialization_successful;
	}
	std::cout<<"successo.\n";
	return initialization_successful;
}

bool PCA9685driver::init_device(){
	bool success=false;
	try{
		if(debug) std::cout<<"Initializing device....";
		set_all_PWM(0, 0);
		write_byte(REG_MODE2,BIT_OUTDRV);
		write_byte(REG_MODE1,BIT_ALLCALL);
		usleep(5000);//wait for oscillator
		unsigned char mode1 = read_byte(REG_MODE1);
		mode1 = mode1 & ~BIT_SLEEP;//wake up (reset sleep)
		write_byte(REG_MODE1, mode1);
		usleep(5000);//wait for oscillator
		if(debug) std::cout<<"complete.";
		success=true;
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	std::cout<<std::endl;
    return success;
}

int PCA9685driver::read_byte(int register_address){
	if(i2c_file_descriptor<0)
		throw std::exception();
	return i2c_smbus_read_byte_data(i2c_file_descriptor,register_address);
}

int PCA9685driver::write_byte(int register_address,unsigned char value){
	if(i2c_file_descriptor<0)
		throw std::exception();
	return i2c_smbus_write_byte_data(i2c_file_descriptor,register_address,value);
}

bool PCA9685driver::set_PWM_frequency(int hertz){
	bool success=false;
	try{
	    int prescale = 25000000;//25MHz
	    prescale /= 4096;//12-bit
	    prescale /= hertz;
	    prescale -= 1;
	    if (debug){
	      std::cout<<"Setting PWM frequency to "<<hertz<<"Hz; ";
	      std::cout<<"estimated pre-scale: "<<prescale<<". Sending commands...";
	    }
	    int oldmode = read_byte(REG_MODE1);
	    int newmode = (oldmode & 0x7F) | 0x10;//sleep
	    write_byte(REG_MODE1, newmode);//go to sleep
	    write_byte(REG_PRESCALE,prescale);
	    write_byte(REG_MODE1, oldmode);
	    usleep(5000);
	    write_byte(REG_MODE1, oldmode | 0x80);
	    success=true;
	    if(debug) std::cout<<"done.\n";
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;
}

bool PCA9685driver::set_PWM(int channel,int on,int off){
	bool success=false;
	try{
		if(debug) std::cout<<"Setting pwm of channel "<<channel<<". ON: "<<on<<" OFF: "<<off<<std::endl;
		write_byte(REG_LED0_ON_L+4*channel, on&0xFF);
		write_byte(REG_LED0_ON_H+4*channel, (on >> 8)&0xFF );
		write_byte(REG_LED0_OFF_L+4*channel, off&0xFF);
		write_byte(REG_LED0_OFF_H+4*channel, (off >> 8)&0xFF );
		success=true;
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;
}

bool PCA9685driver::set_all_PWM(int on,int off){
	bool success=false;
	try{
		if(debug) std::cout<<"Setting pwm of all channels. ON: "<<on<<" OFF: "<<off<<std::endl;
		write_byte(REG_ALL_LED_ON_L, on & 0xFF);
		write_byte(REG_ALL_LED_ON_H, on >> 8);
		write_byte(REG_ALL_LED_OFF_L, off & 0xFF);
		write_byte(REG_ALL_LED_OFF_H, off >> 8);
		success=true;
	}catch(std::exception& e){
		std::cout<<e.what()<<std::endl;
	}
	return success;

}

void PCA9685driver::dump_memory(){
	int reg=0;
    for(reg = 0; reg<0x46; reg++) {
        int register_content = read_byte(reg);
        if ( register_content < 0) {
            std::cout<<"Failed to read from the i2c bus address "<<reg<<" ("<<strerror(errno)<<")\n";
        } else {
            std::cout<<"Read value at address "<<std::showbase<<std::internal<<std::setfill('0')<<std::hex<<std::setw(4)<<reg<<". Data:  "<<std::hex<<std::setw(4)<<register_content<<"\n"<<std::dec;
        }
    }
}

void PCA9685driver::test(){
	if(debug){
		std::cout<<std::endl;
		std::cout<<"+ - - - - - - - - - - - - - +"<<std::endl;
		std::cout<<"| STARTING: TEST OPERATIONS |"<<std::endl;
		std::cout<<"+ - - - - - - - - - - - - - +"<<std::endl;
		std::cout<<"Performing 5 2-second long cycles to move all motor channels back and forth."<<std::endl;
	}
	int i=0;
	for( i=0; i<5; i++ ){
		if(debug) std::cout<<i+1<<".....\n";
		set_all_PWM(0,150);
		sleep(1);
		set_all_PWM(0,600);
		sleep(1);
	}
	if(debug) std::cout<<"\nTest completed.\n";
}
