package me.vadim.ja.swing;

import com.formdev.flatlaf.ui.FlatMenuItemRenderer;
import com.formdev.flatlaf.ui.FlatMenuItemUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * @author vadim
 */
public class CustomFlatMenuItemUI extends FlatMenuItemUI {

	public static ComponentUI createUI(JComponent c) {
		return new CustomFlatMenuItemUI();
	}

	@Override
	protected FlatMenuItemRenderer createRenderer() {
		return new CustomFlatMenuItemRenderer(menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter);
	}

}
