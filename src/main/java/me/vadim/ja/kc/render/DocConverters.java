package me.vadim.ja.kc.render;

import com.ruiyun.jvppeteer.util.Helper;
import com.ruiyun.jvppeteer.util.StringUtil;
import com.ruiyun.jvppeteer.util.ValidateUtil;
import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.render.impl.PDFConversionService;
import me.vadim.ja.kc.render.impl.PreviewConversionService;
import me.vadim.ja.kc.render.impl.electron.ElectronPDFConverter;
import me.vadim.ja.kc.render.impl.puppeteer.JvppetteerPDFConverter;
import me.vadim.ja.kc.render.impl.puppeteer.JvppetteerPreviewConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public final class DocConverters {

	/**
	 * <a href="https://github.com/fanyong920/jvppeteer/blob/7870fe58205b81f409d72df2204ba5a44bc411e2/src/main/java/com/ruiyun/jvppeteer/core/page/Page.java#L146">source</a>
	 */
	private static final Map<String, Double> unitToPixels = new HashMap<String, Double>() {
		private static final long serialVersionUID = -4861220887908575532L;

		{
			put("px", 1.00);
			put("in", 96.00);
			put("cm", 37.8);
			put("mm", 3.78);
		}
	};

	/**
	 * <a href="https://github.com/fanyong920/jvppeteer/blob/7870fe58205b81f409d72df2204ba5a44bc411e2/src/main/java/com/ruiyun/jvppeteer/core/page/Page.java#L1770">source</a>
	 */
	public static double unitConvert(String parameter, String targetUnit) {
		if (StringUtil.isEmpty(parameter)) {
			return 0;
		}
		double pixels;
		if (Helper.isNumber(parameter)) {
			pixels = Double.parseDouble(parameter);
		} else if (parameter.endsWith("px") || parameter.endsWith("in") || parameter.endsWith("cm") || parameter.endsWith("mm")) {

			String unit = parameter.substring(parameter.length() - 2).toLowerCase();
			String valueText;
			if (unitToPixels.containsKey(unit)) {
				valueText = parameter.substring(0, parameter.length() - 2);
			} else {
				// In case of unknown unit try to parse the whole parameter as number of pixels.
				// This is consistent with phantom's paperSize behavior.
				unit      = "px";
				valueText = parameter;
			}
			double value = Double.parseDouble(valueText);
			ValidateUtil.assertArg(!Double.isNaN(value), "Failed to parse parameter value: " + parameter);
			pixels = value * unitToPixels.get(unit);
		} else {
			throw new IllegalArgumentException("Margins cannot handle unit: " + parameter);
		}
		return (1.0 / unitToPixels.get(targetUnit)) * pixels;
	}

	private DocConverters() {}

	public static final int PORT = 8225;
	public static final int PORT2 = 8371;

	@SuppressWarnings("StaticNonFinalField")
	public static String printing_css = null;

	public static void putPrintingCss(InMemoryFileServer server){
		if(printing_css == null)
			throw new NullPointerException("printing_css unset");
		server.putResource("/css", new ServerResource("printing.css", printing_css, "text/css"));
	}

	private static final ExecutorService worker = KanjiCardUI.threadPool("HTML conversion worker %d");

	public static PDFConversionService print_electron(String convertURL){
		return new ElectronPDFConverter(PORT, convertURL, worker);
	}

	public static PDFConversionService print_jvppetteer(){
		return new JvppetteerPDFConverter(PORT, worker);
	}

	public static PreviewConversionService preview_jvppetteer(){
		return new JvppetteerPreviewConverter(PORT2, worker);
	}

}
