package me.vadim.ja.kc.db;

import com.j256.ormlite.dao.Dao;
import me.vadim.ja.kc.db.impl.SQLiteSession;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;
import me.vadim.ja.kc.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;

/**
 * @author vadim
 */
public class ImageCache {

	private static final SessionProvider db = () -> new SQLiteSession(new File("render.cache"));

	/**
	 * Query a cached stroke order diagram.
	 *
	 * @param character the character which was rendered
	 * @param renderOpts the {@link DiagramCreator#toBitmask() render options} used to create the image
	 *
	 * @return the diagram PNG data encoded in base64
	 */
	public @Nullable String queryOne(String character, int renderOpts) {
		try {
			try (DatabaseSession sesh = db.session()) {
				Dao<CachedImage, Long> dao = sesh.getDAO(CachedImage.class, long.class);
				CachedImage cached = dao.queryForId(CachedImage.id(character.codePointAt(0), renderOpts));
				return cached == null ? null : cached.getBase64();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Util.sneaky(e);
			return null; // never happens
		}
	}

	/**
	 * Query cached stroke order diagrams.
	 *
	 * @param characters the characters which were rendered
	 * @param renderOpts the {@link DiagramCreator#toBitmask() render options} used to create the images
	 *
	 * @return an array of diagram PNG data encoded in base64
	 */
	public @Nullable String @NotNull [] queryMultiple(String characters, int renderOpts) {
		try {
			try (DatabaseSession sesh = db.session()) {
				Dao<CachedImage, Long> dao = sesh.getDAO(CachedImage.class, long.class);
				return characters.codePoints().mapToObj(c -> {
					CachedImage cached = null;
					try {
						cached = dao.queryForId(CachedImage.id(c, renderOpts));
					} catch (SQLException e) {
						e.printStackTrace();
						Util.sneaky(e);
					}
					return cached == null ? null : cached.getBase64();
				}).toArray(String[]::new);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Util.sneaky(e);
			return null; // never happens
		}
	}

	/**
	 * Cache a stroke order diagram.
	 *
	 * @param character  the character which was rendered
	 * @param renderOpts the {@link DiagramCreator#toBitmask() render options} used to create the image
	 * @param image      the diagram PNG data encoded in base64
	 */
	public void insertOne(String character, int renderOpts, String image) {
		try {
			try (DatabaseSession sesh = db.session()) {
				Dao<CachedImage, Long> dao    = sesh.getDAO(CachedImage.class, long.class);
				CachedImage            cached = new CachedImage();
				cached.set(character, renderOpts);
				cached.setBase64(image);
				dao.createOrUpdate(cached);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Util.sneaky(e);
		}
	}

//	public static void main(String[] args) throws Exception {
//		ImageCache cache = new ImageCache();
//		String     kanji = "今今";
//
//		DiagramCreator creator = new DiagramCreator("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 200, true, 5, DiagramCreator.y);
//		cache.insertOne(kanji, creator.toBitmask(), creator.strokeOrder(kanji));
//
//		FileOutputStream fos;
//		int i = 0;
//
//		fos = new FileOutputStream("image"+i+++".png");
//		fos.write(Base64.getDecoder().decode(cache.queryOne(kanji, creator.toBitmask())));
//		fos.close();
//
//		for (String s : cache.queryMultiple(kanji, creator.toBitmask())) {
//			fos = new FileOutputStream("image"+i+++".png");
//			fos.write(Base64.getDecoder().decode(s));
//			fos.close();
//		}
//	}

}
