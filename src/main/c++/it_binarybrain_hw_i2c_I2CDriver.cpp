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

JNIEXPORT jint JNICALL Java_it_binarybrain_hw_i2c_I2CDriver_nativeOpenDeviceFile
  (JNIEnv * env, jobject obj, jstring i2c_device_path){

	//Bolierplate per l'accesso alle variabili istanza nativeExitCode e debug
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I"); assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	const char* i2c_device_path_cstr = env->GetStringUTFChars(i2c_device_path, JNI_FALSE);
	int i2c_file_descriptor=-1;
	if(debug) std::cout<<"[native] Opening virtual file: "<<i2c_device_path_cstr<<"....";
	i2c_file_descriptor = open(i2c_device_path_cstr,O_RDWR);
	if ( i2c_file_descriptor < 0) {
		if(debug) std::cout<<"failed to open the bus. ("<<strerror(errno)<<")\n";
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



JNIEXPORT void JNICALL Java_it_binarybrain_hw_i2c_I2CDriver_nativeCloseDeviceFile
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
		if(debug) std::cout<<"success.\n";
	}else{
		env->SetIntField(obj,exit_code_fid,-1);
		if(debug) std::cout<<"failed. ("<<strerror(errno)<<")\n";
	}
	std::cout.flush();
}


JNIEXPORT void JNICALL Java_it_binarybrain_hw_i2c_I2CDriver_nativeWriteByte
  (JNIEnv * env, jobject obj, jbyte i2c_device_address, jbyte i2c_memory_address, jbyte value)
{
	//Bolierplate per l'accesso alle variabili istanza nativeExitCode, debug e i2cFileDescriptor
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I");	assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);
	jfieldID i2c_file_descriptor_fid = env->GetFieldID( env->GetObjectClass(obj), "i2cFileDescriptor", "I"); assert(i2c_file_descriptor_fid!=NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	jint i2c_file_descriptor=env->GetIntField(obj,i2c_file_descriptor_fid);
	if(debug) std::cout<<"[native] Setting up file descriptor for communication with device at address 0x"<<std::hex<<(int)(i2c_device_address&0xFF)<<"....";
	if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_device_address) < 0) {
		if(debug) std::cout<<"failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return;
	}
	if(debug) std::cout<<"success.\n[native] writing value 0x"<<std::hex<<(int)(value&0xFF)<<" to address 0x"<<std::hex<<(int)(i2c_memory_address&0xFF)<<"..."<<std::dec;
	if(i2c_file_descriptor<0){
		if(debug) std::cout<<"error. (I2C file descriptor not open. Did you call init?)\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return;
	}
	if( i2c_smbus_write_byte_data(i2c_file_descriptor,(unsigned char)(i2c_memory_address&0xFF),(unsigned char)(value&0xFF)) != -1 ){
		if(debug) std::cout<<"success.\n";
		env->SetIntField(obj,exit_code_fid,0);

	}else{
		if(debug) std::cout<<"error. ("<<strerror(errno)<<")\n";
		env->SetIntField(obj,exit_code_fid,-1);
	}
	std::cout.flush();
}


JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_i2c_I2CDriver_nativeReadByte
  (JNIEnv * env, jobject obj, jbyte i2c_device_address, jbyte i2c_memory_address)
{
	//Bolierplate per l'accesso alle variabili istanza nativeExitCode, debug e i2cFileDescriptor
	jfieldID exit_code_fid = env->GetFieldID( env->GetObjectClass(obj), "nativeExitCode", "I");	assert(exit_code_fid != NULL);
	jfieldID debug_fid = env->GetFieldID( env->GetObjectClass(obj), "debug", "Z"); assert(debug_fid != NULL);
	jfieldID i2c_file_descriptor_fid = env->GetFieldID( env->GetObjectClass(obj), "i2cFileDescriptor", "I"); assert(i2c_file_descriptor_fid!=NULL);

	//Codice funzionale
	jboolean debug=env->GetBooleanField(obj,debug_fid);
	jint read_value=0;
	jint i2c_file_descriptor=env->GetIntField(obj,i2c_file_descriptor_fid);
	if(debug) std::cout<<"[native] Setting up file descriptor for communication with device at address 0x"<<std::hex<<(int)(i2c_device_address&0xFF)<<"....";
	if (ioctl(i2c_file_descriptor,I2C_SLAVE,i2c_device_address) < 0) {
		if(debug) std::cout<<"failed to acquire bus access and/or talk to slave. ("<<strerror(errno)<<")\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	std::cout<<"success.\n[native] trying read from memory address 0x"<<std::hex<<(int)(i2c_memory_address&0xFF)<<" from device at address 0x"<<std::hex<<(int)(i2c_device_address&0xFF)<<"...";
	if(i2c_file_descriptor<0){
		std::cout<<"error (I2C file descriptor not open. Did you call init?)\n";
		env->SetIntField(obj,exit_code_fid,-1);
		std::cout.flush();
		return -1;
	}
	read_value = i2c_smbus_read_byte_data(i2c_file_descriptor,(unsigned char)(i2c_memory_address&0xFF));
	if(read_value!=-1){
		std::cout<<"success.\n";
		env->SetIntField(obj,exit_code_fid,0);
	}else{
		std::cout<<"error. ("<<strerror(errno)<<")\n";
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

