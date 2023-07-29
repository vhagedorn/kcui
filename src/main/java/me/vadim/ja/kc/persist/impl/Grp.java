package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.wrapper.Group;

import java.util.Objects;

/**
 * @author vadim
 */
public class Grp implements Group {

	private String name;

	Grp() {}

	Grp(String name) {
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

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group)) return false;
		Group group = (Group) obj;
		return Objects.equals(name, group.getName());
	}

}
