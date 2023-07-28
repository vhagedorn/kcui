package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.db.impl.lib.KanjiLibrary;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.impl.factory.FlashcardPipeline;
import me.vadim.ja.kc.render.impl.factory.PDFUtil;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import me.vadim.ja.swing.NaturalOrderComparator;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//this never got cleaned up :(

/**
 * @author vadim
 */
public class CurriculumManager {

	private static void launch(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ignored) {}
	}


	public static final CurriculumManager cringe = new CurriculumManager();

	private CurriculumManager() {}

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
//						   "pronoun",
//						   "adjective",
						   "adverb",
						   "verb;godan",
						   "verb;ichidan",
						   "verb;irregular",
						   "verb;auxiliary",
						   // this will do for now
						   "verb (tr);godan",
						   "verb (tr);ichidan",
						   "verb (tr);irregular",
						   "verb (tr);auxiliary",
//						   "preposition",
//						   "conjunction",
//						   "particle",
						   "interjection"
		};
		for (String part : parts) {
			String[] split = part.split(";");
			pos.add(create(split[0], split.length > 1 ? split[1] : null));
		}
//		pos.clear();
//		temp.loadGrammar().thenAccept(pos::addAll);
		temp.saveGrammar(pos);
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
		if(part == null) return Collections.emptyList();
		List<PartOfSpeech.Info> result = new ArrayList<>();
		for (PartOfSpeech p : pos)
			if (p.name.equalsIgnoreCase(part.name))
				if (p.hasInfo())
					result.add(p.info);
		return result;
	}

	public List<Kanji> tempGenkiL4_L10() {
		if (!temp.alive())
			temp.start();
		List<Kanji> result = new ArrayList<>(25);
		Curriculum  genki  = temp.loadCurriculums().join().get(0);
		if (genki == null)
			throw new NullPointerException("genki");
		for (int i = 4; i <= 10; i++) {
			Group l = genki.createGroup("Lesson " + i);

			int the = (int) (Math.random() * 10);
			for (int j = the; j < the + 10; j++)
				 result.add(new Kanji("Test " + i + "." + j, l));
		}

		result.sort(Comparator.comparing(x -> x.group.name, new NaturalOrderComparator()));

		return result;
	}

	FlashcardPipeline pipeline;

	private void lazy() {
		if (pipeline == null)
			pipeline = new FlashcardPipeline(new DiagramCreator("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 200, true, 5, DiagramCreator.DOWN),
//											 DocConverters.print_electron("http://127.0.0.1:8081/pdfexport"),
											 DocConverters.print_jvppetteer(),
											 DocConverters.preview_jvppetteer()
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

	private final ExecutorService worker = KanjiCardUI.singleThread("Batch Exporter Service");
	public CompletableFuture<PDDocument[]> export(List<Group> groups, int renderOpts) {
		lazy();
		List<Kanji> toRender = allKanji.stream().filter(it -> groups.contains(it.group)).collect(Collectors.toList());
		List<CompletableFuture<PDDocument>> tasks = toRender.stream()
															.map(it -> CompletableFuture.supplyAsync(() -> pipeline.createFlashcardPDF(it, renderOpts), worker))
															.map(it -> it.thenApply(bytes -> {
																try {
																	return PDDocument.load(bytes);
																} catch (IOException e) {
																	throw new RuntimeException(e);
																}
															})).collect(Collectors.toList());
		return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
								.thenApplyAsync(x -> {
									List<PDDocument> pdfs = tasks.stream().map(CompletableFuture::join).collect(Collectors.toList());
									PDDocument[] result =
											Stream.of(PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 0),
													  PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 1))
												  .map(PDFUtil::export)
												  .map(bytes -> {
													  try {
														  return PDDocument.load(bytes);
													  } catch (IOException e) {
														  throw new RuntimeException(e);
													  }
												  }).toArray(PDDocument[]::new);
									// COSDocument or PDDocument stores references to imported pages
									// preventing me from closing the `pdfs` list
									// Exporting and then importing them should resolve this issue.
									PDFUtil.closeSafely(pdfs.toArray(PDDocument[]::new));
									return result;
								}, worker);
	}

	public CompletableFuture<BufferedImage[]> generatePreview(Kanji kanji, int renderOpts) {
		lazy();

		return pipeline.queueFlashcardPreview(kanji, renderOpts);
	}

	public BufferedImage[] preview(Kanji kanji, int renderOpts) {
		lazy();

		return pipeline.createFlashcardPreview(kanji, renderOpts);
	}


	final AppRegistry temp = new AppRegistry(new KanjiLibrary());

	public final List<Curriculum> curriculums = new ArrayList<>();

	public Curriculum createCurriculum(String name) {
		Curriculum curriculum = new Curriculum(name);
		temp.saveCurriculum(curriculum).join(); // block here, since this is created from a modal and it needs the ID immediately
		return curriculum;
	}

	public Group createGroup(Curriculum curriculum, String name){
		Group group = curriculum.createGroup(name);
		temp.saveCurriculum(curriculum).join();
		return group;
	}

	private final List<Kanji> allKanji = new ArrayList<>(2136);

	public List<Kanji> allKanjiIn(Curriculum curriculum) {
		if (!curriculum.hasId())
			throw new IllegalArgumentException("id not set");
		return allKanji.stream().filter(k -> k.curriculum.id() == curriculum.id()).collect(Collectors.toList());
	}

	public Kanji createKanji(String value, Group group) {
		Kanji kanji = new Kanji(value, group);
		allKanji.add(kanji);
		group.ensureInCurriculum();
		if(!group.hasId()) // ensure the group has an ID
			temp.saveCurriculum(group.curriculum).thenAccept(x -> save(kanji));
		else
			save(kanji);
		return kanji;
	}

	public void save(Kanji kanji) {
		if (!temp.alive())
			temp.start();
		//refresh status in the cache
		allKanji.remove(kanji);
		if(kanji.hasId())
			List.copyOf(allKanji).stream().filter(k -> k.hasId() && k.id() == kanji.id()).forEach(allKanji::remove);
		allKanji.add(kanji);
		//save to db
		System.out.println("Saving " + kanji.toPreviewString());
		temp.saveKanji(kanji);
	}

	public void saveAllKanji(){
		allKanji.forEach(temp::saveKanji);
	}

	public void saveCurriculums() {
		curriculums.forEach(temp::saveCurriculum);
	}

	public void saveCurriculum(Curriculum curriculum) {
		temp.saveCurriculum(curriculum);
	}

	public void delete(Kanji kanji) {
		allKanji.remove(kanji);
		temp.deleteKanji(kanji);
	}

	public void delete(Curriculum curriculum){
		curriculums.remove(curriculum);
		temp.deleteCurriculum(curriculum);
	}

	public void delete(Group group) {
		group.curriculum.groups.remove(group);
		temp.saveCurriculum(group.curriculum).join();
	}

	public void shutdown(){
		if(!temp.alive())
			temp.start();
		saveAllKanji();
		saveCurriculums();
		if(pipeline != null)
			pipeline.close();
		temp.cease();
	}

	{
		if (!temp.alive())
			temp.start();

		resetGrammar();
		curriculums.clear();
		temp.loadCurriculums().thenAccept(curriculums::addAll);
		allKanji.clear();
		temp.loadKanji().thenApply(allKanji::addAll);
	}

}