#include <iostream>
#include <iomanip>
#include <cassert>

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

JNIEXPORT jint JNICALL Java_it_binarybrain_hw_I2CDriver_nativeOpenDeviceFile
  (JNIEnv * env, jobject obj, jstring i2c_device_path,jint device_address){

	//Bolierplate per l'accesso alle variabili istanza nativeExitCode e debug
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I"); assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	const char* i2c_device_path_cstr = env->GetStringUTFChars(i2c_device_path, JNI_FALSE);
	int i2c_address = device_address;
	int i2c_file_descriptor=-1;
	if(debug) std::cout<<"[native] Opening file descriptor: "<<i2c_device_path_cstr<<"....";
	i2c_file_descriptor = open(i2c_device_path_cstr,O_RDWR);
	if ( i2c_file_descriptor < 0) {
		if(debug) std::cout<<"failed to open the bus. ("<<strerror(errno)<<")\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	if(debug) std::cout<<"success.\n[native] Beginning communication with i2c slave of address "<<i2c_address<<"....";
	if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_address) < 0) {
		if(debug) std::cout<<"Failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	if(debug) std::cout<<"success.\n";
	env->SetIntField(obj,exit_code_fid,0);
	env->ReleaseStringUTFChars(i2c_device_path,i2c_device_path_cstr);
	std::cout.flush();
	return i2c_file_descriptor;
}



  JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_nativeCloseDeviceFile
  (JNIEnv * env, jobject obj){

	  //Bolierplate per l'accesso alle variabili istanza nativeExitCode, debug e i2cFileDescriptor
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I"); assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);
	jfieldID i2c_file_descriptor_fid = env->GetFieldID( env->GetObjectClass(obj), "i2cFileDescriptor", "I"); assert(i2c_file_descriptor_fid!=NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	jint i2c_file_descriptor=env->GetIntField(obj,i2c_file_descriptor_fid);
	if(debug) std::cout<<"[native] attempting to close the given file descriptor...";
	if(close(i2c_file_descriptor)==0){
		env->SetIntField(obj,exit_code_fid,0);
		if(debug) std::cout<<"success."<<std::endl;
	}else{
		env->SetIntField(obj,exit_code_fid,-1);
		if(debug) std::cout<<"failed. ("<<strerror(errno)<<")"<<std::endl;
	}
	std::cout.flush();
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_nativeWriteByte
  (JNIEnv * env, jobject obj, jbyte i2c_address_arg, jbyte value_arg)
{
	  //Bolierplate per l'accesso alle variabili istanza nativeExitCode, debug e i2cFileDescriptor
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I");	assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);
	jfieldID i2c_file_descriptor_fid = env->GetFieldID( env->GetObjectClass(obj), "i2cFileDescriptor", "I"); assert(i2c_file_descriptor_fid!=NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	jint i2c_file_descriptor=env->GetIntField(obj,i2c_file_descriptor_fid);
	unsigned char i2c_address = i2c_address_arg;
	unsigned char value = value_arg;
	if(debug) std::cout<<"[native] writing byte "<<std::hex<<(int)value<<" to address "<<(int)i2c_address<<"..."<<std::dec;
	if(i2c_file_descriptor<0){
		if(debug) std::cout<<"error. (I2C file descriptor not open. Did you call init?)\n"<<std::endl;
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return;
	}
	if( i2c_smbus_write_byte_data(i2c_file_descriptor,i2c_address,value) != -1 ){
		if(debug) std::cout<<"success.\n";
		env->SetIntField(obj,exit_code_fid,0);

	}else{
		if(debug) std::cout<<"error. ("<<strerror(errno)<<")"<<std::endl;
		env->SetIntField(obj,exit_code_fid,-1);
	}
	std::cout.flush();
}


JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_I2CDriver_nativeReadByte
  (JNIEnv * env, jobject obj, jbyte i2c_address)
{
	//Bolierplate per l'accesso alle variabili istanza nativeExitCode, debug e i2cFileDescriptor
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I");	assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);
	jfieldID i2c_file_descriptor_fid = env->GetFieldID( env->GetObjectClass(obj), "i2cFileDescriptor", "I"); assert(i2c_file_descriptor_fid!=NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	jint read_value=0;
	jint i2c_file_descriptor=env->GetIntField(obj,i2c_file_descriptor_fid);
	std::cout<<"Trying read from "<<std::hex<<(int)i2c_address<<" from file descriptor...";
	if(i2c_file_descriptor<0){
		std::cout<<"error (I2C file descriptor not open. Did you call init?)"<<std::endl;
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	read_value = i2c_smbus_read_byte_data(i2c_file_descriptor,i2c_address);
	if(read_value!=-1){
		std::cout<<"success.\n";
		env->SetIntField(obj,exit_code_fid,0);
	}else{
		std::cout<<"error. ("<<strerror(errno)<<")"<<std::endl;
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	std::cout.flush();
	return (jbyte)(read_value&0xFF);
}


#ifdef __cplusplus
}
#endif
#endif

