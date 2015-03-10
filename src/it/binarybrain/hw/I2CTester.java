package it.binarybrain.hw;
import it.binarybrain.hw.I2CDriver;

public class I2CTester {

	public static void main(String[] args) {
		I2CDriver i2c = new I2CDriver();
		i2c.init("ciao",(byte)16);
		i2c.readByte((byte)0x15);
		i2c.writeByte((byte)0x15,(byte)0x88);
		i2c.test();
	}

}
