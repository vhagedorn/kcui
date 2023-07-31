package me.vadim.ja.kc.model;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;
import me.vadim.ja.kc.model.xml.Location;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public interface LibraryContext {

	Library getActiveLibrary();

	void cacheImgs(Card kanji);

	void submit(Card kanji, int renderOpts);

	void submitAsync(Card kanji, int renderOpts);

	// update will be triggered exactly 4 times
	CompletableFuture<PDDocument[]> export(List<Card> cards, int renderOpts, Runnable update);

	CompletableFuture<BufferedImage[]> generatePreview(Card kanji, int renderOpts);

	BufferedImage[] preview(Card kanji, int renderOpts);

	void save(Card kanji);

	void saveLibrary(boolean quiet);

	void delete(HashCode code, Location location);

	void delete(Card kanji);

	void delete(Curriculum curriculum);

	void delete(Group group);

	void shutdown();

}
