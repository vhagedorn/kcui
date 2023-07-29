package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.util.Util;

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
		this.name = Util.sanitizeXML(name.replace(Location.DELIM, ""));
	}

	private final Set<Grp> groups = new HashSet<>();

	@Override
	public Set<Group> getGroups() {
		return Collections.unmodifiableSet(groups);
	}

	@Override
	public Group addGroup(String name) {
		Grp group = new Grp(this, name);
		groups.add(group);
		return group;
	}

	@Override
	public void unlinkGroup(Group group) {
		if(!(group instanceof Grp)) return;
		Grp grp = (Grp) group;
		grp.curr = null;
		groups.remove(grp);
	}

	@Override
	public void flatten() {
		for (Grp group : groups)
			group.curr = this;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Curriculum)) return false;
		Curriculum curriculum = (Curriculum) obj;
		return Objects.equals(name, curriculum.getName());
	}

	@Override
	public String toString() {
		return name;
	}

}
