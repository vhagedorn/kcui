package me.vadim.ja.kc.persist.impl;

import me.vadim.ja.kc.persist.PronounciationType;
import me.vadim.ja.kc.persist.SpokenElement;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Library;
import me.vadim.ja.kc.persist.LinguisticElement;

/**
 * @author vadim
 */
public class KCFactory {

	public static Card newCard(Location location) {
		return new Kard(location);
	}

	public static Library newLibrary(String author) {
		return new Lib(author);
	}

	public static final LinguisticElement ofKanji(String info) {
		return new Kanji(info);
	}

	public static final LinguisticElement ofDefinition(String info) {
		return new Definition(info);
	}

	public static final LinguisticElement ofGrammar(String info) {
		return new Grammar(info);
	}

	public static final SpokenElement ofSpoken(String info, PronounciationType type) {
		return new Speak(info, type);
	}

}
