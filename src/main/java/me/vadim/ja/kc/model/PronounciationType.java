package me.vadim.ja.kc.model;

import me.vadim.ja.kc.model.xml.KCFactory;

/**
 * @author vadim
 */
public enum PronounciationType {

	/**
	 * Unspecified or not applicable.
	 */
	UNKNOWN("n/a"),

	/**
	 * Borrowed Chinese pronounciation.
	 */
	ON_YOMI("On'yomi"),

	/**
	 * Native Japanese pronounciation.
	 */
	KUN_YOMI("Kun'yomi"),

	/**
	 * Used in names (rare).
	 */
	NANORI("Nanori");

	private final String name;

	PronounciationType(String name) {
		this.name = name;
	}

	public SpokenElement toSpoken(String info) {
		return KCFactory.ofSpoken(info, this);
	}

	@Override
	public String toString() {
		return name;
	}
}
