package me.vadim.ja.kc.storage.model.impl;

import me.vadim.ja.kc.storage.model.Model;

/**
 * @author vadim
 */
public class LibraryModel extends Model<Void, CurriculumModel> {

	public LibraryModel() {
		super(null);
	}

	@Override
	protected CurriculumModel create(String param) {
		return new CurriculumModel(param);
	}

	@Override
	public String getParam() {
		throw new UnsupportedOperationException();
	}

}
