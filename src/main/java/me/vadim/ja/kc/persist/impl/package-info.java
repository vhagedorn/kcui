/**
 * @author vadim
 */
@XmlJavaTypeAdapters({
		@XmlJavaTypeAdapter(value = KanjiAdapter.class, type = Kanji.class),
		@XmlJavaTypeAdapter(value = DefinitionAdapter.class, type = Definition.class),
		@XmlJavaTypeAdapter(value = GrammarAdapter.class, type = Grammar.class),
		@XmlJavaTypeAdapter(value = LocationAdapter.class, type = Location.class),
		@XmlJavaTypeAdapter(value = MultimapAdapter.class, type = ListMultimap.class),
		@XmlJavaTypeAdapter(value = MapAdapter.class, type = Map.class),
})
// this some bullshit right here
// how the fuck does this even work??
package me.vadim.ja.kc.persist.impl;

import com.google.common.collect.ListMultimap;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import java.util.Map;
