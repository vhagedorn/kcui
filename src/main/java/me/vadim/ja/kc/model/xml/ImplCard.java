package me.vadim.ja.kc.model.xml;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.model.LinguisticElement;
import me.vadim.ja.kc.model.SpokenElement;
import me.vadim.ja.kc.model.wrapper.Card;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
@XmlRootElement(name = "card")
@XmlAccessorType(XmlAccessType.NONE)
class ImplCard implements Card {

	private static final HashFunction sha1 = Hashing.sha1();

	@XmlAttribute
	private Location location;

	@XmlElement
	private ElementJapanese[] japanese = new ElementJapanese[0];

	@XmlElement
	private ElementEnglish[] english = new ElementEnglish[0];

	@XmlElement
	private ElementGrammar[] grammar = new ElementGrammar[0];

	@XmlElement
	private ElementSpoken[] spoken = new ElementSpoken[0];

	ImplCard() { }

	ImplCard(Location location) {
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
		japanese = kanji.codePoints().mapToObj(Character::toString).map(ElementJapanese::new).toArray(ElementJapanese[]::new);
	}

	@Override
	public void setEnglish(String... definitions) {
		english = Arrays.stream(definitions).map(ElementEnglish::new).toArray(ElementEnglish[]::new);
	}

	@Override
	public void setGrammar(LinguisticElement... parts) {
		grammar = Arrays.stream(parts).map(ElementGrammar.class::cast).toArray(ElementGrammar[]::new);
	}

	@Override
	public void setSpoken(SpokenElement... speak) {
		spoken = Arrays.stream(speak).map(ElementSpoken.class::cast).toArray(ElementSpoken[]::new);
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

	@XmlAttribute
	Integer renderOpts;

	@Override
	public int getRenderOpts() {
		return renderOpts == null ? location.getCurriculum().getDefaultRenderOpts() : renderOpts;
	}

	@Override
	public boolean hasRenderOptsOverride() {
		return renderOpts != null;
	}

	@Override
	public void setRenderOptsOverride(Integer mask) {
		this.renderOpts = mask;
	}

	@Override
	public void copyDataFrom(Card card) {
		if (!(card instanceof ImplCard))
			throw new IllegalArgumentException(card.toPreviewString());
		ImplCard kard = (ImplCard) card;
		japanese   = kard.japanese;
		english    = kard.english;
		grammar    = kard.grammar;
		spoken     = kard.spoken;
		renderOpts = kard.renderOpts;
	}

	@Override
	public String toString() {
		return describeJapanese();
	}

	@Override
	public String toPreviewString() {
		return new StringBuilder("Card")
				.append("@").append(location)
				.append(" { ")
				.append("japanese=").append(join("", japanese)).append(", ")
				.append("english=[").append(join("; ", english)).append("], ")
				.append("grammar=").append(join(" or ", grammar)).append(", ")
				.append("spoken=[").append(join(", ", spoken)).append("]")
				.append(" }")
				.toString();
	}

	@Override
	public HashCode hash() {
		return sha1.hashString(describeJapanese(), StandardCharsets.UTF_8);
	}

}
