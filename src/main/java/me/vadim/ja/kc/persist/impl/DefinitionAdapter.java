package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author vadim
 */
public class DefinitionAdapter extends XmlAdapter<String, Definition> {

	@Override
	public Definition unmarshal(String v) throws Exception {
		return new Definition(v);
	}

	@Override
	public String marshal(Definition v) throws Exception {
		return v.describe();
	}

}
