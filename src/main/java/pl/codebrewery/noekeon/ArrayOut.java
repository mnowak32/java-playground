package pl.codebrewery.noekeon;

public class ArrayOut {
	public static String outBytes(byte[] in) {
		StringBuilder sb = new StringBuilder("(")
			.append(in.length)
			.append(")[");
		for (int i = 0; i < in.length; i++) {
			if (i > 0) sb.append(", ");
			sb.append(Integer.toHexString(0xff & in[i]));
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String outInts(int[] in) {
		StringBuilder sb = new StringBuilder("(")
			.append(in.length)
			.append(")[");
		for (int i = 0; i < in.length; i++) {
			if (i > 0) sb.append(", ");
			sb.append(Integer.toHexString(in[i]));
		}
		sb.append("]");
		return sb.toString();
	}
}
