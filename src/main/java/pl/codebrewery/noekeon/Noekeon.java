package pl.codebrewery.noekeon;

import static java.lang.Integer.*;
import static java.lang.System.arraycopy;

import java.util.Arrays;

public class Noekeon {
	
	private final static int[] NULL_KEY = new int[] { 0, 0, 0, 0 };
	private final static int ROUND_NUM = 16;
	
	private final int[] key;
	private final int[] state = new int[4];
	
	private final int[] input, output;
	private final Mode mode;
	
	private final int[] rct = new int[ROUND_NUM + 1];
	
	public enum Mode { ENCRYPTION, DECRYPTION }

	public Noekeon(byte[] input, byte[] key, Mode mode) {
		if (key.length != 16) throw new IllegalArgumentException("The key MUST be 16 bytes long!");
		
		final int byteLength = ((input.length + 15) / 16) * 16; //rounded up to nearest 16 multiply;
		final byte[] inputPadded = Arrays.copyOf(input, byteLength);
		this.input = toWords(inputPadded);
		this.output = new int[this.input.length];
		this.mode = mode;
		this.key = toWords(key);
		if (mode == Mode.DECRYPTION) { //decryption key is transformed
			theta(NULL_KEY, this.key);
		}
		
		rct[0] = 0x80;
		for (int i = 1; i <= ROUND_NUM; i++) {
			rct[i] = rctNext(rct[i - 1]);
		}
	}
	
	public byte[] process() {

		for (int i = 0; i < input.length; i += 4) { //4-word blocks
			arraycopy(input, i, state, 0, 4); //copy an input fragment to the buffer
			processBlock(key, state, mode);
			arraycopy(state, 0, output, i, 4); //copy the buffer to the output
		}
		return toBytes(output);
	}
	
	private int rctNext(int prev) {
		return 0xff & (((prev & 0x80) > 0) ? (prev << 1) ^ 0x1b : (prev << 1));
	}
	
	private void processBlock(int[] workingKey, int[] state, Mode mode) {
		int r;
		for (r = 0; r < ROUND_NUM; r++) {
			roundBegin(workingKey, state, r, mode);
			roundFinish(workingKey, state);
		}
		roundBegin(workingKey, state, r, mode);
	}
	
	private void roundBegin(int[] key, int[] state, int r, Mode mode) {
		if (mode == Mode.ENCRYPTION) state[0] ^= rct[r];
		theta(key, state);
		if (mode == Mode.DECRYPTION) state[0] ^= rct[ROUND_NUM - r];
	}
	
	private void roundFinish(int[] key, int[] state) {
		pi1(state);
		gamma(state);
		pi2(state);
	}

	private void theta(int[] k, int[] a) {
		int temp;
		temp = a[0] ^ a[2];
		temp ^= rotateRight(temp, 8) ^ rotateLeft(temp, 8);
		a[1] ^= temp;
		a[3] ^= temp;
		
		a[0] ^= k[0]; a[1] ^= k[1]; a[2] ^= k[2]; a[3] ^= k[3];
		
		temp = a[1] ^ a[3];
		temp ^= rotateRight(temp, 8) ^ rotateLeft(temp, 8);
		a[0] ^= temp;
		a[2] ^= temp;
	}
	
	private void gamma(int[] a) {
		a[1] ^= ~a[3] & ~a[2];
		a[0] ^= a[2] & a[1];
		int tmp = a[3]; a[3] = a[0]; a[0] = tmp;
		a[2] ^= a[0] ^ a[1] ^ a[3];
		a[1] ^= ~a[3] & ~a[2];
		a[0] ^= a[2] & a[1];
	}
	
	private void pi1(int[] a) {
		a[1] = rotateLeft(a[1], 1);
		a[2] = rotateLeft(a[2], 5);
		a[3] = rotateLeft(a[3], 2);
	}
	
	private void pi2(int[] a) {
		a[1] = rotateRight(a[1], 1);
		a[2] = rotateRight(a[2], 5);
		a[3] = rotateRight(a[3], 2);
	}
	
//	private int[] toWords(byte[] in) {
//		IntBuffer buf = IntBuffer.allocate(in.length / 4);
//		buf.put(ByteBuffer.wrap(in).asIntBuffer());
//		return buf.array();
//	}
	
	private int[] toWords(byte[] in) {
		final int[] out = new int[in.length / 4];
		for (int i = 0; i < out.length; i++ ) {
			out[i] =
				((0xff & in[i * 4 + 0]) << 24) |
				((0xff & in[i * 4 + 1]) << 16) |
				((0xff & in[i * 4 + 2]) << 8) |
				((0xff & in[i * 4 + 3]) << 0);
		}
		return out;
	}
	
//	private byte[] toBytes(int[] in) {
//		final ByteBuffer buf = ByteBuffer.allocate(in.length * 4);
//		buf.asIntBuffer().put(in);
//		return buf.array();
//	}
	
	private byte[] toBytes(int[] in) {
		final byte[] out = new byte[in.length * 4];
		for (int i = 0; i < in.length; i++) {
			out[i * 4 + 0] = (byte) ((in[i] >>> 24) & 0xff);
			out[i * 4 + 1] = (byte) ((in[i] >>> 16) & 0xff);
			out[i * 4 + 2] = (byte) ((in[i] >>> 8) & 0xff);
			out[i * 4 + 3] = (byte) ((in[i] >>> 0) & 0xff);
		}
		return out;
	}
}
