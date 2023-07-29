package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.PronounciationType;
import me.vadim.ja.kc.persist.SpokenElement;

/**
 * @author vadim
 */
class Speak extends LinEl implements SpokenElement {

	final PronounciationType type;

	Speak(String info, PronounciationType type) {
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
