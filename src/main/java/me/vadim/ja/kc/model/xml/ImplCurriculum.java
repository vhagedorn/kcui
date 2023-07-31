package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import me.vadim.ja.kc.util.Util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author vadim
 */
@XmlRootElement(name = "curriculum")
class ImplCurriculum implements Curriculum {

	private String name;
	@XmlAttribute
	private int renderOpts = DiagramCreator.createBitmask(200, true, 5, DiagramCreator.DOWN);

	ImplCurriculum() { }

	ImplCurriculum(String name) {
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

	@XmlElement
	private final Set<ImplGroup> groups = new HashSet<>();

	@Override
	public Set<Group> getGroups() {
		flatten();
		return Collections.unmodifiableSet(groups);
	}

	@Override
	public Group addGroup(String name) {
		ImplGroup group = new ImplGroup(this, name);
		groups.add(group);
		return group;
	}

	@Override
	public void unlinkGroup(Group group) {
		if (!(group instanceof ImplGroup)) return;
		ImplGroup groupImpl = (ImplGroup) group;
		groupImpl.curriculumImpl = null;
		groups.remove(groupImpl);
	}

	@Override
	public void flatten() {
		for (ImplGroup group : groups)
			group.curriculumImpl = this;
	}

	@Override
	public int getDefaultRenderOpts() {
		return renderOpts;
	}

	@Override
	public void setDefaultRenderOpts(int mask) {
		this.renderOpts = mask;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Curriculum)) return false;
		Curriculum curriculum = (Curriculum) obj;
		return Objects.equals(name, curriculum.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
