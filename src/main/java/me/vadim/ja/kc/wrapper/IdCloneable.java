package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public interface IdCloneable<T extends IdCloneable<T>> extends Identifiable {

	T withId(long id);

}
