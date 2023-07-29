package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.persist.PartOfSpeech;

/**
 * @author vadim
 */
public class GrammarAdapter extends XmlAdapter<String, Grammar> {

	@Override
	public Grammar unmarshal(String v) throws Exception {
		return (Grammar) PartOfSpeech.fromDesc(v);
	}

	@Override
	public String marshal(Grammar v) throws Exception {
		return v.describe();
	}

}
