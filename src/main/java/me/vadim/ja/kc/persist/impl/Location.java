package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;

import java.util.Objects;

/**
 * @author vadim
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

	public static final String DELIM = "#";
	public static final String EMPTY = "<empty>";

	public static final Location NONE = new Location(null, null);

	private Curr curriculum;
	private Grp group;

	public Location(Curriculum curriculum, Group group) {
		this.curriculum = (Curr) curriculum;
		this.group      = (Grp) group;
	}

	public Curriculum getCurriculum() {
		return curriculum;
	}

	public Group getGroup() {
		return group;
	}

	void flatten(Library library) {
		if (curriculum != null) {
			setCurriculum(library.getCurriculum(curriculum.getName()));
			curriculum.flatten();
		}
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = (Curr) curriculum;
	}

	public void setGroup(Group group) {
		this.group = (Grp) group;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (curriculum != null)
			builder.append(curriculum.getName());
		if (group != null)
			builder.append(DELIM).append(group.getName());
		String result = builder.toString();
		return result.isEmpty() ? EMPTY : result;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Location)) return false;
		Location location = (Location) obj;
		return Objects.equals(curriculum, location.curriculum) && Objects.equals(group, location.group);
	}

}
