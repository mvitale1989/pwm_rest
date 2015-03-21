JHOME = $(shell readlink -f $$(which java) | rev | cut -d/ -f-3 --complement | rev )
PROJECT_HOME=../../..
TARGET_DIR=$(PROJECT_HOME)/build/libs
ARTIFACT=$(TARGET_DIR)/libbbi2c.so

all: header library

library: $(ARTIFACT)
$(ARTIFACT): it_binarybrain_hw_i2c_I2CDriver.cpp
	mkdir -p $(TARGET_DIR)
	g++ -fPIC -shared -I$(JHOME)/include -I$(JHOME)/include/linux it_binarybrain_hw_i2c_I2CDriver.cpp -o $(ARTIFACT)

header: it_binarybrain_hw_i2c_I2CDriver.h
	javah -classpath ../../../build/classes/main -d . it.binarybrain.hw.i2c.I2CDriver


clean:
	#rm -f $(ARTIFACT)
