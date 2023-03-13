package me.vadim.ja.kc.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
public class Kanji extends IdAdapter {

	public final String     value;
	public final Curriculum curriculum;
	public final Group      group;

	public Kanji(String value, Group group) {
		this.value      = value;
		this.curriculum = group.curriculum;
		this.group      = group;
	}

	public final List<PartOfSpeech>   partsOfSpeech   = new ArrayList<>();
	public final List<Pronounciation> pronounciations = new ArrayList<>();
	public final List<Definition>     definitions     = new ArrayList<>();

	public void addPartOfSpeech(PartOfSpeech pos) {
		partsOfSpeech.add(pos);
	}

	public void addPronounciation(PronounciationType type, String pronounciation) {
		pronounciations.add(Pronounciation.builder().value(pronounciation).type(type).index(pronounciations.size()).build());
	}

	public void addDefinition(String definition) {
		definitions.add(Definition.builder().value(definition).index(definitions.size()).build());
	}

	public Kanji withId(long id){
		Kanji copy = new Kanji(value, group);
		copy.partsOfSpeech.addAll(partsOfSpeech);
		copy.pronounciations.addAll(pronounciations);
		copy.definitions.addAll(definitions);
		copy.setId(id);
		return copy;
	}

	@Override
	public String toString() {
		return value;
	}
}
