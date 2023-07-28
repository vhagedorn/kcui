package me.vadim.ja.kc.storage.model.impl;

import me.vadim.ja.kc.storage.model.Model;
import me.vadim.ja.kc.wrapper.Group;
import me.vadim.ja.kc.wrapper.Kanji;

/**
 * @author vadim
 */
public class CardModel extends Model<Kanji, CardModel.NotImplemented> {

	public static final class NotImplemented extends Model {

		public NotImplemented(Object model) {
			super(model);
			throw new UnsupportedOperationException();
		}

		@Override
		protected Model create(String param) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getParam() {
			throw new UnsupportedOperationException();
		}

	}

	private final Model<Group, ?> parent;
	CardModel(String value, Model<Group, ?> parent) {
		super(new Kanji(value, parent.model));
		this.parent = parent;
	}

	public Model<Kanji, ?> rename(String newValue) {
		parent.delete(model.value);
		return parent.findOrNew(newValue);
	}

	@Override
	protected NotImplemented create(String param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParam() {
		throw new UnsupportedOperationException();
	}

}
