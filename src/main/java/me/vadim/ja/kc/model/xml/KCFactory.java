package me.vadim.ja.kc.model.xml;

import me.vadim.ja.kc.model.LibraryContext;
import me.vadim.ja.kc.model.PronounciationType;
import me.vadim.ja.kc.model.SpokenElement;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Library;
import me.vadim.ja.kc.model.LinguisticElement;

/**
 * @author vadim
 */
public final class KCFactory {

	public static Card newCard(Location location) {
		return new ImplCard(location);
	}

	public static LibraryContext loadDefault() {
		return new LibCtx();
	}

	public static Library newLibrary(String author) {
		return new ImplLibrary(author);
	}

	public static final LinguisticElement ofKanji(String info) {
		return new ElementJapanese(info);
	}

	public static final LinguisticElement ofDefinition(String info) {
		return new ElementEnglish(info);
	}

	public static final LinguisticElement ofGrammar(String info) {
		return new ElementGrammar(info);
	}

	public static final SpokenElement ofSpoken(String info, PronounciationType type) {
		return new ElementSpoken(info, type);
	}

}
