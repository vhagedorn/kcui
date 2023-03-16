package me.vadim.ja.kc.wrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

	public List<PartOfSpeech> getPartsOfSpeech() {
		partsOfSpeech.sort(Comparator.comparingInt(PartOfSpeech::getPriority));
		return new ArrayList<>(partsOfSpeech);
	}

	public List<Pronounciation> getPronounciations() {
		pronounciations.sort(Comparator.comparingInt(Pronounciation::getIndex));
		return new ArrayList<>(pronounciations);
	}

	public List<Definition> getDefinitions() {
		definitions.sort(Comparator.comparingInt(Definition::getIndex));
		return new ArrayList<>(definitions);
	}

	public String toGrammarString() {
		return getPartsOfSpeech().stream()
								 .map(PartOfSpeech::toInfoString)
								 .collect(Collectors.joining(", "));
	}

	public Kanji withId(long id) {
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

	public String toPreviewString() {
		StringBuilder builder = new StringBuilder();

		builder.append(value);
		if (isIdSet())
			builder.append('(').append(id()).append(')');
		else
			builder.append("(#)");

		builder.append(" { ");
		builder.append('[').append(getPartsOfSpeech().stream().map(PartOfSpeech::toInfoString).collect(Collectors.joining(", "))).append(']');
		builder.append(", ");
		builder.append('[').append(getPronounciations().stream().map(Pronounciation::toString).collect(Collectors.joining(", "))).append(']');
		builder.append(", ");
		builder.append('[').append(getDefinitions().stream().map(Definition::toString).collect(Collectors.joining(", "))).append(']');
		builder.append(" }");

		return builder.toString();
	}


}
