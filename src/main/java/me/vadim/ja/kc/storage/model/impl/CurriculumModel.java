package me.vadim.ja.kc.storage.model.impl;

import me.vadim.ja.kc.storage.model.Model;
import me.vadim.ja.kc.wrapper.Curriculum;

/**
 * @author vadim
 */
public class CurriculumModel extends Model<Curriculum, GroupModel> {

	CurriculumModel(String name) {
		super(new Curriculum(name));
	}

	@Override
	protected GroupModel create(String param) {
		return new GroupModel(param, this);
	}

	@Override
	public String getParam() {
		return model.getName();
	}

}
