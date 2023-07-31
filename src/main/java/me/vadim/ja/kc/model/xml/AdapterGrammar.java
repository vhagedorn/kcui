package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.model.PartOfSpeech;

/**
 * @author vadim
 */
class AdapterGrammar extends XmlAdapter<String, ElementGrammar> {

	@Override
	public ElementGrammar unmarshal(String v) throws Exception {
		return (ElementGrammar) PartOfSpeech.fromDesc(v);
	}

	@Override
	public String marshal(ElementGrammar v) throws Exception {
		return v.describe();
	}

}
