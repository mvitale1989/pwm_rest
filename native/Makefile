all: run_tests library


library: ./source/PCA9685driver.cpp ./source/PCA9685driver.h
	g++ -Wall -std=c++0x -fPIC -c ./source/PCA9685driver.cpp -o ./buildfiles/pwm_rest.o
	g++ -std=c++0x -shared -Wl,-soname,libpwm_rest.so.1 -o ./lib/libpwm_rest.so.1.0 ./buildfiles/pwm_rest.o


run_tests: ./source/run_tests.cpp ./buildfiles/PCA9685driver.o
	g++ -std=c++0x -c ./source/run_tests.cpp -o buildfiles/run_tests.o
	g++ -std=c++0x buildfiles/run_tests.o buildfiles/PCA9685driver.o -o ./bin/run_tests

./buildfiles/PCA9685driver.o: ./source/PCA9685driver.cpp ./source/PCA9685driver.h
	g++ -std=c++0x -c ./source/PCA9685driver.cpp -o ./buildfiles/PCA9685driver.o

clean:
	rm -f ./buildfiles/*
	rm -f ./bin/*
	rm -f ./lib/*

