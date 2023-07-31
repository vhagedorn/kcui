package me.vadim.ja.kc.model.xml;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author vadim
 */
@XmlRootElement(name = "library")
@XmlAccessorType(XmlAccessType.NONE)
class ImplLibrary implements Library {

	@XmlAttribute
	private String author;

	ImplLibrary() { }

	ImplLibrary(String author) {
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
		return curriculums.computeIfAbsent(name, ImplCurriculum::new);
	}

	@Override
	public void unlinkCurriculum(Curriculum curriculum) {
		curriculums.remove(curriculum.getName());
	}

	@Override
	public void unlinkCard(Card card) {
		cards.get(card.getLocation()).remove(card);
	}

	@Override
	public List<Curriculum> getCurriculums() {
		return new ArrayList<>(curriculums.values());
	}

	@Override
	public Card createCard(Location location) {
		location.flatten(this);
		Card card = new ImplCard(location);
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

	@Override
	public void prune() {
		for (Location key : cards.keySet()) {
			Iterator<Card> iter = cards.get(key).iterator();
			while (iter.hasNext())
				if (iter.next() == null) {
					System.err.println("> Pruning invalid card @" + key);
					iter.remove();
				}
		}

		Iterator<String> iter = curriculums.keySet().iterator();
		String key;
		while(iter.hasNext()) {
			key = iter.next();
			if(curriculums.get(key) == null) {
				System.err.println("> Pruning invalid curriculum " + key);
				iter.remove();
			}
		}
	}

	private List<Card> flatten(List<Card> cards) {
		cards.forEach(c -> c.getLocation().flatten(this));
		return cards;
	}

	@Override
	public String toString() {
		return new StringBuilder(Optional.ofNullable(author).orElse("Anonymous")).append("'s Library")
																				 .append(" { ")
																				 .append(cards.values().size()).append(" card(s) in ").append(curriculums.size()).append(" curriculum(s)")
																				 .append(" }")
																				 .toString();
	}

}
