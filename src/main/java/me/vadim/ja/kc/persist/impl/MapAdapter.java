package me.vadim.ja.kc.persist.impl;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import me.vadim.ja.kc.persist.wrapper.Curriculum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for {@link Lib#curriculums}.
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, Curriculum>> {

	public static class AdaptedMap {

		public List<Entry> entry = new ArrayList<>();

	}

	@XmlType(namespace = "map")
	public static class Entry {

		@XmlAttribute
		public String key;
		@XmlElement
		public Curr value;

	}

	@Override
	public AdaptedMap marshal(Map<String, Curriculum> map) throws Exception {
		AdaptedMap adaptedMap = new AdaptedMap();
		for (String key : map.keySet()) {
			Entry entry = new Entry();
			entry.key   = key;
			entry.value = (Curr) map.get(key);
			adaptedMap.entry.add(entry);
		}
		return adaptedMap;
	}

	@Override
	public Map<String, Curriculum> unmarshal(AdaptedMap adaptedMap) throws Exception {
		Map<String, Curriculum> map = new HashMap<>();
		for (Entry entry : adaptedMap.entry) {
			map.put(entry.key, entry.value);
		}
		return map;
	}

}