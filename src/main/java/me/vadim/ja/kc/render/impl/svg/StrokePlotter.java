package me.vadim.ja.kc.render.impl.svg;

import me.vadim.ja.kc.render.impl.svg.opt.CollageOptions;
import me.vadim.ja.kc.render.impl.svg.opt.DiagramOptions;
import me.vadim.ja.kc.render.impl.svg.opt.StrokesOptions;
import me.vadim.ja.kc.util.Util;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPathElement;
import org.w3c.dom.svg.SVGPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

/**
 * @author vadim
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public class StrokePlotter {

	public static final String KVG_DIR = "kvg_dir";

	private static final String svgNS = "http://www.w3.org/2000/svg";
	public static final String x = "x";
	public static final String y = "y";

	private static class Count {

		final int x, y;

		Count(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	private static String col2hex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private static void appendPath(Document document, SVGPathElement path, Color color) {
		Element element = document.createElementNS(svgNS, "path");
		element.setAttribute("d", path.getAttribute("d"));
		if (color != null)
			element.setAttribute("stroke", col2hex(color));
		document.getDocumentElement().appendChild(element);
	}

	private static void appendPoint(Document document, float x, float y, float r, Color color) {
		Element element = document.createElementNS(svgNS, "circle");
		element.setAttribute("cx", String.valueOf(x));
		element.setAttribute("cy", String.valueOf(y));
		element.setAttribute("r", String.valueOf(r));
		element.setAttribute("stroke", col2hex(color));
		element.setAttribute("fill", col2hex(color));
		document.getDocumentElement().appendChild(element);
	}

	private static void drawGrid(Graphics2D g2d, Dimension dim, String axis, Count ct, float es, float ee, Color color, float thickness, float[] dash, int step) {
		step = Math.max(step, 1);
		int w = dim.width, h = dim.height;

		int r = -1;
		if (x.equals(axis))
			r = ct.x;
		if (y.equals(axis))
			r = ct.y;
		if (r == -1)
			throw new IllegalArgumentException(axis);

		for (int i = 0; i < r * step + 1; i++) {
			float c = i;
			if (i == 0)
				c += es;
			if (i == r)
				c -= ee;

			int x1, y1, x2, y2;
			x1 = y1 = x2 = y2 = -1;
			if (x.equals(axis)) {
				x1 = x2 = Math.round(c * w / step);
				y1 = 0;
				y2 = Math.round(ct.y * h);
			}
			if (y.equals(axis)) {
				y1 = y2 = Math.round(c * h / step);
				x1 = 0;
				x2 = Math.round(ct.x * w);
			}

			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
			g2d.drawLine(x1, y1, x2, y2);
		}
	}

	public static StrokePlotter read(File file) {
		try {
			String             parser   = XMLResourceDescriptor.getXMLParserClassName();
			SVGDocumentFactory factory  = new SAXSVGDocumentFactory(parser);
			SVGDocument        document = factory.createSVGDocument(file.toURI().toString());

			UserAgent      userAgent     = new UserAgentAdapter();
			DocumentLoader loader        = new DocumentLoader(userAgent);
			BridgeContext  bridgeContext = new BridgeContext(userAgent, loader);
			bridgeContext.setDynamicState(BridgeContext.DYNAMIC);

			// Enable CSS- and SVG-specific enhancements.
			(new GVTBuilder()).build(bridgeContext, document);

			return new StrokePlotter(document);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static StrokePlotter kvg(int codepoint) {
		String hex = String.format("%5s", Integer.toHexString(codepoint)).replace(' ', '0');
		String kvg = System.getProperty(KVG_DIR, null);
		if (kvg == null)
			throw new UnsupportedOperationException(KVG_DIR+" not found");
		return read(new File(kvg, hex + ".svg"));
	}

	private final Map<String, String> attr;
	private final NodeIterable<SVGPathElement> paths;

	public StrokePlotter(SVGDocument src) {
		this.attr  = Map.of(
				"style", src.getDocumentElement().getElementsByTagName("g").item(0).getAttributes().getNamedItem("style").getTextContent(),
				"viewBox", src.getDocumentElement().getAttribute("viewBox"),
				"width", src.getDocumentElement().getAttribute("width"),
				"height", src.getDocumentElement().getAttribute("height")
						   );
		this.paths = new NodeIterable<>(src.getElementsByTagName("path"));
	}

	private BufferedImage renderSlide(int n, StrokesOptions sopt) throws IOException, TranscoderException {
		SVGDocument document = (SVGDocument) SVGDOMImplementation.getDOMImplementation().createDocument(svgNS, "svg", null);
		attr.forEach(document.getRootElement()::setAttribute);

		if (n > -1) {
			// Draw previous strokes
			for (int j = (sopt.onion_future ? paths.size() - 1 : n); j >= 0; j--)
				 appendPath(document, paths.get(j), (sopt.onion_future ? j > n : j < n) ? sopt.onion : sopt.drawn); // Draw some strokes in onion

			// Draw starting point
			SVGPathElement start = paths.get(n);
			SVGPoint       pt    = start.getPointAtLength(0f);
			appendPoint(document, pt.getX(), pt.getY(), sopt.point_r, sopt.point);

			// Draw current stroke
			appendPath(document, start, sopt.drawn);
		} else // Draw all strokes
			for (int j = 0; j < paths.size(); j++)
				 appendPath(document, paths.get(j), sopt.drawn);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Transcoder coder = new PNGTranscoder();

		TranscoderInput  i = new TranscoderInput(document);
		TranscoderOutput o = new TranscoderOutput(baos);

		coder.transcode(i, o);

		return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
	}

	public BufferedImage[] renderDiagrams(StrokesOptions sopt) throws IOException, TranscoderException {
		int len = paths.size();

		BufferedImage[] rendered = new BufferedImage[len];
		for (int i = len - 1; i >= 0; i--)
			 rendered[i] = renderSlide(i, sopt);

		return rendered;
	}

	public BufferedImage renderCombined(StrokesOptions sopt, DiagramOptions diag, CollageOptions opts) throws IOException, TranscoderException {
		BufferedImage[] images = renderDiagrams(sopt);

		if (diag.drawFullKanji) {
			BufferedImage[] temp = new BufferedImage[images.length + 1];
			System.arraycopy(images, 0, temp, 1, images.length);
			temp[0] = renderSlide(-1, sopt);
			images  = temp;
		}

		int nx = 0, ny = 0;
		if (DiagramOptions.x.equals(diag.orientation)) {
			nx = diag.wrapAt;
			ny = Util.ceilDiv(images.length, nx);
		}
		if (DiagramOptions.y.equals(diag.orientation)) {
			ny = diag.wrapAt;
			nx = Util.ceilDiv(images.length, ny);
		}

		int w = images[0].getWidth();
		int h = images[0].getHeight();

		Dimension dim = new Dimension(w, h);
		Count     ct  = new Count(nx, ny);

		BufferedImage canvas = new BufferedImage(w * nx, h * ny, BufferedImage.TYPE_INT_ARGB);
		Graphics2D    g2d    = canvas.createGraphics();

		drawGrid(g2d, dim, x, ct, opts.grid_epsilon_s, opts.grid_epsilon_e, opts.inner_color, opts.inner_thick, opts.inner_dash, 2);
		drawGrid(g2d, dim, y, ct, opts.grid_epsilon_s, opts.grid_epsilon_e, opts.inner_color, opts.inner_thick, opts.inner_dash, 2);
		drawGrid(g2d, dim, x, ct, opts.grid_epsilon_s, opts.grid_epsilon_e, opts.outer_color, opts.outer_thick, opts.outer_dash, 1);
		drawGrid(g2d, dim, y, ct, opts.grid_epsilon_s, opts.grid_epsilon_e, opts.outer_color, opts.outer_thick, opts.outer_dash, 1);

		int x = diag.isRTL() ? Util.ceilDiv(images.length, diag.wrapAt) - 1 : 0;
		int y = 0;
		for (BufferedImage image : images) {
			g2d.drawImage(image, x * w, y * h, null);
			if (DiagramOptions.x.equals(diag.orientation))
				if (++x >= nx) { // next row
					x = 0;
					y++;
				}
			if (DiagramOptions.y.equals(diag.orientation))
				if (++y >= ny) { // next col
					y = 0;
					x--;
				}
		}

		g2d.dispose();

		return canvas;
	}

}
