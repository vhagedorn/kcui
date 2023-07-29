package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author vadim
 */
public class KanjiAdapter extends XmlAdapter<String, Kanji> {

	@Override
	public Kanji unmarshal(String v) throws Exception {
		return new Kanji(v);
	}

	@Override
	public String marshal(Kanji v) throws Exception {
		return v.describe();
	}

}
