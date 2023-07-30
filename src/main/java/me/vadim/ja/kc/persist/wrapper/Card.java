package me.vadim.ja.kc.persist.wrapper;

import me.vadim.ja.kc.persist.Hashable;
import me.vadim.ja.kc.persist.LinguisticElement;
import me.vadim.ja.kc.persist.SpokenElement;
import me.vadim.ja.kc.persist.decor.MultiLine;
import me.vadim.ja.kc.persist.decor.SingleLine;
import me.vadim.ja.kc.persist.impl.Location;

/**
 * @author vadim
 */
public interface Card extends Hashable {

	Location getLocation();

	@SingleLine LinguisticElement[] getJapanese();

	@MultiLine LinguisticElement[] getEnglish();

	@SingleLine LinguisticElement[] getGrammar();

	@MultiLine SpokenElement[] getSpoken();

	void setLocation(Location location);

	void setJapanese(String kanji);

	void setEnglish(String... definitions);

	void setGrammar(LinguisticElement... parts);

	void setSpoken(SpokenElement... speak);

	String describeJapanese();

	String describeGrammar();

	String toPreviewString();

	/**
	 * Copies data but not location from {@code card}.
	 * @param card same-impl {@link Card} instance
	 */
	void copyDataFrom(Card card);

}
