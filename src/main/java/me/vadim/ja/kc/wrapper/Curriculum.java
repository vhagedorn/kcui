package me.vadim.ja.kc.wrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author vadim
 */
public class Curriculum extends IdAdapter {

	public final String name;

	public final Set<Group> groups = new HashSet<>();

	public Curriculum(long id, String name) {
		super(id);
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}