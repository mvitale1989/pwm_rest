JHOME = $(shell readlink -f $$(which java) | rev | cut -d/ -f-3 --complement | rev )
PROJECT_HOME=../../..
TARGET_DIR=$(PROJECT_HOME)/build/libs
ARTIFACT=$(TARGET_DIR)/libbbi2c.so

all: header library

library: $(ARTIFACT)
$(ARTIFACT): it_binarybrain_hw_i2c_I2CBusDriver.cpp
	mkdir -p $(TARGET_DIR)
	g++ -fPIC -shared -I$(JHOME)/include -I$(JHOME)/include/linux it_binarybrain_hw_i2c_I2CBusDriver.cpp -o $(ARTIFACT)

header: it_binarybrain_hw_i2c_I2CBusDriver.h
it_binarybrain_hw_i2c_I2CBusDriver.h: ../../../build/classes/main/it/binarybrain/hw/i2c/I2CBusDriver.class
	javah -classpath ../../../build/classes/main -d . it.binarybrain.hw.i2c.I2CBusDriver


clean:
	#rm -f $(ARTIFACT)
