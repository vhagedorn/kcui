package me.vadim.ja.kc.util;

import java.util.regex.Pattern;

/**
 * @author vadim
 */
public final class Util {

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void fuckUncheckedExceptions(Throwable throwable) throws T {
		throw (T) throwable;
	}

	public static void sneaky(Throwable throwable) {
		Util.<RuntimeException>fuckUncheckedExceptions(throwable);
	}

	/**
	 * @link <a href="https://stackoverflow.com/a/4237934/12344841">Source</a>
	 */
	public static final Pattern xml11pattern = Pattern.compile("[^"
			+ "\u0001-\uD7FF"
			+ "\uE000-\uFFFD"
			+ "\ud800\udc00-\udbff\udfff"
			+ "]+");

	public static String sanitizeXML(String dirty) {
		return dirty == null ? null : xml11pattern.matcher(dirty).replaceAll("");
	}

}
