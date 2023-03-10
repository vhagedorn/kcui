package me.vadim.ja.kc.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vadim
 */
public class Kanji extends IdAdapter {

	public final String value;
	public final Curriculum curriculum;
	public final Group group;

	public Kanji(long id, String value, Group group) {
		super(id);
		this.value      = value;
		this.curriculum = group.curriculum;
		this.group      = group;
	}

	public final Map<PronounciationType, List<String>> pronounciations = new HashMap<>();
	public final List<PartOfSpeech>                    partsOfSpeech   = new ArrayList<>();
	public final List<String>                          definitions     = new ArrayList<>();

	public void addPronounciation(PronounciationType type, String pronounciation){
		pronounciations.computeIfAbsent(type, x -> new ArrayList<>()).add(pronounciation);
	}

	public void addPartOfSpeech(PartOfSpeech pos){
		partsOfSpeech.add(pos);
	}

	public void addDefinition(String definition) {
		definitions.add(definition);
	}

	@Override
	public String toString() {
		return value;
	}
}
