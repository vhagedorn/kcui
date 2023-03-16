package me.vadim.ja.kc.wrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vadim
 */
public class Curriculum extends IdAdapter {

	public final String name;

	public final Set<Group> groups = new HashSet<>();

	public Curriculum(String name) {
		this.name = name;
	}

	public Group createGroup(String name){
		Group group = new Group(name, this);
		this.groups.add(group);
		return group;
	}

	public List<Group> getGroups() {
		return new ArrayList<>(groups);
	}

	@Override
	public String toString() {
		return name;
	}

}