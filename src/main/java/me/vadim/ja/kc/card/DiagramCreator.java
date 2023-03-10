package me.vadim.ja.kc.card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class DiagramCreator {

	//rtl top to bottom
	public static final String DOWN = "down";
	public static final String Y    = DOWN;

	//ltr standard mode
	public static final String ACROSS = "across";
	public static final String X      = ACROSS;

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

	//dfk ,orient ,            ,dpi   ,wrap
	//1   ,1      ,1,1,1,1,1,1,16    ,8      = 32
	//bool,0=Y;1=X,           ,short,byte
	public int toBitmask() {
		int mask = 0;

		/*flags*/
		mask |= drawFullKanji ? 1 : 0;
		mask <<= 1;
		mask |= orientation.equals(Y) ? 0 : 1;

		/*reserved*/
		mask <<= 6;
		/*configuration*/
		mask <<= 16;
		mask |= dpi;

		mask <<= 8;
		mask |= wrapAt;

		return mask;
	}

	public static DiagramCreator fromBitmask(int mask) {
//		boolean dfk = (mask & 0b10_000000_0000000000000000_00000000) != 0;
		boolean dfk = (mask >>> 31) != 0;
		String orient = (mask << 1 >>> 1 >>> 30) == 0 ? Y : X;
		short dpi = (short) (mask << 8 >>> 16);
		byte wrap = (byte) (mask << 24 >>> 24);

		System.out.println("dfk:"+dfk);
		System.out.println("o:"+orient);
		System.out.println("dpi:"+dpi);
		System.out.println("wrap:"+wrap);
		return null;
	}

	public static void main(String[] args) {
		DiagramCreator xd = new DiagramCreator(null, 596, false, 255, X);
		System.out.println("dfk:"+xd.drawFullKanji);
		System.out.println("o:"+xd.orientation);
		System.out.println("dpi:"+xd.dpi);
		System.out.println("wrap:"+xd.wrapAt);
		int bitmask = xd.toBitmask();
		System.out.println("mask="+bitmask);
		DiagramCreator.fromBitmask(bitmask);
	}

	public boolean isRTL() {
		return orientation.equals(Y);
	}

	public String strokeOrder(char kanji) {
		try {
			String[] cmd = {
					exec,
					"--base64", "strokes",
					"--dpi", String.valueOf(dpi),
					"--draw-full-kanji", String.valueOf(drawFullKanji),
					"--num-panels", String.valueOf(wrapAt),
					"--orientation", orientation,
					"0x" + Integer.toHexString(kanji)
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
