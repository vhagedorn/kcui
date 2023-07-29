package me.vadim.ja.kc.persist;

import me.vadim.ja.kc.persist.impl.KCFactory;

import java.util.Arrays;

/**
 * @author vadim
 */
public enum PartOfSpeech {

	NOUN, // todo "proper" variant?
	PRONOUN, // todo pronoun as noun variant?
	VERB("godan", "ichidan", "irregular", "auxiliary"),
	ADVERB,
	ADJECTIVE,
	PARTICLE,
	PREPOSITION,
	CONJUNCTION,
	INTERJECTION;

	private final String[] variants;

	PartOfSpeech(String... variants) {
		this.variants = variants;
	}

	public static final int NO_VARIANT = -1;

	/* verb variants */
	public static final int VERB_GODAN = 0;
	public static final int VERB_ICHIDAN = 1;
	public static final int VERB_IRREGULAR = 2;
	public static final int VERB_AUXILIARY = 3;


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

}
