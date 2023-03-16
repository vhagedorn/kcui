package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class Definition {

	public final String value;
	private final int    index;

	Definition(String value, int index) {
		this.value = value;
		this.index = index;
	}

	public static Builder builder(){
		return new Builder();
	}

	public Builder copy(){
		return builder().value(value).index(index);
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return value;
	}

	public static final class Builder {
		private String value;
		private int    index;

		private Builder() {}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder index(int priority) {
			this.index = priority;
			return this;
		}

		public Definition build() {
			if (value == null)
				throw new NullPointerException("value");

			return new Definition(value, index);
		}
	}
}
