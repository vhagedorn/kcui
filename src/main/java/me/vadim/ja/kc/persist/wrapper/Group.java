package me.vadim.ja.kc.persist.wrapper;

import me.vadim.ja.kc.persist.impl.Location;

/**
 * @author vadim
 */
public interface Group {

	String getName();

	void setName(String name);

	Curriculum getCurriculum();

	Location toLocation();

}
