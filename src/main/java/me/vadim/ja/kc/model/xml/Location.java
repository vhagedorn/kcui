package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;

import java.util.Objects;

/**
 * @author vadim
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

	public static final String DELIM = "#";
	public static final String EMPTY = "<empty>";

	public static final Location NONE = new Location(null, null);

	private ImplCurriculum curriculum;
	private ImplGroup group;

	public Location(Curriculum curriculum, Group group) {
		this.curriculum = (ImplCurriculum) curriculum;
		this.group      = (ImplGroup) group;
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
		this.curriculum = (ImplCurriculum) curriculum;
	}

	public void setGroup(Group group) {
		this.group = (ImplGroup) group;
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
	public int hashCode() {
		int result = 1;

		result = 92821 * result + ((curriculum == null) ? 0 : curriculum.hashCode());
		result = 92821 * result + ((group == null) ? 0 : group.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location)) return false;
		Location location = (Location) obj;
		return Objects.equals(curriculum, location.curriculum) && Objects.equals(group, location.group);
	}

}
