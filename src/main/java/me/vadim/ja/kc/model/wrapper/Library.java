package me.vadim.ja.kc.model.wrapper;

import me.vadim.ja.kc.model.xml.Location;

import java.util.List;

/**
 * @author vadim
 */
public interface Library {

	String getAuthor();

	void setAuthor(String author);

	Curriculum getCurriculum(String name);

	void unlinkCurriculum(Curriculum curriculum);

	void unlinkCard(Card card);

	List<Curriculum> getCurriculums();

	Card createCard(Location location);

	List<Card> getCards();

	List<Card> getCards(Curriculum curriculum);

	List<Card> getCards(Group group);

	List<Card> getCards(Location location);

	// prunes nulls after demarshalling
	void prune();

}
