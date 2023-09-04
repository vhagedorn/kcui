package me.vadim.ja.kc.model.wrapper;

import me.vadim.ja.kc.model.Hashable;
import me.vadim.ja.kc.model.LinguisticElement;
import me.vadim.ja.kc.model.SpokenElement;
import me.vadim.ja.kc.model.decor.MultiLine;
import me.vadim.ja.kc.model.decor.SingleLine;
import me.vadim.ja.kc.model.xml.Location;

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

	int getRenderOpts();

	boolean hasRenderOptsOverride();

	// null to sync with curriculum
	void setRenderOptsOverride(Integer mask);

	/**
	 * Copies data but not location from {@code card}.
	 * @param card same-impl {@link Card} instance
	 */
	void copyDataFrom(Card card);

}
