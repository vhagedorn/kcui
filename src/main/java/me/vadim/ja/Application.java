package me.vadim.ja;

import javax.swing.*;
import java.io.IOException;

/**
 * @author vadim
 */
public abstract class Application extends JFrame {

	public Application() throws IOException {}

	public abstract void mainWindow();

	@Deprecated
	protected void systemLAF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			for (UIManager.LookAndFeelInfo installedLookAndFeel : UIManager.getInstalledLookAndFeels()) {
//				System.out.println(installedLookAndFeel.getName() + " -> " + installedLookAndFeel.getClassName());
//			}
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}