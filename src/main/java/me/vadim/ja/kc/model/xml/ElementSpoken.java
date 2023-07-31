package me.vadim.ja.kc.model.xml;

import me.vadim.ja.kc.model.PronounciationType;
import me.vadim.ja.kc.model.SpokenElement;

/**
 * @author vadim
 */
class ElementSpoken extends LinEl implements SpokenElement {

	final PronounciationType type;

	ElementSpoken(String info, PronounciationType type) {
		super(info);
		this.type = type;
	}

	@Override
	public PronounciationType getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.ordinal() + describe();
	}

}
