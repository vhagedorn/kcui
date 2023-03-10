package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.card.FlashcardPipeline;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class CurriculumManager {

	public static final CurriculumManager cringe = new CurriculumManager();

	public final Map<Long, Curriculum> curriculums = new HashMap<>();

	public Curriculum genki() {
		return curriculums.computeIfAbsent(0L, (k) -> {
			Curriculum genki  = new Curriculum(k, "Genki");
			Group      lesson = new Group(0, "Lesson 3", genki);
			genki.groups.add(lesson);
			return genki;
		});
	}

	private final List<PartOfSpeech> pos = new ArrayList<>();

	private static PartOfSpeech create(String name) {
		return create(name, null);
	}

	private static int priority = 0; // epic code quality

	private static PartOfSpeech create(String name, String info) {
		return PartOfSpeech.builder().name(name).info(info).priority(priority++).build();
	}

	public void resetGrammar() {
		priority = 0;
		pos.clear();
		String[] parts = { // defaults
						   "noun",
//					"pronoun",
//					"adjective",
						   "adverb",
						   "verb;godan",
						   "verb;ichidan",
						   "verb;irregular",
//					"preposition",
//					"conjunction",
//					"particle",
						   "interjection"
		};
		for (String part : parts) {
			String[] split = part.split(";");
			pos.add(create(split[0], split.length > 1 ? split[1] : null));
		}
	}

	public List<PartOfSpeech> partsOfSpeech() {
		if (pos.isEmpty())
			resetGrammar();

		return pos;
	}

	public List<PartOfSpeech> partsOfSpeechDistinct() { // distinct on PoS name
		return partsOfSpeech().stream()
							  .sorted(Comparator.comparingInt(x -> x.priority))
							  .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PartOfSpeech::toString))),
																	ArrayList::new));
	}

	public List<PartOfSpeech.Info> availableInfos(PartOfSpeech part) {
		List<PartOfSpeech.Info> result = new ArrayList<>();
		for (PartOfSpeech p : pos)
			if (p.name.equalsIgnoreCase(part.name))
				if (p.info != null)
					result.add(p.info);
		return result;
	}

	public Kanji createKanji(String value, Group group) {
		return new Kanji(0, value, group);
	}

	FlashcardPipeline pipeline;

	public void submit(Kanji kanji) {
		if (pipeline == null)
//			pipeline = new FlashcardPipeline("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 8001, "http://localhost:8081/pdfexport");
			pipeline = new FlashcardPipeline("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 8001, "http://127.0.0.1:8081/pdfexport");
		System.out.println("generating " + kanji);

		try {
			File dir = new File("out");
			if (!dir.isDirectory())
				dir.mkdirs();
			if (!dir.isDirectory())
				throw new IOException("failure to create `out` directory");

			File target = new File(dir, "card.pdf");

			Files.copy(new ByteArrayInputStream(pipeline.createFlashcardPDF(kanji)), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Desktop.getDesktop().open(target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("done");
	}

}