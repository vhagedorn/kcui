package me.vadim.ja.kc.db.impl.blob;

import me.vadim.ja.kc.db.Identifiable;
import me.vadim.ja.kc.render.impl.img.DiagramCreator;

import java.util.Base64;

/**
 * @author vadim
 */
@Deprecated
class CachedImage {

	public Key key;
	public Value value;

	CachedImage(String character, int renderOpts, String base64) {
		this.key   = new Key(character, renderOpts);
		this.value = new Value(base64);
	}

	CachedImage(Key key, Value value) {
		this.key   = key;
		this.value = value;
	}

	public static Key key(String character, int renderOpts) {
		return new Key(character, renderOpts);
	}

	public static Key key(String character, DiagramCreator diag) {
		return new Key(character, diag.toBitmask());
	}

	public static Value value(String base64) {
		return new Value(base64);
	}

	public static Value value(byte[] toEncode) {
		return new Value(Base64.getEncoder().encodeToString(toEncode));
	}

	public static class Key implements Identifiable {

		public final String character;
		public final int renderOpts;

		private Key(String character, int renderOpts) {
			this.character  = character;
			this.renderOpts = renderOpts;
		}

		@Override
		public long id() {
			return (long) Character.codePointAt(character, 0) << 32 | (long) renderOpts & 0xffffffffL;
		}

		private static int codepoint(long id) {
			return (int) (id >>> 32);
		}

		private static int bitmask(long id) {
			return (int) id;
		}

	}

	public static class Value {

		public String base64;

		private Value(String base64) {
			this.base64 = base64;
		}

		public byte[] decode() {
			return Base64.getDecoder().decode(base64); // png image data
		}

	}

}
