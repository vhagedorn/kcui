package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.persist.PronounciationType;

/**
 * @author vadim
 */
public class Pronounciation {

	public static final Pronounciation EMPTY = new Pronounciation(PronounciationType.UNKNOWN, null, 0);

	public final String             value;
	public final PronounciationType type;
	private final int                index;

	Pronounciation(PronounciationType type, String value, int index) {
		this.value = value;
		this.type  = type;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder copy() {
		return builder().value(value).index(index);
	}

	@Override
	public String toString() {
		return value;
	}

	public static final class Builder {
		private String             value;
		private PronounciationType type;
		private int                index;

		private Builder() {}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder type(PronounciationType type) {
			this.type = type;
			return this;
		}

		public Builder index(int index) {
			this.index = index;
			return this;
		}

		public Pronounciation build() {
			if (value == null)
				throw new NullPointerException("value");
			if (type == null)
				throw new NullPointerException("type");

			return new Pronounciation(type, value, index);
		}
	}
}
