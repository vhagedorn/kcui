package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class Group extends IdAdapter {

	public final String name;
	public final Curriculum curriculum;

	public Group(long id, String name, Curriculum curriculum) {
		super(id);
		this.name       = name;
		this.curriculum = curriculum;
	}

	@Override
	public String toString() {
		return name;
	}

}
