package me.vadim.ja.kc.render.factory;

import me.vadim.ja.kc.ResourceAccess;
import me.vadim.ja.kc.wrapper.Definition;
import me.vadim.ja.kc.wrapper.Kanji;
import me.vadim.ja.kc.wrapper.PartOfSpeech;
import me.vadim.ja.kc.wrapper.Pronounciation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class Generator implements ResourceAccess {

	public static final String back  = "doc/back.html";
	public static final String front = "doc/front.html";

	public final Kanji kanji;

	public Generator(Kanji kanji) {
		this.kanji = kanji;
	}

	private Document htmlFront, htmlBack;

	public Document createFront() {
		try {
			Document doc     = Jsoup.parse(loadResource(front), StandardCharsets.UTF_8.name(), "");
			Element  element = doc.getElementById("kanji");
			if (element == null)
				throw new IllegalStateException();
			element.html(kanji.value);
			htmlFront = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return htmlFront;
	}

	public Document createBack(String[] base64) {
		try {
			Document doc = Jsoup.parse(loadResource(back), StandardCharsets.UTF_8.name(), "");
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
			kanji.pronounciations.sort(Comparator.comparingInt(x -> x.index));
			for (Pronounciation pronounciation : kanji.pronounciations) {
				div = doc.createElement("div");

				//todo: orientation?
				//todo: pronounciation type?
				span = doc.createElement("span");
				span.attr("class", "psym");
				span.html("⛛");// <- todo
				div.appendChild(span);

				span = doc.createElement("span");
				span.attr("class", "pron");
				span.html(pronounciation.value);
				div.appendChild(span);

				element.appendChild(div);
			}

			//part of speech
			element = doc.getElementById("type");
			if (element == null)
				throw new IllegalStateException();
			element.html(kanji.partsOfSpeech.stream()
											.sorted(Comparator.comparingInt(PartOfSpeech::getPriority))
											.map(PartOfSpeech::toInfoString)
											.collect(Collectors.joining(", ")));

			//definition
			element = doc.getElementById("definition");
			if (element == null)
				throw new IllegalStateException();
			element.html("");
			Element li;
			kanji.definitions.sort(Comparator.comparingInt(x -> x.index));
			for (Definition def : kanji.definitions) {
				li = doc.createElement("li");
				li.html(def.value);
				element.appendChild(li);
			}

			htmlBack = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return htmlBack;
	}

}
