package pl.codebrewery.noekeon;

import static pl.codebrewery.noekeon.ArrayOut.outBytes;

import java.util.Random;

import pl.codebrewery.noekeon.Noekeon.Mode;

public class NoeTest {
	static byte[] input = "jestem odpadem atomowym i jest mi z tym dobrze całkiem...".getBytes();
//	static byte[] key = Arrays.copyOf("klucz do szafy numer 1".getBytes(), 16);
	static byte[] key = new byte[16];
	
	public static void main(String[] args) {
		new Random().nextBytes(key);
		
		System.out.println("The input is: " + outBytes(input));
		System.out.println("The key is: " + outBytes(key));
		Noekeon enc = new Noekeon(input, key, Mode.ENCRYPTION);
		byte[] out = enc.process();
		System.out.println("The ENCRYPTED output is: " + outBytes(out));
		
		Noekeon dec = new Noekeon(out, key, Mode.DECRYPTION);
		byte[] out2 = dec.process();
		System.out.println("The DECRYPTED output is: " + outBytes(out2));
		
		System.out.println("After conversion to String: " + new String(out2));
	}
}
