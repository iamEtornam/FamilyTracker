package co.etornam.familytracker.util;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomStringGenerator {

	public static String getAlphaNumbericString(int size) {
		byte[] array = new byte[256];
		new Random().nextBytes(array);
		String randomString = new String(array, Charset.forName("UTF-8"));
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < randomString.length(); i++) {
			char c = randomString.charAt(i);
			if (((c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9'))
					&& (size > 0)) {
				buffer.append(c);
				size--;
			}
		}
		return buffer.toString();
	}
}
