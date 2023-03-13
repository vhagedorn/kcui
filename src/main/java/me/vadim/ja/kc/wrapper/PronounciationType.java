package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public enum PronounciationType {
	/**
	 * Unspecified.
	 */
	UNKNOWN(-1),
	/**
	 * Borrowed Chinese pronounciation.
	 */
	ON_YOMI(0),
	/**
	 * Japanese native pronounciation.
	 */
	KUN_YOMI(1),
	/**
	 * Used in names (rare).
	 */
	NANORI(3);

	public final int id;

	PronounciationType(int id) {
		this.id = id;
	}

	public static PronounciationType fromID(int id){
		for (PronounciationType val : values())
			if(val.id == id)
				return val;
		return null;
	}

	@Override
	public String toString() {
		return Character.toUpperCase(name().charAt(0)) + name().substring(1).toLowerCase().replace('_', '\'');
	}
}
