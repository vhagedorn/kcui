package me.vadim.ja.kc;

import me.vadim.ja.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vadim
 */
public class KanjiCardUI extends Application {

	public static final int PREFERRED_SIZE_X = 820;
	public static final int PREFERRED_SIZE_Y = 520;
	public static final int MINIMUM_SIZE_X = 600;
	public static final int MINIMUM_SIZE_Y = 350;

	public KanjiCardUI() throws IOException {
		setPreferredSize(new Dimension(PREFERRED_SIZE_X, PREFERRED_SIZE_Y));
		setMinimumSize(new Dimension(MINIMUM_SIZE_X, MINIMUM_SIZE_Y));
		setIconImage(KCIcon.LOGO.getPrimary().unedited());
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setUndecorated(true);
	}

	protected void populate(){
		setLayout(new BorderLayout());
		createMenuBar();
		add(KCUI.tabPane(this), BorderLayout.CENTER);
	}

	private final Icon d0 = KCIcon.DEBUG.getSecondary().withSize(20, 20).asIcon();
	private final Icon d1 = KCIcon.DEBUG.getPrimary().withSize(20, 20).asIcon();

	//toggleable atomic boolean
	private static class Mock {
		private final AtomicLong count = new AtomicLong(0);

		public boolean respond() {
			return count.getAndIncrement() % 2 == 0;
		}
	}
	private final Mock debugState = new Mock();

	protected void createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
//		menuBar.setLayout(new GridBagLayout());

		JButton button = new JButton(d1);
		button.setBackground(null);
		button.setBorder(null);
//		button.setFocusPainted(false);
		button.addActionListener(e -> {
			boolean state = debugState.respond();
			if(state){
				button.setIcon(d0);
			}else{
				button.setIcon(d1);
			}
			KCUI.debugBorders(this, state, true);
		});

		setJMenuBar(menuBar);
		menuBar.add(Box.createGlue());// this un-centers the title :\
		menuBar.add(button/*, new GridBagConstraints(){{
			weightx = 1;
			weighty = 1;
			anchor = GridBagConstraints.EAST;
			fill = GridBagConstraints.BOTH;
		}}*/);
	}

	@Override
	public void mainWindow() {
		setTitle("KanjiCard UI");
		setPreferredSize(new Dimension(PREFERRED_SIZE_X, PREFERRED_SIZE_Y));
		populate();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public Rectangle getMaximizedBounds()
	{
		return(maxBounds);
	}

	public synchronized void setMaximizedBounds(Rectangle maxBounds)
	{
		this.maxBounds = maxBounds;
		super.setMaximizedBounds(maxBounds);
	}

	private Rectangle maxBounds;
	public synchronized void setExtendedState(int state)
	{
		if (maxBounds == null &&
			(state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
		{
			Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
			Rectangle screenSize = getGraphicsConfiguration().getBounds();
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
			this.frameBounds = frame.getBounds();
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