package me.vadim.ja.kc.storage.model;

import me.vadim.ja.kc.storage.model.impl.CardModel;
import me.vadim.ja.kc.storage.model.impl.CurriculumModel;
import me.vadim.ja.kc.storage.model.impl.GroupModel;
import me.vadim.ja.kc.storage.model.impl.LibraryModel;

/**
 * @author vadim
 */
public interface ReadContext {

	LibraryModel library();

	void advanceCurriculum(String name);
	CurriculumModel currentCurriculum();

	void advanceGroup(String name);
	GroupModel currentGroup();

	void advanceCard(String value);
	CardModel currentCard();

}
