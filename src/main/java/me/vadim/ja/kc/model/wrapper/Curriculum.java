package me.vadim.ja.kc.model.wrapper;

import java.util.Set;

/**
 * @author vadim
 */
public interface Curriculum {

	String getName();

	void setName(String name);

	Group addGroup(String name);

	void unlinkGroup(Group group);

	Set<Group> getGroups();

	// fix groups
	void flatten();

	int getDefaultRenderOpts();

	void setDefaultRenderOpts(int mask);

}
