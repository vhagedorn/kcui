package me.vadim.ja.kc.render.impl.factory;

import me.vadim.ja.kc.model.LinguisticElement;
import me.vadim.ja.kc.model.SpokenElement;
import me.vadim.ja.kc.model.wrapper.Card;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

//todo: catch IllegalStateExceptions and show popup dialog

/**
 * @author vadim
 */
public class Generator {

	@SuppressWarnings("StaticNonFinalField")
	public static String
			back = null,
			front = null;

	public final Card kanji;

	public Generator(Card kanji) {
		this.kanji = kanji;
	}

	private Document htmlFront, htmlBack;

	public Document createFront() {
		try {
			Document doc     = Jsoup.parse(front);
			Element  element = doc.getElementById("kanji");
			if (element == null)
				throw new IllegalStateException();
			element.html(kanji.describeJapanese());
			htmlFront = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return htmlFront;
	}

	public Document createBack(String[] base64) {
		try {
			Document doc = Jsoup.parse(back);
			Element  element;

			//stroke diagrams
			element = doc.getElementById("diagrams");
			if (element == null)
				throw new IllegalStateException();
			element.html("");//clear
			for (String encoded : base64) {
				Element img = doc.createElement("img");
				img.attr("src", "data:image/png;base64," + encoded);
				img.attr("class", "stroke-center");
				element.appendChild(img);
			}

			//pronounciation
			element = doc.getElementById("pronounciation");
			if (element == null)
				throw new IllegalStateException();
			element.html("");
			Element div;
			Element span;
			for (SpokenElement pronounciation : kanji.getSpoken()) {
				div = doc.createElement("div");

				//todo: orientation?
				//todo: pronounciation type?
				span = doc.createElement("span");
				span.attr("class", "psym");
				span.html("⛛");// <- todo
				div.appendChild(span);

				span = doc.createElement("span");
				span.attr("class", "pron");
				span.html(pronounciation.describe());
				div.appendChild(span);

				element.appendChild(div);
			}

			//part of speech
			element = doc.getElementById("type");
			if (element == null)
				throw new IllegalStateException();
			element.html(kanji.describeGrammar());

			//definition
			element = doc.getElementById("definition");
			if (element == null)
				throw new IllegalStateException();
			element.html("");
			Element li;
			for (LinguisticElement def : kanji.getEnglish()) {
				li = doc.createElement("li");
				li.html(def.describe());
				element.appendChild(li);
			}

			htmlBack = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return htmlBack;
	}

}
