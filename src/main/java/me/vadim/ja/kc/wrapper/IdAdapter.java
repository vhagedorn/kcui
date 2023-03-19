package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public abstract class IdAdapter implements Identifiable {

	private long id;
	private boolean isIdSet;

	public IdAdapter(){
		this.id = -1;
		this.isIdSet = false;
	}

	public IdAdapter(long id) {
		this.id = id;
		this.isIdSet = true;
	}

	public final boolean hasId(){
		return this.isIdSet;
	}

	public final void setId(long id){
		if(this.isIdSet)
			throw new UnsupportedOperationException("lazy value already initialized");

		this.id = id;
		this.isIdSet = true;
	}

	public final long getId(){
		if(!this.isIdSet)
			throw new UnsupportedOperationException("lazy value not initialized");

		return this.id;
	}

	@Override
	public final long id() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (obj instanceof Identifiable)
				return id == ((Identifiable) obj).id();
		} catch (RuntimeException ignored) {}
		return super.equals(obj);
	}
}