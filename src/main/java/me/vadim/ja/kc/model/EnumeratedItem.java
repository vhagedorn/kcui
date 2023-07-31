package me.vadim.ja.kc.model;

import java.util.Objects;

/**
 * @author vadim
 */
public class EnumeratedItem<T> {

	public final int index;
	public final T item;

	public EnumeratedItem(int index, T item) {
		this.index = index;
		this.item  = item;
	}

	@Override
	public String toString() {
		return Objects.toString(item);
	}

}
