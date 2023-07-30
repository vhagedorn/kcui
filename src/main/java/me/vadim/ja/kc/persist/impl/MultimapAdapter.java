package me.vadim.ja.kc.persist.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.hash.HashCode;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.persist.io.JAXBStorage;
import me.vadim.ja.kc.persist.wrapper.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for {@link Lib#cards}.
 */
public class MultimapAdapter extends XmlAdapter<MultimapAdapter.AdaptedMultimap, ListMultimap<Location, Card>> {

	public static class AdaptedMultimap {

		public List<Entry> entry = new ArrayList<>();

	}

	@XmlType(namespace = "multi")
	public static class Entry {

		@XmlAttribute
		public Location key;
		@XmlAttribute
		public String kanji; // for the benefit of anyone trying to read the XML file
		@XmlValue
		public String value;

	}

	@Override
	public AdaptedMultimap marshal(ListMultimap<Location, Card> multimap) throws Exception {
		AdaptedMultimap adaptedMultimap = new AdaptedMultimap();
		for (Location key : multimap.keySet()) {
			for (Card value : multimap.get(key)) {
				Entry entry = new Entry();
				entry.key   = key;
				entry.kanji = value.describeJapanese();
				entry.value = value.hash().toString();
				adaptedMultimap.entry.add(entry);
			}
		}
		return adaptedMultimap;
	}

	@Override
	public ListMultimap<Location, Card> unmarshal(AdaptedMultimap adaptedMultimap) throws Exception {
		ListMultimap<Location, Card> multimap = ArrayListMultimap.create();
		for (Entry entry : adaptedMultimap.entry)
			multimap.put(entry.key, JAXBStorage.readCard(entry.key, HashCode.fromString(entry.value)));
		return multimap;
	}

}