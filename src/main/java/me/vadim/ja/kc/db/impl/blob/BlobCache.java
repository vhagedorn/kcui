package me.vadim.ja.kc.db.impl.blob;

import me.vadim.ja.kc.db.DbMultimap;
import me.vadim.ja.kc.db.impl.Sqlite3Database;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * @author vadim
 */
@SuppressWarnings("UnusedReturnValue")
public final class BlobCache extends Sqlite3Database {

	private final DbMultimap<CachedImage.Key, CachedImage.Value> images;
	private final Object writeLock = new Object();

	public BlobCache() {
		super("render.cache");
		images = new CIMultimap();
	}


	@Override
	protected void onConnect() throws SQLException {
		images.initialize(connection);
	}

	/**
	 * Cache stroke order diagram. This method is synchronized on an internal write lock.
	 *
	 * @param character  the character which was rendered
	 * @param opts   the {@link DiagramCreator#toBitmask() render options} used to create the image
	 * @param base64 the diagram PNG data encoded in base64
	 */
	public void insertDiagram(String character, int opts, String base64) {
		synchronized (writeLock) {
			images.put(CachedImage.key(character, opts), List.of(CachedImage.value(base64)));
		}
	}

	/**
	 * Query a cached stroke order diagram.
	 *
	 * @param character the character which was rendered
	 * @param opts  the {@link DiagramCreator#toBitmask() render options} used to create the image
	 *
	 * @return the diagram PNG data encoded in base64
	 */
	@Nullable
	public String queryDiagram(String character, int opts) {
		return images.get(CachedImage.key(character, opts)).stream().findFirst().map(x -> x.base64).orElse(null);
	}

	public static void main(String[] args) throws Exception {
		BlobCache cache = new BlobCache();
		cache.connect();
//		String kanji = "今";
//
//		DiagramCreator creator = new DiagramCreator("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 200, true, 5, DiagramCreator.y);
////		cache.insertDiagram(kanji, creator.toBitmask(), creator.strokeOrder(kanji.charAt(0)));
//
//		FileOutputStream fos = new FileOutputStream("image.png");
//		fos.write(Base64.getDecoder().decode(cache.queryDiagram(kanji, creator.toBitmask())));
//		fos.close();
		cache.disconnect();
	}

}
