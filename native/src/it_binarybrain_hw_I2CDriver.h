/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class it_binarybrain_hw_I2CDriver */

#ifndef _Included_it_binarybrain_hw_I2CDriver
#define _Included_it_binarybrain_hw_I2CDriver
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     it_binarybrain_hw_I2CDriver
 * Method:    nativeOpenDeviceFile
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_it_binarybrain_hw_I2CDriver_nativeOpenDeviceFile
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     it_binarybrain_hw_I2CDriver
 * Method:    nativeCloseDeviceFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_nativeCloseDeviceFile
  (JNIEnv *, jobject);

/*
 * Class:     it_binarybrain_hw_I2CDriver
 * Method:    nativeWriteByte
 * Signature: (BB)V
 */
JNIEXPORT void JNICALL Java_it_binarybrain_hw_I2CDriver_nativeWriteByte
  (JNIEnv *, jobject, jbyte, jbyte);

/*
 * Class:     it_binarybrain_hw_I2CDriver
 * Method:    nativeReadByte
 * Signature: (B)B
 */
JNIEXPORT jbyte JNICALL Java_it_binarybrain_hw_I2CDriver_nativeReadByte
  (JNIEnv *, jobject, jbyte);

#ifdef __cplusplus
}
#endif
#endif
