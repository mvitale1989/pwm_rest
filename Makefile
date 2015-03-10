all: release

release:
	@echo "RELEASE"

debug:
	@echo "DEBUG"


library: header
	make all -C native

header:
	javah -classpath src -d native/src it.binarybrain.hw.I2CDriver

clean:
	make clean -C native
