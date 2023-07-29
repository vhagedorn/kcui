package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;

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
		setCurriculum(library.getCurriculum(curriculum.getName()));
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
		if(curriculum != null)
			builder.append(curriculum.getName());
		if(group != null)
			builder.append(DELIM).append(group.getName());
		String result = builder.toString();
		return result.isEmpty() ? EMPTY : result;
	}

}
