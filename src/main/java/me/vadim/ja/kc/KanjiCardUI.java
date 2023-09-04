package me.vadim.ja.kc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.vadim.ja.Application;
import me.vadim.ja.kc.model.xml.JAXBStorage;
import me.vadim.ja.kc.render.DocConverters;
import me.vadim.ja.kc.render.impl.factory.Generator;
import me.vadim.ja.kc.ui.KCIcon;
import me.vadim.ja.kc.util.Util;
import me.vadim.ja.kc.util.threading.LocalExecutors;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * @author vadim
 */
public class KanjiCardUI extends Application {

	public static final int PREFERRED_SIZE_X = 820;
	public static final int PREFERRED_SIZE_Y = 575;
	public static final int MINIMUM_SIZE_X = 725;
	public static final int MINIMUM_SIZE_Y = 500;

	public static ExecutorService threadPool(String nameFormat) {
		return LocalExecutors.newExtendedThreadPool(new ThreadFactoryBuilder()
															.setDaemon(true)
															.setNameFormat(nameFormat)
															.build());
	}

	public static ExecutorService singleThread(String nameFormat) {
		return LocalExecutors.newExtendedThreadPool(new ThreadFactoryBuilder()
															.setDaemon(false)
															.setNameFormat(nameFormat)
															.build());
	}

	public static void postError(String message) {
		postError(message, null);
	}

	public static void postError(String message, @Nullable Throwable t) {
		String msg = message;
		if(t != null)
			msg += String.format("\n[%s: %s]", t.getClass().getSimpleName(), t.getMessage());
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings("FieldCanBeLocal")
	private final TextResource
			version, license_txt, templates_md,
			license_html, back_html, front_html,
			printing_css;
	private final KanjiCardUIKt kt;

	private static class TextResource {

		final String fname, rname;
		byte[] buf;

		TextResource(String fname) throws IOException {
			this.fname = fname.startsWith("./") ? fname : "./" + fname;
			this.rname = this.fname.substring(2);
		}

		TextResource(String rname, String fname) throws IOException {
			this.fname = fname.startsWith("./") ? fname : "./" + fname;
			this.rname = rname.startsWith("./") ? rname.substring(2) : rname;
		}

		void load() throws IOException {
			try (InputStream is = Util.loadResource(rname)) {
				buf = is.readAllBytes();
			}
		}

		void copyReplace() throws IOException {
			if (buf == null) load();
			new File(fname).getParentFile().mkdirs();
			Files.write(Path.of(fname), buf);
		}

		void copyOrLoad() throws IOException {
			if (buf == null) load();
			if (!new File(fname).exists())
				copyReplace();
			else
				buf = Files.readAllBytes(Path.of(fname));
		}

		public String asString() throws IOException {
			if (buf == null) load();
			return toString();
		}

		@Override
		public String toString() {
			if (buf == null) throw new UnsupportedOperationException("call load() first");
			return new String(buf, StandardCharsets.UTF_8);
		}

	}

	public KanjiCardUI() throws IOException {
		{
			version = new TextResource("version");
			version.load();

			license_txt = new TextResource("LICENSE.txt");
			license_txt.copyReplace();

			templates_md = new TextResource("templates.md", "template/README.md");
			templates_md.copyReplace();

			license_html = new TextResource("LICENSE.html");
			license_html.load();

			back_html = new TextResource("doc/back.html", "template/back_side_card.html");
			back_html.copyOrLoad();
			front_html = new TextResource("doc/front.html", "template/frontside_card.html");
			front_html.copyOrLoad();

			Generator.back  = back_html.asString();
			Generator.front = front_html.asString();

			printing_css = new TextResource("doc/printing.css", "template/card_stylesheet.css");
			printing_css.copyOrLoad();
			DocConverters.printing_css = printing_css.asString();

			// todo
			JAXBStorage.prefDir = new File(".");
			JAXBStorage.cardDir = new File("./cards");
			JAXBStorage.freeDir = new File("./cards/.group");
		}

		System.out.println();
		legal();
		banner();
		System.out.println("KanjiCard UI" + " v" + version);
		System.out.println("\tby Vadim Hagedorn");
		System.out.println();

//		kt = null;
		kt = new KanjiCardUIKt(this);
		kt.setVersion(version.asString());
		kt.setLicense(license_html.asString());
		setup();
	}

	private void setup() {
		setFocusable(true);
		setPreferredSize(new Dimension(PREFERRED_SIZE_X, PREFERRED_SIZE_Y));
		setMinimumSize(new Dimension(MINIMUM_SIZE_X, MINIMUM_SIZE_Y));
		setIconImage(KCIcon.LOGO.getPrimary().unedited());
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.print("Cleaning up");
				System.out.print('.');
				e.getWindow().setVisible(false);
				System.out.print('.');
				kt.shutdown();
				System.out.print('.');
				System.out.println(" done.");
			}
		});
