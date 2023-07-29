package me.vadim.ja.kc.persist.wrapper;

import me.vadim.ja.kc.persist.Hashable;
import me.vadim.ja.kc.persist.LinguisticElement;
import me.vadim.ja.kc.persist.impl.Location;

/**
 * @author vadim
 */
public interface Card extends Hashable {

	Location getLocation();

	LinguisticElement[] getJapanese();

	LinguisticElement[] getEnglish();

	LinguisticElement[] getGrammar();

	void setLocation(Location location);

	void setJapanese(String kanji);

	void setEnglish(String... definitions);

	void setGrammar(LinguisticElement... parts);

	String describeJapanese();

}
