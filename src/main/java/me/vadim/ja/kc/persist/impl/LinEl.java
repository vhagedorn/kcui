package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.LinguisticElement;

/**
 * @author vadim
 */
abstract class LinEl implements LinguisticElement {

	final String info;

	LinEl(String info) {
		if (info == null)
			throw new NullPointerException("info");
		this.info = info;
	}

	@Override
	public String describe() {
		return info;
	}

	@Override
	public String toString() {
		return describe();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
