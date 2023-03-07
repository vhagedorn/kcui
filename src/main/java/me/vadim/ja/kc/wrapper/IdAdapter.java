package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public abstract class IdAdapter implements Identifiable {

	private final long id;

	public IdAdapter(long id) {
		this.id = id;
	}

	@Override
	public long id() {
		return this.id;
	}
}
