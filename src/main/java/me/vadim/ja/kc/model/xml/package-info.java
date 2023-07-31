/**
 * This package is unfortunately crowded and the names are not quite how I like them,
 * however due to Java's class-level visibility being limited to only package-private
 * means that I can't separate these out into packages.
 * @author vadim
 */
@XmlJavaTypeAdapters({
		@XmlJavaTypeAdapter(value = AdapterJapanese.class, type = ElementJapanese.class),
		@XmlJavaTypeAdapter(value = AdapterEnglish.class, type = ElementEnglish.class),
		@XmlJavaTypeAdapter(value = AdapterGrammar.class, type = ElementGrammar.class),
		@XmlJavaTypeAdapter(value = AdapterSpoken.class, type = ElementSpoken.class),
		@XmlJavaTypeAdapter(value = AdapterLocation.class, type = Location.class),
		@XmlJavaTypeAdapter(value = AdapterCards.class, type = ListMultimap.class),
		@XmlJavaTypeAdapter(value = AdapterCurriculums.class, type = Map.class),
})
// this some bullshit right here
// how the fuck does this even work??
package me.vadim.ja.kc.model.xml;

import com.google.common.collect.ListMultimap;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import java.util.Map;
