package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.render.PDFConverters;
import me.vadim.ja.kc.render.factory.FlashcardPipeline;
import me.vadim.ja.kc.render.img.DiagramCreator;

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

	private static void launch(File file){
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ignored){}
	}


	public static final CurriculumManager cringe = new CurriculumManager();

	private CurriculumManager() {}

	public final Map<Long, Curriculum> curriculums = new HashMap<>();

	public Curriculum genki() {
		return curriculums.computeIfAbsent(0L, (k) -> {
			Curriculum genki = new Curriculum("Genki");
			genki.createGroup("Lesson 3");
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
							  .sorted(Comparator.comparingInt(PartOfSpeech::getPriority))
							  .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PartOfSpeech::toString))),
																	ArrayList::new));
	}

	public List<PartOfSpeech.Info> availableInfos(PartOfSpeech part) {
		List<PartOfSpeech.Info> result = new ArrayList<>();
		for (PartOfSpeech p : pos)
			if (p.name.equalsIgnoreCase(part.name))
				if (p.hasInfo())
					result.add(p.info);
		return result;
	}

	public Kanji createKanji(String value, Group group) {
		return new Kanji(value, group);
	}

	FlashcardPipeline pipeline;

	private void lazy() {
		if (pipeline == null)
			pipeline = new FlashcardPipeline(new DiagramCreator("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 200, true, 5, DiagramCreator.DOWN),
//											 PDFConverters.electron("http://127.0.0.1:8081/pdfexport")
											 PDFConverters.jvppetteer()
			);
	}

	public void cacheImgs(Kanji kanji) {
		if (kanji.value.isEmpty() || kanji.value.isBlank()) // ignore excess events
			return;
		lazy();
		System.out.println("Caching stroke order diagrams for partial kanji " + kanji.value);
		pipeline.cacheStrokeDiagrams(kanji);
	}

	public void submit(Kanji kanji, int renderOpts) {
		lazy();
		System.out.println("generating " + kanji.toPreviewString());

		try {
			File dir = new File("out");
			if (!dir.isDirectory())
				dir.mkdirs();
			if (!dir.isDirectory())
				throw new IOException("failure to create `out` directory");

			File target = new File(dir, "card.pdf");

			Files.copy(new ByteArrayInputStream(pipeline.createFlashcardPDF(kanji, renderOpts)), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Desktop.getDesktop().open(target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("done");
	}

	public void submitAsync(Kanji kanji, int renderOpts) {
		lazy();

		pipeline.queueFlashcardGeneration(kanji, new File("out", "card.pdf"), renderOpts).thenAccept(CurriculumManager::launch);
	}

	public void save(Kanji kanji) {
		System.out.println("TODO");
//		pipeline.queueFlashcardGeneration(kanji, new File("out", "card.pdf")).thenAccept(CurriculumManager::launch);
	}

}