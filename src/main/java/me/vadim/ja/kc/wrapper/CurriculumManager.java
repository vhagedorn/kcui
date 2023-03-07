package me.vadim.ja.kc.wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vadim
 */
public class CurriculumManager {

	public final Map<Long, Curriculum> curriculums = new HashMap<>();

	public Curriculum genki() {
		return curriculums.computeIfAbsent(0L, (k) -> {
			Curriculum genki = new Curriculum(k, "Genki");
			Group lesson = new Group(0, "Lesson 3");
			genki.groups.add(lesson);
			return genki;
		});
	}


	public Kanji createKanji(String value){
		return new Kanji(0, value);
	}

}