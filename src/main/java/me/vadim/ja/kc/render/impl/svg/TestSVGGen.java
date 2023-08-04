package me.vadim.ja.kc.render.impl.svg;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.model.LibraryContext;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.xml.LibCtx;
import me.vadim.ja.kc.render.impl.ctx.ExportJob;
import me.vadim.ja.kc.render.impl.svg.opt.CollageOptions;
import me.vadim.ja.kc.render.impl.svg.opt.DiagramOptions;
import me.vadim.ja.kc.render.impl.svg.opt.Rendering;
import me.vadim.ja.kc.render.impl.svg.opt.StrokesOptions;
import me.vadim.ja.kc.util.Util;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestSVGGen {

	public static final String svgNS = "http://www.w3.org/2000/svg";


	public void paint(Graphics2D g2d) {
		g2d.setPaint(Color.red);
		g2d.fill(new Rectangle(10, 10, 100, 100));
	}

	public static void main0(String[] args) throws IOException {

		// Get a DOMImplementation.
		DOMImplementation domImpl =
				GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String   svgNS    = "http://www.w3.org/2000/svg";
		Document document = domImpl.createDocument(svgNS, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		// Ask the test to render into the SVG Graphics2D implementation.
		TestSVGGen test = new TestSVGGen();
		test.paint(svgGenerator);

		// Finally, stream out SVG to the standard output using
		// UTF-8 encoding.
		boolean useCSS = true; // we want to use CSS style attributes
		Writer  out    = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
		svgGenerator.stream(out, useCSS);
	}

//	public static void main1(String[] args) throws Exception {
//		URI uri = new File("input.svg").toURI();
//
//		SVGMetaPost converter = new SVGMetaPost(uri.toString());
//
//		List<MetaPostPath> list = converter.convert();
//		for (MetaPostPath path : list) {
//			System.out.println(path.toCode());
//		}
//	}
//
//	public static void main2(String[] args) throws Exception {
//
//		// Get a DOMImplementation.
//		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
//
//		// Create an instance of org.w3c.dom.Document.
////		SVGDocument document = new SVGOMDocument(null, domImpl);
//		SVGDocument document = (SVGDocument) domImpl.createDocument(svgNS, "svg", null);
//
//		URI uri = new File("input.svg").toURI();
//
//		SVGMetaPost converter = new SVGMetaPost(uri.toString());
//
//		NodeIterable<SVGPathElement> list = new NodeIterable<>(converter.getPathElements());
//		for (SVGPathElement path : list) {
////			appendPath(document, path.getAttribute("d"));
//			SVGPoint pt = path.getPointAtLength(0f);
//			System.out.println(pt);
////			appendPoint(document, pt.getX(), pt.getY());
//		}
//
//		String style = converter.getSVGDocument().getDocumentElement().getElementsByTagName("g").item(0).getAttributes().getNamedItem("style").getTextContent();
//		document.getRootElement().setAttribute("style", style);
//
//		Transcoder            xcode = new SVGTranscoder();
//		ByteArrayOutputStream baos  = new ByteArrayOutputStream();
//
//		TranscoderInput  i = new TranscoderInput(document);
//		TranscoderOutput o = new TranscoderOutput(new OutputStreamWriter(baos));
//
//		xcode.transcode(i, o);
//		StringSelection selection = new StringSelection(baos.toString());
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		clipboard.setContents(selection, selection);
//	}

	public static void main3(String[] args) throws Exception {
		File i = new File("input.svg");
		File o = new File("out");

		o.mkdirs();

		StrokePlotter   plotter = StrokePlotter.read(i);
		BufferedImage[] diag    = plotter.renderDiagrams(Rendering.default_sopt);
		for (int j = 0; j < diag.length; j++)
			 ImageIO.write(diag[j], "png", new File(o, j + ".png"));
		Util.launch(o);
	}

	public static void main4(String[] args) throws Exception {
		File i = new File("input.svg");
		File o = new File("out.png");

		StrokesOptions sopt = new StrokesOptions(Color.black, new Color(181, 181, 181), Color.red, 3, true);
		DiagramOptions diag = new DiagramOptions(200, true, 5, DiagramOptions.DOWN);
		CollageOptions opts = new CollageOptions(.01f, .02f, new Color(96, 57, 19), 3f, null, new Color(234, 174, 106), 1f / 2f, new float[] { 5f });

		StrokePlotter plotter = StrokePlotter.read(i);
		BufferedImage out     = plotter.renderCombined(sopt, diag, opts);
		ImageIO.write(out, "png", o);
		Util.launch(o);
	}

	public static void main5(String[] args) throws Exception {
		System.setProperty("kvg_dir", "D:\\Programming\\JupyterNotebook\\kanjivg\\kanji");
		StrokePlotter plotter = StrokePlotter.kvg('漢');

		File png = new File("output.png");

		BufferedImage out = Rendering.render_default(plotter);
		ImageIO.write(out, "png", png);
		Util.launch(png);
	}

	public static void main6(String[] args) throws Exception {
		System.setProperty("kvg_dir", "D:\\Programming\\JupyterNotebook\\kanjivg\\kanji");
		StrokePlotter plotter = StrokePlotter.kvg('漢');

		BufferedImage out = Rendering.render_default(plotter);

		String encoded = "data:image/png;base64," + Util.imgToBase64String(out, "png");

		StringSelection selection = new StringSelection(encoded);
		Clipboard       clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public static void main7(String[] args) throws Exception {
		System.setProperty("kvg_dir", "D:\\Programming\\JupyterNotebook\\kanjivg\\kanji");

		int amt = 1000;

		long sum = 0;//Math.round(1965964960.60D*1000L);
		for (int k = 0; k < amt; k++) {
			if (true) continue;
			long   nanos = System.nanoTime();
			String kanji = "水曜日木曜日子供撮る食べる毎日水曜日木曜日子供撮る食べる毎日";

			int i = 0;
			for (int codepoint : kanji.codePoints().toArray()) {
				StrokePlotter plotter = StrokePlotter.kvg(codepoint);
				Rendering.render_default(plotter);
				i++;
			}
			long diff = System.nanoTime() - nanos;
			System.out.printf("Rendered %d diagrams in %dms.%n", i, TimeUnit.NANOSECONDS.toMillis(diff));

			sum += diff;
		}

		double avg = (double) sum / (double) amt;
		System.out.printf("Average is %dms.%n", TimeUnit.NANOSECONDS.toMillis(Math.round(avg)));
	}

	public static void main8(String[] args) throws Exception {
		new KanjiCardUI();
		LibraryContext ctx = new LibCtx();

		ExportJob export = ctx.getRenderContext().createExport(ctx.getActiveLibrary().getCards().toArray(Card[]::new))
							  .orderByGroups()
							  .splitFrontAndBack(false);

		AtomicInteger atomic = new AtomicInteger(0);
		int expect = export.getExpectedUpdatePollCount();
		export.sendProgressUpdates(() -> {
			System.out.println("Update "+atomic.incrementAndGet()+"/"+expect);
		});

		System.out.println("Begin export...");
		export.getResult().join();
		System.out.println("Done.");

		ctx.shutdown();
	}

}