package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.util.Util;

import java.util.Objects;

/**
 * @author vadim
 */
@XmlRootElement(name = "group")
public class Grp implements Group {

	private String name;

	Grp() { }

	@XmlTransient
	Curr curr;

	Grp(Curr curr, String name) {
		this.curr = curr;
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

	@Override
	public Curriculum getCurriculum() {
		return curr;
	}

	@Override
	public Location toLocation() {
		return new Location(curr, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Group)) return false;
		Group group = (Group) obj;
		return Objects.equals(name, group.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void createInParent(Curriculum curriculum) {
		curriculum.addGroup(name);
	}

}
