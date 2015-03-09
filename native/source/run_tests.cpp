#include "PCA9685driver.h"
#include<iostream>
#include<string>
using namespace std;

int main(){
	string i2c_bus = "/dev/i2c-1";
	int device_address=0x40;
	PCA9685driver* d=new PCA9685driver(i2c_bus,device_address,true);
	d->print_test();
	try{
		d->init();
		d->dump_memory();
		d->set_PWM_frequency(50);
		//d->set_PWM(0,0,256);
		//d->set_PWM(1,0,300);
		d->test();
		d->dump_memory();
		d->set_all_PWM(0,0);
	}catch(exception e){
		cerr<<"TEST FAILED\nAn unexpected error occurred during tests. Aborting."<<endl;
	}
	return 0;
}
