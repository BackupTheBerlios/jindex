package org.jindex.utils;

public class JStringUtils {
	/**
	 * Encodes XML reserved characters to thier respective entities.
	 * <p>
	 * This will encode/change the following raw characters:
	 * <ul>
	 * <li>&quot; = &amp;quot;</li>
	 * <li>&gt; = &amp;gt;</li>
	 * <li>&lt; = &amp;lt;</li>
	 * <li>&amp; = &amp;amp;</li>
	 * </ul>
	 * 
	 * @param str
	 *            to be encoded.
	 * @return String with standard encoded entities.
	 */
	public static String encodeXMLEntities(String str) {

		if (str == null) {
			return "null";
		}
		int len = str.length();
		StringBuffer outBuffer = new StringBuffer(len * 2);
		for (int x = 0; x < len; x++) {
			char aChar = str.charAt(x);
			switch (aChar) {
			case '\"':
				outBuffer.append("&quot;");
				break;
			case '>':
				outBuffer.append("&gt;");
				break;
			case '<':
				outBuffer.append("&lt;");
				break;
			case '\'':
				outBuffer.append("&apos;");
				break;
			case '&':
				outBuffer.append("&amp;");
				break;
			default:
				outBuffer.append(aChar);
				break;
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Encodes method to two resultant types.
	 * <p>
	 * This method can encode a given text into HTML or text that is very
	 * similar to that encoding scheme seen in the Java Properties object using
	 * \\uxxxx for non-printable characters
	 * <p>
	 * If encoding into HTML various entities will be encoded for proper HTML
	 * formatting.
	 * 
	 * @param theString
	 *            to be encoded
	 * @param asHTML
	 *            encode using HTML logic, other while Properties type logic.
	 * @param escapeSpaces
	 *            signals to show spaces with &amp;nbsp; or '\ '
	 * @return encoded string.
	 */
	public static String encode(String theString, boolean asHTML, boolean escapeSpaces) {

		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len * 2);
		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			switch (aChar) {
			case ' ':
				if (asHTML)
					outBuffer.append("&nbsp;");
				else {
					if (escapeSpaces)
						outBuffer.append('\\');
					outBuffer.append(' ');
				}
				break;
			case '\\':
				outBuffer.append('\\');
				break;
			case '\t':
				if (asHTML)
					outBuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				else {
					outBuffer.append('\\');
					outBuffer.append('t');
				}
				break;
			case '\"':
				if (asHTML)
					outBuffer.append("&quot;");
				else
					outBuffer.append('\"');
				break;
			case '>':
				if (asHTML)
					outBuffer.append("&gt;");
				else
					outBuffer.append('>');
				break;
			case '<':
				if (asHTML)
					outBuffer.append("&lt;");
				else
					outBuffer.append('<');
				break;
			case '\n':
				if (asHTML)
					outBuffer.append("<BR>");
				else {
					outBuffer.append('\\');
					outBuffer.append('n');
				}
				break;
			case '\r':
				if (asHTML)
					outBuffer.append("<BR>");
				else {
					outBuffer.append('\\');
					outBuffer.append('r');
				}
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			default:
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					if (asHTML) {
						outBuffer.append('&');
						outBuffer.append('#');
					} else {
						outBuffer.append('\\');
						outBuffer.append('u');
					}
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Method for decoding ascii escaped character sequences for UNICODE
	 * characters.
	 * <p>
	 * This will decode text encoded in a fashion similar to the Java Properties
	 * encoding mechanisim. Such that \\uxxxx are converted to the proper
	 * characters.
	 * <p>
	 * In fact most of this code came from that private method contained there.
	 * 
	 * @see #encode(String, boolean, boolean)
	 * @param str
	 *            to decode ASCII escaped character sequences from.
	 * @return decoded string.
	 */
	public static String decode(String str) {

		char aChar;
		int len = str.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = str.charAt(x++);
			if (aChar == '\\') {
				aChar = str.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = str.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	/**
	 * Helper method to count number of given characters in a string.
	 * <p>
	 * This will return number of character instances in the given string. if
	 * the given string is null then a -1 will returned.
	 * <p>
	 * For example if the given string is "XXNNXXnxXnXN" and the char is 'n' it
	 * will return a value of two as this method is case sensitve.
	 * <p>
	 * Another example is if given the string 'X\nXxxNnN' with count escaped set
	 * to false will return 1 otherwise it will return 2.
	 * 
	 * @param str
	 *            to check characters for.
	 * @param c
	 *            character to count
	 * @return number of occurences of the c in str.
	 */
	public static int charCount(String str, char c, boolean countEscaped) {

		if (str == null)
			return -1;
		int cnt = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				if (i >= 1) {
					if (str.charAt(i - 1) == '\\') {
						if (countEscaped) {
							cnt++;
						} else {
							continue;
						}
					} else {
						cnt++;
					}
				}
			}
		}
		return cnt;
	}

	private static char toHex(int nibble) {

		return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	
	
	
	
	
	
	
	
}
