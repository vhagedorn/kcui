package me.vadim.ja.kc.db.impl;

import me.vadim.ja.kc.db.Identifiable;

/**
 * @author vadim
 */
@Deprecated
public interface IdCloneable<T extends IdCloneable<T>> extends Identifiable {

	T withId(long id);

}
