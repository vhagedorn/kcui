package me.vadim.ja.kc.render;

/**
 * @author vadim
 */
public class PrintOptions {

	//this will never be cleaned up :)

	public static PrintOptions letter(){
		return new PrintOptions(new PageSize() {
			@Override
			public String name() {
				return "Letter";
			}

			@Override
			public float width() {
				return 8.5f;
			}

			@Override
			public float height() {
				return 11f;
			}
		}, new Margins("in", 1, 1, 1, 1), false, true);
	}

	public static PrintOptions index(){
		return new PrintOptions(new PageSize() {
			@Override
			public String name() {
				return "Index Card";
			}

			@Override
			public float width() {
				return 4f;
			}

			@Override
			public float height() {
				return 6f;
			}
		}, new Margins("in", .25f, .25f, .25f, .25f), false, false);
	}

	private final PageSize size;
	private final Margins margins;
	private final boolean  landscape;
	private final boolean  printHeadersAndFooters;

	public PrintOptions(PageSize size, Margins margins, boolean landscape, boolean printHeadersAndFooters) {
		this.size      = size;
		this.margins   = margins;
		this.landscape = landscape;
		this.printHeadersAndFooters = printHeadersAndFooters;
	}

	public PageSize getSize() {
		return size;
	}

	public Margins getMargins() {
		return margins;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public boolean printHeadersAndFooters() {
		return printHeadersAndFooters;
	}

}
