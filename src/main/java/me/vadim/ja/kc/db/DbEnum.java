package me.vadim.ja.kc.db;

import me.vadim.ja.kc.wrapper.Identifiable;
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
	void insert(I obj);

	/**
	 * Update the given {@link I object} with new values.
	 * @param obj the {@link I#id() id} of the row to update and the new {@link I object} with to update it
	 */
	void update(I obj);

	/**
	 * Delete all duplicate {@link I objects} but one and {@link #update(Identifiable) update} it, or {@link #insert(Identifiable) create} a new object.
	 * @param obj the {@link I object} to update
	 */
	void upsert(I obj);

	/**
	 * Find {@link I#id() ids} of all similar {@link I objects}.
	 * @param obj the {@link I object} to find
	 * @return an array of {@link I#id() ids} of identical {@link I objects}
	 */
	@NotNull
	long[] query(I obj);

	/**
	 * Find the {@link I object} with a given {@link I#id() id}.
	 * @param id the {@link I object's} {@link I#id() id}
	 * @return a newly built {@link I object}
	 */
	@Nullable
	I query(long id);

	/**
	 * Find all {@link I objects} in this enumeration.
	 * @return all {@link I objects} in the table
	 */
	@NotNull
	I[] query();

}
