package me.vadim.ja.kc.storage.model.impl;

import me.vadim.ja.kc.storage.model.ReadContext;

/**
 * @author vadim
 */
public class XMLReadContext implements ReadContext {

	private final LibraryModel library;

	private CurriculumModel curriculum;
	private GroupModel      group;
	private CardModel       card;

	public XMLReadContext(LibraryModel library) {
		this.library = library;
	}

	@Override
	public LibraryModel library() {
		return library;
	}

	@Override
	public void advanceCurriculum(String name) {
		curriculum = library.findOrNew(name);
	}

	@Override
	public CurriculumModel currentCurriculum() {
		return curriculum;
	}

	@Override
	public void advanceGroup(String name) {
		group = curriculum.findOrNew(name);
	}

	@Override
	public GroupModel currentGroup() {
		return group;
	}

	@Override
	public void advanceCard(String value) {
		card = group.findOrNew(value);
	}

	@Override
	public CardModel currentCard() {
		return card;
	}

}
