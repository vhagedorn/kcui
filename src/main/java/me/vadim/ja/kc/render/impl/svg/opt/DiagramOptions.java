package me.vadim.ja.kc.render.impl.svg.opt;

/**
 * @author vadim
 */
public class DiagramOptions {

	/**
	 * rtl top to bottom
	 */
	public static final String DOWN, Y, y;

	/**
	 * ltr standard mode
	 */
	public static final String ACROSS, X, x;

	static {
		DOWN   = Y = y = "down";
		ACROSS = X = x = "across";
	}

	public final short dpi;
	public final boolean drawFullKanji;
	public final byte wrapAt;
	public final String orientation;

	//smaller primitives to ensure long will pack
	public DiagramOptions(int dpi, boolean drawFullKanji, int wrapAt, String orientation) {
		if(!(x.equals(orientation) || y.equals(orientation)))
			throw new IllegalArgumentException(orientation);

		this.dpi           = (short) dpi;
		this.drawFullKanji = drawFullKanji;
		this.wrapAt        = (byte) wrapAt;
		this.orientation   = orientation;
	}

	//dfk ,orient ,            ,dpi   ,wrap
	//1   ,1      ,1,1,1,1,1,1,16    ,8      = 32
	//bool,0=Y;1=X,           ,short,byte
	public int toBitmask() {
		int mask = 0;

		/*flags*/
		mask |= drawFullKanji ? 1 : 0;
		mask <<= 1;
		mask |= orientation.equals(Y) ? 0 : 1;
		mask <<= 1;

		/*reserved*/
		mask <<= 6 - 1;
		/*configuration*/
		// TIL: implicit widening conversions preserve sign bits
		mask <<= 16;
		mask |= Short.toUnsignedInt(dpi);

		mask <<= 8;
		mask |= Byte.toUnsignedInt(wrapAt);

		return mask;
	}

	public static DiagramOptions fromBitmask(int mask) {
		boolean dfk    = (mask >>> 31) != 0;
		String  orient = (mask << 1 >>> 1 >>> 30) == 0 ? Y : X;
		int     dpi    = (mask << 8 >>> 16) & 0xFFFF; // short
		int     wrap   = (mask << 24 >>> 24) & 0xFF; // byte
		return new DiagramOptions(dpi, dfk, wrap, orient);
	}

	public static int createBitmask(int dpi, boolean drawFullKanji, int wrapAt, String orientation) {
		return new DiagramOptions(dpi, drawFullKanji, wrapAt, orientation).toBitmask();
	}

	public boolean isRTL() {
		return orientation.equals(Y);
	}

}
