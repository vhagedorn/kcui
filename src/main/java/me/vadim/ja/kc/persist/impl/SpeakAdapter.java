package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.persist.PronounciationType;

/**
 * @author vadim
 */
public class SpeakAdapter extends XmlAdapter<String, Speak> {

	@Override
	public Speak unmarshal(String v) throws Exception {
		if(v == null || v.isBlank())
			throw new IllegalArgumentException(v == null ? null : "(empty)");

		return new Speak(v.substring(1), PronounciationType.fromID(Integer.parseInt(String.valueOf(v.charAt(0)))));
	}

	@Override
	public String marshal(Speak v) throws Exception {
		return v.toString();
	}

}
