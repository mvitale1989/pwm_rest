JHOME = $(shell readlink -f $$(which java) | rev | cut -d/ -f-3 --complement | rev )

all: header library


library: lib/libbbi2c.so
lib/libbbi2c.so: src/it_binarybrain_hw_I2CDriver.h src/it_binarybrain_hw_I2CDriver.cpp
	mkdir -p lib
	g++ -fPIC -shared -I$(JHOME)/include -I$(JHOME)/include/linux src/it_binarybrain_hw_I2CDriver.cpp -o lib/libbbi2c.so

header: src/it_binarybrain_hw_I2CDriver.h
src/it_binarybrain_hw_I2CDriver.h: ../src/it/binarybrain/hw/I2CDriver.java
	javah -classpath ../src -d src it.binarybrain.hw.I2CDriver

clean:
	rm -rf lib
