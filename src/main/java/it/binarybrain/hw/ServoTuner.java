package it.binarybrain.hw;

import it.binarybrain.hw.i2c.I2CDriver;
import it.binarybrain.hw.i2c.PCA9685;

import java.io.IOException;
import java.util.Scanner;

public class ServoTuner {
	public static void main(String[] args){
		String i2cBus = "/dev/i2c-1";
		int deviceAddress = 0x40;
		int servoChannel=0;
		System.out.println("Assuming bus: /dev/i2c-1, device address 0x40, with servo connected to channel 0...\n");
		try {
			PCA9685 pca9685 = new PCA9685(i2cBus,deviceAddress);
			Servo servo = new Servo(0,180,0,1);
			pca9685.addPWMControllable(servo,servoChannel);
			pca9685.init();
			System.out.println("Welcome to servo tester!");
			System.out.println("This program helps you tune the servo parameters for later use: set an arbitrary duty-cycle value to "+
					"find out the min and max duty cycle supported by your motor, write them down and then use them in your next configuration.");
			dutyCycleTester(servo);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{}
		I2CDriver.getInstance().signalExitToAll();
	}
	
	public static void dutyCycleTester(PWMControllable pwmControllable){
		Scanner s = new Scanner(System.in);
		String input=null;
		float dutyCycle=0.075F;
		try{
			System.out.println("Insert a duty cycle value (starting d-c: "+Float.toString(dutyCycle)+"; q to exit)");
			pwmControllable.setDutyCycle(dutyCycle);
			do{
				System.out.println("> ");
				input = s.nextLine();
				try{
					dutyCycle=Float.parseFloat(input);
					System.out.print("Setting duty cycle to "+Float.toString(dutyCycle)+"...");
					pwmControllable.setDutyCycle(dutyCycle);
					System.out.println("done.");
				}catch(NumberFormatException e){}
			}while(input==null || !input.equals("q"));
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			s.close();
		}
	}
}
