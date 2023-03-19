package me.vadim.ja.kc.wrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vadim
 */
public class Curriculum extends IdAdapter implements IdCloneable<Curriculum> {

	// all my other types are immutable; however, it
	// probably would have made sense to make them mutable,
	// but with this one it was just less troublesome to
	// do this instead of trying to preserve immutability
	private String name;

	public final Set<Group> groups = new HashSet<>();

	public Curriculum(String name) {
		this.name = name;
	}

	public void rename(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Curriculum withId(long id){
		Curriculum curriculum = new Curriculum(name);
		curriculum.groups.addAll(this.groups);
		curriculum.setId(id);
		return curriculum;
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