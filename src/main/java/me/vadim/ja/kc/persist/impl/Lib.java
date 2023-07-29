package me.vadim.ja.kc.persist.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vadim
 */
@XmlRootElement(name = "library")
@XmlAccessorType(XmlAccessType.NONE)
public class Lib implements Library {

	@XmlAttribute
	private String author;

	Lib() {}

	Lib(String author) {
		setAuthor(author);
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@XmlElement
	private final Map<String, Curriculum> curriculums = new HashMap<>();

	@XmlElement
	public final ListMultimap<Location, Card> cards = ArrayListMultimap.create();

	@Override
	public Curriculum getCurriculum(String name) {
		return curriculums.computeIfAbsent(name, Curr::new);
	}

	@Override
	public void unlinkCurriculum(Curriculum curriculum) {
		curriculums.remove(curriculum.getName());
	}

	@Override
	public List<Curriculum> getCurriculums() {
		return new ArrayList<>(curriculums.values());
	}

	@Override
	public Card createCard(Location location) {
		location.flatten(this);
		Card card = new Kard(location);
		cards.get(location).add(card);
		return card;
	}

	@Override
	public List<Card> getCards() {
		return flatten(new ArrayList<>(cards.values()));
	}

	@Override
	public List<Card> getCards(Curriculum curriculum) {
		curriculum = getCurriculum(curriculum.getName());
		List<Card> result = new ArrayList<>(cards.size());
		for (Location key : cards.keySet())
			if (key.getCurriculum().equals(curriculum))
				result.addAll(cards.get(key));
		return flatten(result);
	}

	@Override
	public List<Card> getCards(Group group) {
		List<Card> result = new ArrayList<>(cards.size());
		for (Location key : cards.keySet())
			if (key.getGroup().equals(group))
				result.addAll(cards.get(key));
		return flatten(result);
	}

	@Override
	public List<Card> getCards(Location location) {
		location.flatten(this);
		return flatten(new ArrayList<>(cards.get(location)));
	}

	private List<Card> flatten(List<Card> cards) {
		cards.forEach(c -> c.getLocation().flatten(this));
		return cards;
	}

	@Override
	public String toString() {
		return new StringBuilder(author).append("'s Library")
										.append(" ( ")
										.append(cards.values().size()).append(" card(s) in ").append(curriculums.size()).append(" curriculum(s)")
										.append(" ) ")
										.toString();
	}

}
