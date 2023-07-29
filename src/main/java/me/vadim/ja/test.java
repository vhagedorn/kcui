package me.vadim.ja;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.vadim.ja.kc.persist.PartOfSpeech;
import me.vadim.ja.kc.persist.impl.KCFactory;
import me.vadim.ja.kc.persist.impl.Location;
import me.vadim.ja.kc.persist.io.JAXBStorage;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vadim
 */
public class test {

	private static final AtomicLong uniqueId = new AtomicLong(0);

	public static long shuffleBits(long val) {
		long result = 0;
		result |= (val & 0xFF00000000000000L) >> 56;
		result |= (val & 0x00FF000000000000L) >> 40;
		result |= (val & 0x0000FF0000000000L) >> 24;
		result |= (val & 0x000000FF00000000L) >> 8;
		result |= (val & 0x00000000FF000000L) << 8;
		result |= (val & 0x0000000000FF0000L) << 24;
		result |= (val & 0x000000000000FF00L) << 40;
		result |= (val & 0x00000000000000FFL) << 56;
		return result;
	}

	public static long id() {
		// get a unique current-time-millis value
		long now;
		long prev;
		do {
			prev = uniqueId.get();
			now  = System.currentTimeMillis();
			// make sure now is moving ahead and unique
			if (now <= prev) {
				now = prev + 1;
			}
			// loop if someone else has updated the id
		}
		while (!uniqueId.compareAndSet(prev, now));

		// shuffle it
		return shuffleBits(now);
	}

	public static void main(String[] args) throws Exception {

		System.out.println(Long.toHexString(id()));
		System.out.println(Long.toHexString(id()));
		System.out.println(Long.toHexString(id()));
		System.out.println(Long.toHexString(id()));
		System.out.println(id());
		System.out.println(id());
		System.out.println(id());
		System.out.println(id());

		System.out.println();

		HashFunction sha1 = Hashing.sha1();

		System.out.println(sha1.hashString("My Test String", StandardCharsets.UTF_8));
		System.out.println(sha1.hashString("My Test String", StandardCharsets.UTF_8));
		System.out.println(sha1.hashString("My Test String1", StandardCharsets.UTF_8));

		System.out.println();

		System.out.println(Arrays.toString("Testing".split(",")));
		System.out.println(Arrays.toString("Testing,1,2".split(",")));
		System.out.println(String.join(",", new String[] { "Testing" }));

		System.out.println();

		System.out.println(PartOfSpeech.NOUN.asLinguistic().describe());
		System.out.println(PartOfSpeech.VERB.asLinguistic(0).describe());
		System.out.println(PartOfSpeech.VERB.asLinguistic().describe());

		System.out.println();

		Library lib = KCFactory.newLibrary("me");

		Curriculum curriculum = lib.getCurriculum("Test");
		Group      group      = curriculum.addGroup("group1");
		Location   loc        = new Location(curriculum, group);

		Card card = lib.createCard(loc);
		card.setJapanese("kanji");
		card.setEnglish("a fancy definition that", "tests the multiline abilities", "of this very sophisticated", "tool for some reason know", "as jakarta instead of javax");
		card.setGrammar(PartOfSpeech.NOUN.asLinguistic(), PartOfSpeech.VERB.asLinguistic(PartOfSpeech.VERB_ICHIDAN));

		JAXBStorage.dumpLib(lib, new File("./lib.xml"));
		lib.getCards().forEach(JAXBStorage::dumpCard);
		System.out.println("<- " + lib);

		lib = JAXBStorage.readLib(new File("./lib.xml"));
		System.out.println("-> " + lib);
		//todo: when saving a card... if the the kanji text has been edited, then delete the file with the old hash
	}

}
