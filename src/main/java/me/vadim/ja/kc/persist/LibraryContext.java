package me.vadim.ja.kc.persist;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.persist.impl.Location;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Curriculum;
import me.vadim.ja.kc.persist.wrapper.Group;
import me.vadim.ja.kc.persist.wrapper.Library;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
public interface LibraryContext {

	Library getActiveLibrary();

	void cacheImgs(Card kanji);

	void submit(Card kanji, int renderOpts);

	void submitAsync(Card kanji, int renderOpts);

	CompletableFuture<PDDocument[]> export(java.util.List<Group> groups, int renderOpts);

	CompletableFuture<BufferedImage[]> generatePreview(Card kanji, int renderOpts);

	BufferedImage[] preview(Card kanji, int renderOpts);

	void save(Card kanji);

	void saveLibrary();

	void delete(HashCode code, Location location);

	void delete(Card kanji);

	void delete(Curriculum curriculum);

	void delete(Group group);

	void shutdown();

}
