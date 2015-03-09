all: library run_tests


library: ./source/PCA9685driver.cpp ./source/PCA9685driver.h
	mkdir -p ./lib
	g++ -Wall -std=c++0x -fPIC -shared ./source/PCA9685driver.cpp -o ./lib/libpwm_rest.so


run_tests: ./source/run_tests.cpp
	mkdir -p ./bin
	g++ -std=c++0x -o ./bin/run_tests -Wl,-rpath=../lib:./lib:./native./lib ./source/run_tests.cpp -L./lib -lpwm_rest


clean:
	for f in $$(ls ./bin); do rm ./bin/$$f; done
	for f in $$(ls ./lib); do rm ./lib/$$f; done

