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
  (JNIEnv * env, jobject obj, jstring i2c_device_path,jint device_address,jboolean debug)
{
	int i2c_file_descriptor=-1;
	const char* i2c_device_path_cstr = env->GetStringUTFChars(i2c_device_path, JNI_FALSE);
	int i2c_address = device_address;

	bool initialization_successful=true;
	if(debug) std::cout<<"[native] Opening file descriptor: "<<i2c_device_path_cstr<<"....";
	i2c_file_descriptor = open(i2c_device_path_cstr,O_RDWR);
	if ( i2c_file_descriptor < 0) {
		if(debug) std::cout<<"failed to open the bus. ("<<strerror(errno)<<")\n";
		initialization_successful=false;
	}else{
		if(debug) std::cout<<"success.\n[native] Beginning communication with i2c slave of address "<<i2c_address<<"....";
		if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_address) < 0) {
			if(debug) std::cout<<"Failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
			initialization_successful=false;
			return initialization_successful;
		}
		if(debug) std::cout<<"success.\n";
	}

	env->ReleaseStringUTFChars(i2c_device_path, i2c_device_path_cstr);
	if(initialization_successful==false){
		env->ThrowNew(env->FindClass("it/binarybrain/hw/I2CDriver/I2CDriverException"),"[native][EXCEPTION] driver initialization failed.");
	}
	return i2c_file_descriptor;
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_closeFileDescriptor
  (JNIEnv * env, jobject obj, jint i2c_file_descriptor,jboolean debug){
	int fd=i2c_file_descriptor;
	close(fd);
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_writeByte
  (JNIEnv * env, jobject obj, jint i2c_file_descriptor, jbyte i2c_address_arg, jbyte value_arg,jboolean debug)
{
	unsigned char i2c_address = i2c_address_arg;
	unsigned char value = value_arg;
	std::cout<<"[native] writing byte "<<value<<" to address "<<i2c_address<<"...\n";
	if(i2c_file_descriptor<0)
		env->ThrowNew(env->FindClass("it/binarybrain/hw/I2CDriver/I2CDriverException"),"[native][EXCEPTION] I2C file descriptor not open. Did you call init?");
	i2c_smbus_write_byte_data(i2c_file_descriptor,i2c_address,value);
}


JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_I2CDriver_readByte
  (JNIEnv * env, jobject obj, jint i2c_file_descriptor, jbyte i2c_address,jboolean debug)
{
	jbyte read_value=0;
	printf("Trying read from %02x, from file descriptor...\n",i2c_address);
	if(i2c_file_descriptor<0)
		env->ThrowNew(env->FindClass("it/binarybrain/hw/I2CDriver/I2CDriverException"),"[native][EXCEPTION] I2C file descriptor not open. Did you call init?");
	read_value = i2c_smbus_read_byte_data(i2c_file_descriptor,i2c_address);
	return read_value;
}


#ifdef __cplusplus
}
#endif
#endif

