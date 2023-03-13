package me.vadim.ja.kc.db.impl.img;

import me.vadim.ja.kc.db.Sqlite3Connector;
import me.vadim.ja.kc.render.factory.DiagramCreator;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * @author vadim
 */
public class ImageCache extends Sqlite3Connector {

	public ImageCache() {
		super("image.cache");
	}

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists IMGDAT(id INT primary key, img BLOB)").execute();
	}

	/**
	 * Cache stroke order diagram.
	 *
	 * @param character  the character which was rendered
	 * @param opts   the {@link DiagramCreator#toBitmask() render options} used to create the image
	 * @param base64 the diagram PNG data encoded in base64
	 */
	public void insertDiagram(String character, int opts, String base64) {
		byte[] img = Base64.getDecoder().decode(base64); // png image data
		long   id  = id(Character.codePointAt(character, 0), opts);
		try {
			PreparedStatement statement = connection.prepareStatement("replace into IMGDAT (id, img) VALUES (?, ?)");
			statement.setLong(1, id);
			statement.setBytes(2, img);
			statement.execute();
		} catch (SQLException x) {
			throw new RuntimeException(x);
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
		long id = id(Character.codePointAt(character, 0), opts);

		try {
			PreparedStatement statement = connection.prepareStatement("select img from IMGDAT where id=?");
			statement.setLong(1, id);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				byte[] img = result.getBytes(1);
				return Base64.getEncoder().encodeToString(img);
			} else return null;
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	private static long id(int codepoint, int bitmask) {
		return (long) codepoint << 32 | (long) bitmask & 0xffffffffL;
	}

	private static int codepoint(long id) {
		return (int) (id >>> 32);
	}

	private static int bitmask(long id) {
		return (int) id;
	}

	public static void main(String[] args) throws Exception {
		ImageCache cache = new ImageCache();
		cache.connect();
		String kanji = "今";

		DiagramCreator creator = new DiagramCreator("D:\\Programming\\Anaconda3\\Scripts\\kanji.exe", 200, true, 5, DiagramCreator.y);
//		cache.insertDiagram(kanji, creator.toBitmask(), creator.strokeOrder(kanji.charAt(0)));

		FileOutputStream fos = new FileOutputStream("image.png");
		fos.write(Base64.getDecoder().decode(cache.queryDiagram(kanji, creator.toBitmask())));
		fos.close();
		cache.disconnect();
	}

}
