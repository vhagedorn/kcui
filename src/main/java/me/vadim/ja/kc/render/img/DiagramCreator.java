package me.vadim.ja.kc.render.img;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class DiagramCreator {

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

	public final String  exec;
	public final short   dpi;
	public final boolean drawFullKanji;
	public final byte    wrapAt;
	public final String  orientation;

	//smaller primitives to ensure long will pack
	public DiagramCreator(String exec, int dpi, boolean drawFullKanji, int wrapAt, String orientation) {
		this.exec          = exec;
		this.dpi           = (short) dpi;
		this.drawFullKanji = drawFullKanji;
		this.wrapAt        = (byte) wrapAt;
		this.orientation   = orientation;
	}

	public DiagramCreator withOptions(int bitmask){
		return DiagramCreator.fromBitmask(bitmask, this.exec);
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

	public static DiagramCreator fromBitmask(int mask, String exec) {
		boolean dfk    = (mask >>> 31) != 0;
		String  orient = (mask << 1 >>> 1 >>> 30) == 0 ? Y : X;
		int     dpi    = (mask << 8 >>> 16) & 0xFFFF; // short
		int     wrap   = (mask << 24 >>> 24) & 0xFF; // byte
		return new DiagramCreator(exec, dpi, dfk, wrap, orient);
	}

	public boolean isRTL() {
		return orientation.equals(Y);
	}

	public String strokeOrder(String character) {
		try {
			String[] cmd = {
					exec,
					"--base64", "strokes",
					"--dpi", String.valueOf(dpi),
					"--draw-full-kanji", String.valueOf(drawFullKanji),
					"--num-panels", String.valueOf(wrapAt),
					"--orientation", orientation,
					"0x" + Integer.toHexString(Character.codePointAt(character, 0))
			};

			System.out.println("> " + String.join(" ", cmd));
			Process proc = new ProcessBuilder().redirectErrorStream(true).command(cmd).start();
			proc.getOutputStream().close();

			String base64 = new BufferedReader(new InputStreamReader(proc.getInputStream()))
					.lines().collect(Collectors.joining("\n"));
			proc.getInputStream().close();

			int ret = proc.waitFor();
			if (ret != 0)
				throw new RuntimeException("Problem executing kanjitool. Please make sure it is installed (via `pip3 install kanjitool`).");

			return base64;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
