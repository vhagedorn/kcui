package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author vadim
 */
class AdapterJapanese extends XmlAdapter<String, ElementJapanese> {

	@Override
	public ElementJapanese unmarshal(String v) throws Exception {
		return new ElementJapanese(v);
	}

	@Override
	public String marshal(ElementJapanese v) throws Exception {
		return v.describe();
	}

}
