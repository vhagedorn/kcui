package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.util.Util;

import java.util.Objects;

/**
 * @author vadim
 */
@XmlRootElement(name = "group")
class ImplGroup implements Group {

	private String name;

	ImplGroup() { }

	@XmlTransient
	ImplCurriculum curriculumImpl;

	ImplGroup(ImplCurriculum curriculumImpl, String name) {
		this.curriculumImpl = curriculumImpl;
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
		return curriculumImpl;
	}

	@Override
	public Location toLocation() {
		return new Location(curriculumImpl, this);
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
