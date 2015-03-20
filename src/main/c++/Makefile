JHOME = $(shell readlink -f $$(which java) | rev | cut -d/ -f-3 --complement | rev )
PROJECT_HOME=../../..
TARGET_DIR=$(PROJECT_HOME)/build/libs
ARTIFACT=$(TARGET_DIR)/libbbi2c.so

all: library

library: $(ARTIFACT)
$(ARTIFACT): it_binarybrain_hw_I2CDriver.h it_binarybrain_hw_I2CDriver.cpp
	mkdir -p $(TARGET_DIR)
	g++ -fPIC -shared -I$(JHOME)/include -I$(JHOME)/include/linux it_binarybrain_hw_I2CDriver.cpp -o $(ARTIFACT)

clean:
	#rm -f $(ARTIFACT)
