package me.vadim.ja.kc.persist.wrapper;

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

	void flatten();

}
