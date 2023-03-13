package me.vadim.ja.kc.render;

/**
 * @author vadim
 */
public class PrintOptions {

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
		}, new Margins(1, 1, 1, 1), false);
	}

	private final PageSize size;
	private final Margins margins;
	private final boolean  landscape;

	public PrintOptions(PageSize size, Margins margins, boolean landscape) {
		this.size      = size;
		this.margins   = margins;
		this.landscape = landscape;
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

}
