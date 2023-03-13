package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class Group extends IdAdapter {

	public final String     name;
	public final Curriculum curriculum;

	Group(String name, Curriculum curriculum) {
		this.name       = name;
		this.curriculum = curriculum;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder copy() {
		return builder().name(name).curriculum(curriculum);
	}

	public void ensureInCurriculum() {
		curriculum.groups.add(this);
	}

	@Override
	public String toString() {
		return name;
	}

	public static final class Builder implements Identifiable {
		public String     name;
		public Curriculum curriculum;
		public  long       c_id = -1;
		public long       id   = -1;

		private Builder() {}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder curriculum(Curriculum curriculum) {
			this.curriculum = curriculum;
			return this;
		}

		public Builder curriculum(long c_id) {
			this.c_id = c_id;
			return this;
		}

		public Builder id(long id) {
			this.id = id;
			return this;
		}

		@Override
		public long id() {
			return this.id;
		}

		public Group build() {
			if (name == null)
				throw new NullPointerException("name");
			if (curriculum == null && c_id == -1)
				throw new NullPointerException("curriculum");

			Group group = new Group(name, curriculum);

			if (id != -1)
				group.setId(id);

			return group;
		}
	}
}
