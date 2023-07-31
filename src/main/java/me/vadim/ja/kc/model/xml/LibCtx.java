package me.vadim.ja.kc.model.xml;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.model.LibraryContext;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;
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
class LibCtx implements LibraryContext {

	public static final String exec = "D:\\Programming\\Anaconda3\\Scripts\\kanji.exe";
	public static final File libfile = new File(JAXBStorage.prefDir, "lib.xml");
	private final Library library;

	LibCtx() {
		Library lib = JAXBStorage.readLib(libfile);
		if (lib == null) {
			lib = new ImplLibrary();
			System.out.println("> Created new library.");
		}
		this.library = lib;
		System.out.println("> Using " + lib);
		lib.prune();
	}

	@Override
	public Library getActiveLibrary() {
		return library;
	}

	public static void launch(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ignored) { }
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

	@Override
	public void submitAsync(Card kanji, int renderOpts) {
		lazy();

		pipeline.queueFlashcardGeneration(kanji, new File("out", "card.pdf"), renderOpts).thenAccept(LibCtx::launch);
	}

	private final ExecutorService worker = KanjiCardUI.singleThread("Batch Exporter Service");

	private static final void update(Runnable update) {

	}

	@Override
	public CompletableFuture<PDDocument[]> export(List<Card> cards, int renderOpts, Runnable update) {
		class updater {

			final Runnable update;

			updater(Runnable update) {
				this.update = update;
			}

			<T> CompletableFuture<T> update(CompletableFuture<T> future) {
				return future.thenApply(it -> {
					update.run(); // I guess this is only called twice!!!!!!!!!
					return it;
				});
			}

		}
		lazy();
		updater updater = new updater(update);
		List<CompletableFuture<PDDocument>> tasks = cards.stream()
														 .map(it -> CompletableFuture.supplyAsync(() -> pipeline.createFlashcardPDF(it, renderOpts), worker))
														 .map(updater::update)
														 .map(it -> it.thenApply(bytes -> {
															 try {
																 return PDDocument.load(bytes);
															 } catch (IOException e) {
																 throw new RuntimeException(e);
															 }
														 }))
														 .map(updater::update)
														 .collect(Collectors.toList());
		return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
								.thenApplyAsync(x -> {
									updater.update.run();
									List<PDDocument> pdfs = tasks.stream().map(CompletableFuture::join).collect(Collectors.toList());
									PDDocument[] result = {
											PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 0),
											PDFUtil.mergePDFs(pdfs.toArray(PDDocument[]::new), 1),
											};
									PDFUtil.closeSafely(pdfs.toArray(PDDocument[]::new));
									updater.update.run();
									return result;
								}, worker)
								.exceptionally(x -> {
									System.err.println("Problem when exporting:");
									x.printStackTrace();
									return null;
								});
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
		System.out.println("Saving " + kanji.toPreviewString() + " to " + JAXBStorage.card2file(kanji));
		JAXBStorage.dumpCard(kanji);
		JAXBStorage.dumpLib(library, libfile);
	}

	@Override
	public void saveLibrary(boolean quiet) {
		if (!quiet)
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
		library.unlinkCard(kanji);
	}

	@Override
	public void delete(Curriculum curriculum) {
		library.unlinkCurriculum(curriculum);
	}

	@Override
	public void delete(Group group) {
		group.getCurriculum().unlinkGroup(group);
	}

	@Override
	public void shutdown() {
		if (pipeline != null)
			pipeline.close();
	}

}
