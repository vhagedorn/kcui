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

	public Kanji(long id, String value) {
		super(id);
		this.value = value;
	}

	public final Map<PronounciationType, List<String>> pronounciations = new HashMap<>();
	public final List<PartOfSpeech>                    partsOfSpeech   = new ArrayList<>();
	public final List<String>                          definitions     = new ArrayList<>();


}
