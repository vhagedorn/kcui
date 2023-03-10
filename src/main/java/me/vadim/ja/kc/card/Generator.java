package me.vadim.ja.kc.card;

import me.vadim.ja.kc.wrapper.Kanji;
import me.vadim.ja.kc.wrapper.PronounciationType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
			for (Map.Entry<PronounciationType, List<String>> entry : kanji.pronounciations.entrySet()) {
				for (String p : entry.getValue()) {
					div = doc.createElement("div");

					//todo: orientation?
					//todo: pronounciation type?
					span = doc.createElement("span");
					span.attr("class", "psym");
					span.html("⛛");
					div.appendChild(span);

					span = doc.createElement("span");
					span.attr("class", "pron");
					span.html(p);
					div.appendChild(span);

					element.appendChild(div);
				}
			}

			//part of speech
			element = doc.getElementById("type");
			if (element == null)
				throw new IllegalStateException();
			element.html(kanji.partsOfSpeech.stream().map(x -> (x.info == null ? "" : x.info + " ") + x.name).collect(Collectors.joining(", ")));

			//definition
			element = doc.getElementById("definition");
			if (element == null)
				throw new IllegalStateException();
			element.html("");
			Element li;
			for (String def : kanji.definitions) {
				li = doc.createElement("li");
				li.html(def);
				element.appendChild(li);
			}

			htmlBack = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return htmlBack;
	}

}
