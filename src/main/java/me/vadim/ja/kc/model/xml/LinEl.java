package me.vadim.ja.kc.model.xml;

import me.vadim.ja.kc.model.LinguisticElement;

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
