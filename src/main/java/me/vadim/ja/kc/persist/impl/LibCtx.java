package me.vadim.ja.kc.persist.impl;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.persist.LibraryContext;
import me.vadim.ja.kc.persist.io.JAXBStorage;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.impl.factory.FlashcardPipeline;
import me.vadim.ja.kc.render.impl.factory.PDFUtil;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class LibCtx implements LibraryContext {

	public static final String exec = "D:\\Programming\\Anaconda3\\Scripts\\kanji.exe";
	public static final File libfile = new File(JAXBStorage.prefDir, "lib.xml");
	private final Library library;

	public LibCtx() {
		Library lib = JAXBStorage.readLib(libfile);
		if(lib == null)
			lib = new Lib();
		this.library = lib;
	}

	@Override
	public Library getActiveLibrary() {
		return library;
	}

	public static void launch(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ignored) {}
	}

	FlashcardPipeline pipeline;

	private void lazy() {
		if (pipeline == null)
			pipeline = new FlashcardPipeline(new DiagramCreator(exec, 200, true, 5, DiagramCreator.DOWN),
//											 PDFConverters.electron("http://127.0.0.1:8081/pdfexport")
											 DocConverters.print_jvppetteer(),
											 DocConverters.preview_jvppetteer()
			);
	}

	@Override
	public void cacheImgs(Card kanji) {
		String value = kanji.describeJapanese();
		if (value.isEmpty() || value.isBlank()) // ignore excess events
			return;
		lazy();
		System.out.println("Caching stroke order diagrams for partial kanji " + value);
		pipeline.cacheStrokeDiagrams(kanji);
	}

	@Override
	public void submit(Card kanji, int renderOpts) {
		lazy();
		System.out.println("generating " + kanji.toString());

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

	@Override
	public void submitAsync(Card kanji, int renderOpts) {
		lazy();

		pipeline.queueFlashcardGeneration(kanji, new File("out", "card.pdf"), renderOpts).thenAccept(LibCtx::launch);
	}

	private final ExecutorService worker = KanjiCardUI.singleThread("Batch Exporter Service");

	@Override
	public CompletableFuture<PDDocument[]> export(List<Group> groups, int renderOpts) {
		lazy();
		List<Card> toRender = groups.stream().flatMap(it -> library.getCards(it).stream()).collect(Collectors.toList());
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
									PDDocument[] result = {
											PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 0),
											PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 1),
											};
									PDFUtil.closeSafely(pdfs.toArray(PDDocument[]::new));
									return result;
								}, worker);
	}

	@Override
	public CompletableFuture<BufferedImage[]> generatePreview(Card kanji, int renderOpts) {
		lazy();

		return pipeline.queueFlashcardPreview(kanji, renderOpts);
	}

	@Override
	public BufferedImage[] preview(Card kanji, int renderOpts) {
		lazy();

		return pipeline.createFlashcardPreview(kanji, renderOpts);
	}

	@Override
	public void save(Card kanji) {
		System.out.println("Saving " + kanji + " to " + JAXBStorage.card2file(kanji));
		JAXBStorage.dumpCard(kanji);
		JAXBStorage.dumpLib(library, libfile);
	}

	@Override
	public void saveLibrary() {
		System.out.println("Saving " + library + " to " + libfile);
		JAXBStorage.dumpLib(library, libfile);
	}

	@Override
	public void delete(HashCode code, Location location) {
		JAXBStorage.card2file(location, code).delete();
	}

	@Override
	public void delete(Card kanji) {
		JAXBStorage.card2file(kanji).delete();
	}

	@Override
	public void delete(Curriculum curriculum){
		library.unlinkCurriculum(curriculum);
	}

	@Override
	public void delete(Group group) {
		group.getCurriculum().unlinkGroup(group);
	}

	@Override
	public void shutdown(){
		if(pipeline != null)
			pipeline.close();
	}

}
