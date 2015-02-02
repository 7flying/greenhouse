package com.sevenflying.utils;

import org.apache.commons.codec.binary.Base64;

/** Off-topic things */
public class Utils {

	public static String encode64(String toEncode) {
		byte[] encodedBytes = Base64.encodeBase64(toEncode.getBytes());
		return new String(encodedBytes);
	}
	
	public static String encode64(char toEncode) {
		return encode64(Character.toString(toEncode));
	}
	
	public static String encode64(long toEncode) {
		return encode64(Long.toString(toEncode));
	}
	
	public static String encode64(double toEncode) {
		return encode64(Double.toString(toEncode));
	}
	
}
