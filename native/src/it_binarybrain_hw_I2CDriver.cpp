#include <iostream>
#include <iomanip>

#include <jni.h>
#include <errno.h>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>



#ifndef _Included_it_binarybrain_hw_I2CDriver
#define _Included_it_binarybrain_hw_I2CDriver
#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jint JNICALL Java_it_binarybrain_hw_I2CDriver_openFileDescriptor
  (JNIEnv * env, jobject obj, jstring i2cDevicePath,jint deviceAddress)
{
	int i2c_file_descriptor=-1;
	const char *i2c_bus = env->GetStringUTFChars(i2cDevicePath, JNI_FALSE);
	int i2c_address = deviceAddress;

	bool initialization_successful=true;
	std::cout<<"[native] Opening file descriptor: "<<i2c_bus<<"....";
	i2c_file_descriptor = open(i2c_bus,O_RDWR);
	if ( i2c_file_descriptor < 0) {
		std::cout<<"failed to open the bus. ("<<strerror(errno)<<")\n";
		initialization_successful=false;
	}else{
		std::cout<<"success.\n[native] Beginning communication with i2c slave of address "<<i2c_address<<"....";
		if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_address) < 0) {
			std::cout<<"Failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
			initialization_successful=false;
			return initialization_successful;
		}
		std::cout<<"success.\n";
	}

	env->ReleaseStringUTFChars(i2cDevicePath, i2c_bus);
	if(initialization_successful==false){
		//TODO THROW ERROR
		std::cout<<"FAILED"<<std::endl;
	}
	return i2c_file_descriptor;
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_closeFileDescriptor
  (JNIEnv * env, jobject obj, jint fileDescriptor){
	int fd=fileDescriptor;
	close(fd);
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_writeByte
  (JNIEnv * env, jobject obj, jint fileDescriptor, jbyte address_arg, jbyte value_arg)
{
	unsigned char address = address;
	unsigned char value = value_arg;
	std::cout<<"[native] writing byte "<<value<<" to address "<<address<<"...\n";
	if(fileDescriptor<0)
		throw std::exception();
	i2c_smbus_write_byte_data(fileDescriptor,address,value);
}


JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_I2CDriver_readByte
  (JNIEnv * env, jobject obj, jint fileDescriptor, jbyte address)
{
	jbyte read_value=0;
	printf("Trying read from %02x, from file descriptor...\n",address);
	if(fileDescriptor<0)
		throw std::exception();
	read_value = i2c_smbus_read_byte_data(fileDescriptor,address);
	return read_value;
}


#ifdef __cplusplus
}
#endif
#endif

