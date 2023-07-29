package me.vadim.ja.kc.util;

/**
 * @author vadim
 */
public final class ReflectUtil {

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void fuckUncheckedExceptions(Throwable throwable) throws T {
		throw (T) throwable;
	}

	public static void sneaky(Throwable throwable) {
		ReflectUtil.<RuntimeException>fuckUncheckedExceptions(throwable);
	}

}
