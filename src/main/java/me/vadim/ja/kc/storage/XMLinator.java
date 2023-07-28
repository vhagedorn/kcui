package me.vadim.ja.kc.storage;

import me.vadim.ja.kc.storage.model.ReadContext;
import org.w3c.dom.Document;

/**
 * @author vadim
 */
public interface XMLinator<T> {

	T read(Document doc);

	void read(Document doc, ReadContext ctx);

	void write(T t, Document doc);

}
