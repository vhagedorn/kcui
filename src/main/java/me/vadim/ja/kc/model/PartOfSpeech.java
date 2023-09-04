package me.vadim.ja.kc.model;

import me.vadim.ja.kc.model.xml.KCFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vadim
 */
public enum PartOfSpeech {

	NOUN("proper"),
	PRONOUN, // todo pronoun as noun variant?
	VERB("godan", "ichidan", "irregular", "auxiliary", "transitive", "intransitive"),
	ADVERB,
	ADJECTIVE("na", "i", "no"),
	PARTICLE,
	PREPOSITION,
	CONJUNCTION,
	INTERJECTION;

	private final String[] variants;

	PartOfSpeech(String... variants) {
		this.variants = variants;
	}

	public List<EnumeratedItem<String>> getVariants() {
		List<EnumeratedItem<String>> e = new ArrayList<>(variants.length);
		for (int i = 0; i < variants.length; i++)
			 e.add(new EnumeratedItem<>(i, variants[i]));
		return e;
	}

	@Override
	public String toString() {
		return asLinguistic().describe();
	}

	public static final int NO_VARIANT = -1;

	/* noun variants */

	public static final int NOUN_PROPER = 0;

	/* verb variants */
	public static final int VERB_GODAN = 0;
	public static final int VERB_ICHIDAN = 1;
	public static final int VERB_IRREGULAR = 2;
	public static final int VERB_AUXILIARY = 3;
	public static final int VERB_TRANSITIVE = 4;
	public static final int VERB_INTRANSITIVE = 5;

	public LinguisticElement asLinguistic() {
		return asLinguistic(NO_VARIANT);
	}

	public LinguisticElement asLinguistic(int variant) {
		if (variant == NO_VARIANT)
			return KCFactory.ofGrammar(name().toLowerCase());
		if (variants.length == 0)
			throw new IllegalArgumentException("No variants! Use asLinguistic(NO_VARIANT) or asLingustic()!");
		if (variant < 0 || variant >= variants.length)
			throw new IllegalArgumentException("Invalid variant " + variant + " for " + Arrays.toString(variants) + "!");
		return KCFactory.ofGrammar(variants[variant] + " " + name().toLowerCase());
	}

	public static LinguisticElement fromDesc(String desc) {
		LinguisticElement linel;
		for (PartOfSpeech value : values()) {
			if ((linel = value.asLinguistic()).describe().equals(desc))
				return linel;
			for (int i = 0; i < value.variants.length; i++)
				if ((linel = value.asLinguistic(i)).describe().equals(desc))
					return linel;
		}
		return null;
	}

	// drops variants:
	// "variant part" -> "part"
	// "grammar" -> "grammar"
	public static PartOfSpeech fromLinguistic(LinguisticElement linel) {
		if (linel == null)
			return null;
		String[] split = linel.describe().split(" ");
		if (split.length == 0)
			return null;
		for (PartOfSpeech value : values())
			if (value.asLinguistic().describe().equals(split[split.length - 1]))
				return value;
		return null;
	}

	public static int variantFromLinguistic(LinguisticElement linel) {
		if (linel == null)
			return NO_VARIANT;
		String[] split = linel.describe().split(" ");
		if (split.length == 0)
			return NO_VARIANT;
		for (PartOfSpeech value : values())
			if (value.asLinguistic().describe().equals(split[split.length - 1]))
				for (int i = 0; i < value.variants.length; i++)
					if(value.variants[i].equals(split[0]))
						return i;
		return NO_VARIANT;
	}

}
