package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public enum PronounciationType {
	/**
	 * Unspecified.
	 */
	UNKNOWN,
	/**
	 * Borrowed Chinese pronounciation.
	 */
	ON_YOMI,
	/**
	 * Japanese native pronounciation.
	 */
	KUN_YOMI,
	/**
	 * Used in names (rare).
	 */
	NANORI;


	@Override
	public String toString() {
		return Character.toUpperCase(name().charAt(0)) + name().substring(1).toLowerCase().replace('_', '\'');
	}
}
