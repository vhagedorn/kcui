package me.vadim.ja.kc.storage.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vadim
 */
public abstract class Model<M, U extends Model> implements Named {

	public final    M       model;
	protected final List<U> uL = new ArrayList<>();
	protected final Map<String, U> uM = new HashMap<>();

	protected Model(M model) {
		this.model = model;
	}

	public void delete(String param) {
		uM.remove(param);
		uL.removeIf(u -> param.equals(u.getParam()));
	}

	public U newModel(String param) {
		U u = create(param);
		uL.add(u);
		return u;
	}

	public U findOrNew(String param) {
		return uM.computeIfAbsent(param, this::newModel);
	}

	protected abstract U create(String param);

}
