all: dependency_check driver tester library
test: tester
	java -classpath bin -Djava.library.path=native/lib it.binarybrain.hw.I2CTester
dependency_check:
	@scripts/check_dependencies.sh


driver: bin/it/binarybrain/hw/I2CDriver.class
tester: bin/it/binarybrain/hw/I2CTester.class

bin/it/binarybrain/hw/I2CDriver.class: src/it/binarybrain/hw/I2CDriver.java
	mkdir -p bin
	javac -classpath src -d bin src/it/binarybrain/hw/I2CDriver.java
bin/it/binarybrain/hw/I2CTester.class: src/it/binarybrain/hw/I2CTester.java
	mkdir -p bin
	javac -classpath src -d bin src/it/binarybrain/hw/I2CTester.java



library: header
	make all -C native

header:
	javah -classpath src -d native/src it.binarybrain.hw.I2CDriver




clean:
	rm -rf bin
	make clean -C native
