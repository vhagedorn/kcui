package me.vadim.ja.kc.model.wrapper;

import me.vadim.ja.kc.model.xml.Location;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author vadim
 */
public interface Group {

	String getName();

	void setName(String name);

	Curriculum getCurriculum();

	Location toLocation();

	/**
	 * Creates a new group with the same name in {@code curriculum}.
	 */
	@ApiStatus.Internal
	void createInParent(Curriculum curriculum);

}
