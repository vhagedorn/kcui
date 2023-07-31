package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.model.PronounciationType;

/**
 * @author vadim
 */
class AdapterSpoken extends XmlAdapter<String, ElementSpoken> {

	@Override
	public ElementSpoken unmarshal(String v) throws Exception {
		if (v == null || v.isBlank())
			throw new IllegalArgumentException(v == null ? null : "(empty)");

		return new ElementSpoken(v.substring(1), PronounciationType.fromID(Integer.parseInt(String.valueOf(v.charAt(0)))));
	}

	@Override
	public String marshal(ElementSpoken v) throws Exception {
		return v.toString();
	}

}
