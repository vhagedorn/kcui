package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class PartOfSpeech {

	public final String name;
	public final Info   info;
	public final int priority;

	private PartOfSpeech(String name, Info info, int priority) {
		this.name = name;
		this.info = info;
		this.priority = priority;
	}

	public boolean hasInfo() {
		return info != null;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Builder builder() { return new Builder(); }

	public static final class Builder {
		private String name;
		private Info   info;
		private int priority;

		private Builder() {}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder info(String info) {
			if(info != null)
				this.info = new Info(info);
			return this;
		}

		public Builder priority(int priority){
			this.priority = priority;
			return this;
		}

		public PartOfSpeech build() {
			PartOfSpeech result = new PartOfSpeech(name, info, priority);
			if(info != null)
				info.parent = result;
			return result;
		}
	}


	/**
	 * Extra info about the part of speech, such as "irregular" for verbs.
	 */
	public static final class Info {

		PartOfSpeech parent;
		public final String value;

		private Info(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public PartOfSpeech getPartOfSpeech() {
			return parent;
		}

	}

}
