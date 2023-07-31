package me.vadim.ja.kc.util;

/**
 * Thrown to indicate that a packaged resource is missing.
 * @author vadim
 */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String resource) {
		super("Unable to load packaged JAR resource '"+resource+"'");
	}

	public ResourceNotFoundException(String resource, Throwable cause) {
		super("Unable to load packaged JAR resource '"+resource+"' due to exception.", cause);
	}

}
