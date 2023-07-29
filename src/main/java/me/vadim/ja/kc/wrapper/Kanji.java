package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.persist.PronounciationType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class Kanji extends IdAdapter implements IdCloneable<Kanji> {

	public final String     value;
	public final Curriculum curriculum;
	public final Group      group;

	public final List<PartOfSpeech>   partsOfSpeech ;
	public final List<Pronounciation> pronounciations;
	public final List<Definition>     definitions;

	public Kanji(String value, Group group) {
		this.value      = value;
		this.curriculum = group.curriculum;
		this.group      = group;
		this.partsOfSpeech = new ArrayList<>(3);
		this.pronounciations = new ArrayList<>(4);
		this.definitions = new ArrayList<>(5);
	}

	private Kanji(String value, Group group, List<PartOfSpeech> partsOfSpeech, List<Pronounciation> pronounciations, List<Definition> definitions) {
		this.value          = value;
		this.curriculum     = group.curriculum;
		this.group          = group;
		this.partsOfSpeech  = partsOfSpeech;
		this.pronounciations = pronounciations;
		this.definitions    = definitions;
	}

	public static Builder builder() {
		return new Builder();
	}

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

	@Override
	public Kanji withId(long id) {
		Kanji copy = new Kanji(value, group);
		copy.partsOfSpeech.addAll(partsOfSpeech);
		copy.pronounciations.addAll(pronounciations);
		copy.definitions.addAll(definitions);
		copy.setId(id);
		return copy;
	}

	public Builder copy() {
		return builder().value(value).group(group).partsOfSpeech(partsOfSpeech).pronounciations(pronounciations).definitions(definitions);
	}

	@Override
	public String toString() {
		return value;
	}

	public String toPreviewString() {
		StringBuilder builder = new StringBuilder();

		builder.append(value);
		if (hasId())
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


	public static final class Builder {
		private long                 id = -1;
		private String               value;
		private Group                group;
		private List<PartOfSpeech>   partsOfSpeech;
		private List<Pronounciation> pronounciations;
		private List<Definition>     definitions;

		private Builder() {}

		public Builder id(long id) {
			this.id = id;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder group(Group group) {
			this.group = group;
			return this;
		}

		public Builder partsOfSpeech(List<PartOfSpeech> partsOfSpeech) {
			this.partsOfSpeech = partsOfSpeech;
			return this;
		}

		public Builder pronounciations(List<Pronounciation> pronounciations) {
			this.pronounciations = pronounciations;
			return this;
		}

		public Builder definitions(List<Definition> definitions) {
			this.definitions = definitions;
			return this;
		}

		public Kanji build() {
			Kanji kanji = new Kanji(value, group, partsOfSpeech, pronounciations, definitions);
			if(id != -1)
				kanji.setId(id);
			return kanji;
		}
	}
}
