package me.vadim.ja.swing;

import com.formdev.flatlaf.ui.FlatMenuItemRenderer;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;

/**
 * @author vadim
 */
public class CustomFlatMenuItemRenderer extends FlatMenuItemRenderer {

	public CustomFlatMenuItemRenderer(JMenuItem menuItem, Icon checkIcon, Icon arrowIcon, Font acceleratorFont, String acceleratorDelimiter) {
		super(menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter);
	}

	@Override
	protected String getTextForAccelerator( KeyStroke accelerator ) {
		StringBuilder buf = new StringBuilder();
		boolean leftToRight = menuItem.getComponentOrientation().isLeftToRight();

		// modifiers
		int modifiers = accelerator.getModifiers();
		if( modifiers != 0 ) {
			if( SystemInfo.isMacOS ) {
				if( leftToRight )
					buf.append( getMacOSModifiersExText( modifiers, leftToRight ) );
			} else
				buf.append(InputEvent.getModifiersExText(modifiers)).append(acceleratorDelimiter);
		}

		// key
		int keyCode = accelerator.getKeyCode();
		if( keyCode != 0 )
			buf.append(getKeyText(keyCode));
		else
			buf.append( accelerator.getKeyChar() );

		// modifiers if right-to-left on macOS
		if( modifiers != 0 && !leftToRight && SystemInfo.isMacOS )
			buf.append( getMacOSModifiersExText( modifiers, leftToRight ) );

		return buf.toString();
	}
	
	private static String getKeyText(int keyCode) {
		switch(keyCode) {
			case VK_COMMA: return ",";
			case VK_PERIOD: return ".";
			case VK_SLASH: return "/";
			case VK_SEMICOLON: return ";";
			case VK_EQUALS: return "=";
			case VK_OPEN_BRACKET: return "[";
			case VK_BACK_SLASH: return "\\";
			case VK_CLOSE_BRACKET: return "]";
			case VK_AMPERSAND: return "&";
			case VK_ASTERISK: return "*";
			case VK_QUOTEDBL: return "\"";
			case VK_LESS: return "<";
			case VK_GREATER: return ">";
			case VK_BRACELEFT: return "{";
			case VK_BRACERIGHT: return "}";
			case VK_AT: return "@";
			case VK_COLON: return ":";
			case VK_DOLLAR: return "$";
			case VK_EXCLAMATION_MARK: return "!";
			case VK_LEFT_PARENTHESIS: return "(";
			case VK_MINUS: return "-";
			case VK_PLUS: return "+";
			case VK_RIGHT_PARENTHESIS: return ")";
			case VK_UNDERSCORE: return "_";
			case VK_QUOTE: return "'";
			default: return KeyEvent.getKeyText(keyCode);
		}
	}

}
