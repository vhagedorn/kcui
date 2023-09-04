package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author vadim
 */
class AdapterEnglish extends XmlAdapter<String, ElementEnglish> {

	@Override
	public ElementEnglish unmarshal(String v) throws Exception {
		return new ElementEnglish(v);
	}

	@Override
	public String marshal(ElementEnglish v) throws Exception {
		return v.describe();
	}

}
