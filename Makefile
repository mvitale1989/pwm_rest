all: dependency_check driver tester library
test: tester
	java -classpath bin -Djava.library.path=native/lib it.binarybrain.hw.I2CTester
dependency_check:
	@scripts/check_dependencies.sh


driver: bin/it/binarybrain/hw/I2CDriver.class bin/it/binarybrain/hw/PCA9685Driver.class
tester: bin/it/binarybrain/hw/I2CTester.class

bin/it/binarybrain/hw/I2CDriver.class: src/it/binarybrain/hw/I2CDriver.java
	mkdir -p bin
	javac -classpath src -d bin src/it/binarybrain/hw/I2CDriver.java
bin/it/binarybrain/hw/PCA9685Driver.class: src/it/binarybrain/hw/PCA9685Driver.java
	mkdir -p bin
	javac -classpath src -d bin src/it/binarybrain/hw/PCA9685Driver.java
bin/it/binarybrain/hw/I2CTester.class: src/it/binarybrain/hw/I2CTester.java
	mkdir -p bin
	javac -classpath src -d bin src/it/binarybrain/hw/I2CTester.java



library:
	make library -C native

header:
	make header -C native




clean:
	rm -rf bin
	make clean -C native
