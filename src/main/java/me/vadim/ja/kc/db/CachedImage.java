package me.vadim.ja.kc.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.nio.charset.StandardCharsets;

/**
 * @author vadim
 */
@DatabaseTable(tableName = "IMGDAT")
public class CachedImage {

	@DatabaseField(id = true)
	private long id;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] img;

	public CachedImage() { } // ORMLite

	public void set(String value, int renderOpts) {
		id = id(Character.codePointAt(value, 0), renderOpts);
	}

	public String getValue() {
		return Character.toString(codepoint(id));
	}

	public int getRenderOpts() {
		return bitmask(id);
	}

	public String getBase64() {
		return new String(img, StandardCharsets.UTF_8);
	}

	public void setBase64(String base64) {
		img = base64.getBytes(StandardCharsets.UTF_8);
	}

	private static int codepoint(long id) {
		return (int) (id >>> 32);
	}

	private static int bitmask(long id) {
		return (int) id;
	}

	public static long id(int codepoint, int bitmask) {
		return (long) codepoint << 32 | (long) bitmask & 0xffffffffL;
	}

}
