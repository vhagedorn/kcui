package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author vadim
 */
public class Curr implements Curriculum {

	private String name;

	Curr() {}

	Curr(String name) {
		setName(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("name");
		this.name = name.replace(Location.DELIM, "");
	}

	private final Set<Group> groups = new HashSet<>();

	@Override
	public Set<Group> getGroups() {
		return Collections.unmodifiableSet(groups);
	}

	@Override
	public Group addGroup(String name) {
		Group group = new Grp(name);
		groups.add(group);
		return group;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Curriculum)) return false;
		Curriculum curriculum = (Curriculum) obj;
		return Objects.equals(name, curriculum.getName());
	}

}
