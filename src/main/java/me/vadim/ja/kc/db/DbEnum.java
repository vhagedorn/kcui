package me.vadim.ja.kc.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface to manage {@link Identifiable} objects stored in an SQL database.
 * @param <I> the objects stored in this enumeration; will be identified by {@link I#id() id}
 * @author vadim
 */
public interface DbEnum<I extends Identifiable> extends DbAddon {

	/**
	 * Delete an {@link I object} with {@code id}.
	 * @param id the {@link I#id() id} to search for
	 */
	void delete(long id);

	/**
	 * Insert a new {@link I object}.
	 * @param obj the {@link I object} to create
	 */
	void create(I obj);

	/**
	 * {@code DELETE} all duplicate {@link I objects} but one and {@code UPDATE} it, or {@code INSERT} a new object.
	 * @param obj the {@link I object} to update
	 */
	void update(I obj);

	/**
	 * Find the {@link I object} with a given {@link I#id() id}.
	 * @param id the {@link I object's} {@link I#id() id}
	 * @return a newly built {@link I object}
	 */
	@Nullable
	I select(long id);

	/**
	 * Find {@link I#id() ids} of all similar {@link I objects}.
	 * @param obj the {@link I object} to find
	 * @return an array of {@link I#id() ids} of identical {@link I objects}
	 */
	@NotNull
	long[] findSimilar(I obj);

	/**
	 * Find all {@link I objects} in this enumeration.
	 * @return all {@link I objects} in the table
	 */
	@NotNull
	I[] values();

}
