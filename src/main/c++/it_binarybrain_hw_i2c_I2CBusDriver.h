/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class it_binarybrain_hw_i2c_I2CBusDriver */

#ifndef _Included_it_binarybrain_hw_i2c_I2CBusDriver
#define _Included_it_binarybrain_hw_i2c_I2CBusDriver
#ifdef __cplusplus
extern "C" {
#endif
#undef it_binarybrain_hw_i2c_I2CBusDriver_MIN_PRIORITY
#define it_binarybrain_hw_i2c_I2CBusDriver_MIN_PRIORITY 1L
#undef it_binarybrain_hw_i2c_I2CBusDriver_NORM_PRIORITY
#define it_binarybrain_hw_i2c_I2CBusDriver_NORM_PRIORITY 5L
#undef it_binarybrain_hw_i2c_I2CBusDriver_MAX_PRIORITY
#define it_binarybrain_hw_i2c_I2CBusDriver_MAX_PRIORITY 10L
/*
 * Class:     it_binarybrain_hw_i2c_I2CBusDriver
 * Method:    nativeOpenDeviceFile
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_it_binarybrain_hw_i2c_I2CBusDriver_nativeOpenDeviceFile
  (JNIEnv *, jobject, jstring);

/*
 * Class:     it_binarybrain_hw_i2c_I2CBusDriver
 * Method:    nativeCloseDeviceFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_it_binarybrain_hw_i2c_I2CBusDriver_nativeCloseDeviceFile
  (JNIEnv *, jobject);

/*
 * Class:     it_binarybrain_hw_i2c_I2CBusDriver
 * Method:    nativeWriteByte
 * Signature: (BBB)V
 */
JNIEXPORT void JNICALL Java_it_binarybrain_hw_i2c_I2CBusDriver_nativeWriteByte
  (JNIEnv *, jobject, jbyte, jbyte, jbyte);

/*
 * Class:     it_binarybrain_hw_i2c_I2CBusDriver
 * Method:    nativeReadByte
 * Signature: (BB)B
 */
JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_i2c_I2CBusDriver_nativeReadByte
  (JNIEnv *, jobject, jbyte, jbyte);

#ifdef __cplusplus
}
#endif
#endif