package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class Group extends IdAdapter {

	public final String name;

	public Group(long id, String name) {
		super(id);
		this.name = name;
	}

}