//		setUndecorated(true);
	}

	private static void banner() {
		System.out.println();
		System.out.println("888    d8P                     d8b d8b  .d8888b.                       888           888     888 8888888 \n" +
						   "888   d8P                      Y8P Y8P d88P  Y88b                      888           888     888   888   \n" +
						   "888  d8P                               888    888                      888           888     888   888   \n" +
						   "888d88K      8888b.  88888b.  8888 888 888         8888b.  888d888 .d88888           888     888   888   \n" +
						   "8888888b        \"88b 888 \"88b \"888 888 888            \"88b 888P\"  d88\" 888           888     888   888   \n" +
						   "888  Y88b   .d888888 888  888  888 888 888    888 .d888888 888    888  888           888     888   888   \n" +
						   "888   Y88b  888  888 888  888  888 888 Y88b  d88P 888  888 888    Y88b 888           Y88b. .d88P   888   \n" +
						   "888    Y88b \"Y888888 888  888  888 888  \"Y8888P\"  \"Y888888 888     \"Y88888            \"Y88888P\"  8888888 \n" +
						   "                               888                                                                       \n" +
						   "                              d88P                                                                       \n" +
						   "                            888P\"                                                                        ");
		System.out.println();
	}

	private static void legal() {
		System.out.println("-------NOTICE-------");
		System.out.println("    KanjiCard UI  Copyright (C) 2023  Vadim Hagedorn\n" +
						   "    This program comes with ABSOLUTELY NO WARRANTY.\n" +
						   "    This is free software, and you are welcome to\n" +
						   "    redistribute it under certain conditions. For\n" +
						   "    details, see `File > License` or `LICENSE.txt`.");
		System.out.println("-------======-------");
	}

	@Override
	public void mainWindow() {
		setTitle("KanjiCard UI");
		setPreferredSize(new Dimension(PREFERRED_SIZE_X, PREFERRED_SIZE_Y));
		populate();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		kt.showFirstLaunch();
	}

	protected void populate() {
		setLayout(new BorderLayout());
		add(kt.populate(), BorderLayout.CENTER);
	}

	@Override
	public Rectangle getMaximizedBounds() {
		return (maxBounds);
	}

	@Override
	public synchronized void setMaximizedBounds(Rectangle maxBounds) {
		this.maxBounds = maxBounds;
		super.setMaximizedBounds(maxBounds);
	}

	private Rectangle maxBounds;

	@Override
	public synchronized void setExtendedState(int state) {
		if (maxBounds == null &&
			(state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
			Insets    screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
			Rectangle screenSize   = getGraphicsConfiguration().getBounds();
			Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x,
												screenInsets.top + screenSize.y,
												screenSize.x + screenSize.width - screenInsets.right - screenInsets.left,
												screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);
			super.setMaximizedBounds(maxBounds);
		}

		super.setExtendedState(state);
	}

	public static class FrameDragListener extends MouseAdapter {

		private final JFrame frame;

		private Point pressedPoint;
		private Rectangle frameBounds;

		public FrameDragListener(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			moveJFrame(event);
		}

		@Override
		public void mousePressed(MouseEvent event) {
			this.frameBounds  = frame.getBounds();
			this.pressedPoint = event.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			moveJFrame(event);
		}

		private void moveJFrame(MouseEvent event) {
			Point endPoint = event.getPoint();

			int xDiff = endPoint.x - pressedPoint.x;
			int yDiff = endPoint.y - pressedPoint.y;
			frameBounds.x += xDiff;
			frameBounds.y += yDiff;
			frame.setBounds(frameBounds);
		}

	}

}