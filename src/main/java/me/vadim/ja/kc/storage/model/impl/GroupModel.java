package me.vadim.ja.kc.storage.model.impl;

import me.vadim.ja.kc.storage.model.Model;
import me.vadim.ja.kc.wrapper.Curriculum;
import me.vadim.ja.kc.wrapper.Group;

/**
 * @author vadim
 */
public class GroupModel extends Model<Group, CardModel> {

	GroupModel(String name, Model<Curriculum, ?> parent) {
		super(parent.model.createGroup(name));
	}

	@Override
	protected CardModel create(String param) {
		return new CardModel(param, this);
	}

	@Override
	public String getParam() {
		return model.name;
	}

}
