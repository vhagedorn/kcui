package me.vadim.ja.kc.persist.impl;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.persist.LinguisticElement;
import me.vadim.ja.kc.persist.SpokenElement;
import me.vadim.ja.kc.persist.wrapper.Card;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
@XmlRootElement(name = "card")
@XmlAccessorType(XmlAccessType.NONE)
public class Kard implements Card {

	private static final HashFunction sha1 = Hashing.sha1();

	@XmlAttribute
	private Location location;

	@XmlElement
	private Kanji[] japanese = new Kanji[0];

	@XmlElement
	private Definition[] english = new Definition[0];

	@XmlElement
	private Grammar[] grammar = new Grammar[0];

	@XmlElement
	private Speak[] spoken = new Speak[0];

	Kard() { }

	Kard(Location location) {
		this.location = location;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public LinguisticElement[] getJapanese() {
		return Arrays.copyOf(japanese, japanese.length);
	}

	@Override
	public LinguisticElement[] getEnglish() {
		return Arrays.copyOf(english, english.length);
	}

	@Override
	public LinguisticElement[] getGrammar() {
		return Arrays.copyOf(grammar, grammar.length);
	}

	@Override
	public SpokenElement[] getSpoken() {
		return Arrays.copyOf(spoken, spoken.length);
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void setJapanese(String kanji) {
		japanese = kanji.codePoints().mapToObj(Character::toString).map(Kanji::new).toArray(Kanji[]::new);
	}

	@Override
	public void setEnglish(String... definitions) {
		english = Arrays.stream(definitions).map(Definition::new).toArray(Definition[]::new);
	}

	@Override
	public void setGrammar(LinguisticElement... parts) {
		grammar = Arrays.stream(parts).map(Grammar.class::cast).toArray(Grammar[]::new);
	}

	@Override
	public void setSpoken(SpokenElement... speak) {
		spoken = Arrays.stream(speak).map(Speak.class::cast).toArray(Speak[]::new);
	}

	@Override
	public String describeJapanese() {
		return Arrays.stream(japanese).map(LinguisticElement::describe).collect(Collectors.joining(""));
	}

	@Override
	public String describeGrammar() {
		return Arrays.stream(grammar).map(LinguisticElement::describe).collect(Collectors.joining(", "));
	}

	private static String join(String delim, LinguisticElement[] elems) {
		return Arrays.stream(elems).map(LinguisticElement::describe).collect(Collectors.joining(delim));
	}

	@Override
	public String toString() {
		return new StringBuilder("Card")
				.append(" { ")
				.append("japanese=").append(join("", japanese)).append(", ")
				.append("english=[").append(join("; ", english)).append("], ")
				.append("grammar=").append(join(" or ", grammar)).append(", ")
				.append("spoken=[").append(join(", ", spoken)).append("]")
				.append(" } ")
				.toString();
	}

	@Override
	public HashCode hash() {
		return sha1.hashString(describeJapanese(), StandardCharsets.UTF_8);
	}

}
