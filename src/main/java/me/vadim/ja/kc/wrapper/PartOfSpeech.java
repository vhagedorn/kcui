package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class PartOfSpeech extends IdAdapter implements IdCloneable<PartOfSpeech> {

	public final String name;
	public final Info   info;
	private final int priority;

	PartOfSpeech(String name, Info info, int priority) {
		this.name = name;
		this.info = info;
		this.priority = priority;
	}

	public int getPriority(){
		return priority;
	}

	public boolean hasInfo() {
		return info != null;
	}

	@Override
	public String toString() {
		return name;
	}

	public String toInfoString() {
		return (hasInfo() ? info.value + " " : "") + name;
	}

	public final Builder copy(){
		return builder().name(name).info(info).priority(priority);
	}

	@Override
	public PartOfSpeech withId(long id) {
		return copy().id(id).build();
	}

	public static Builder builder() { return new Builder(); }

	public static final class Builder {
		private String name;
		private String info;
		private int priority;
		private long id = -1;

		private Builder() {}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder info(String info) {
			this.info = info;
			return this;
		}

		private Builder info(Info info){
			if(info != null)
				this.info = info.value;
			return this;
		}

		public Builder priority(int priority){
			this.priority = priority;
			return this;
		}

		public Builder id(long id){
			this.id = id;
			return this;
		}

		public PartOfSpeech build() {
			if(name == null)
				throw new NullPointerException("name cannot be null");

			Info info = null;
			if(this.info != null)
				info = new Info(this.info);

			PartOfSpeech result = new PartOfSpeech(name, info, priority);

			if(info != null)
				info.parent = result;

			if(id != -1)
				result.setId(id);

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
