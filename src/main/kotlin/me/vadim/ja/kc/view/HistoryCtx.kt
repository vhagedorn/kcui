package me.vadim.ja.kc.view

import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*


/**
 * @author vadim
 */
@Suppress("MemberVisibilityCanBePrivate")
open class HistoryCtx(
	protected val central: JComponent,
	undo: ((ActionEvent) -> Unit)? = null,
	redo: ((ActionEvent) -> Unit)? = null,
					 ) : JPopupMenu() {

	final override fun add(menuItem: JMenuItem?): JMenuItem? {
		val item = super.add(menuItem) ?: return null

		val action = object : AbstractAction() {
			override fun actionPerformed(e: ActionEvent) = item.actionListeners.forEach {
				it.actionPerformed(e)
			}
		}

		val key = item.text
		val stroke = item.accelerator
		central.registerKeyboardAction(action, key, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		item.registerKeyboardAction(action, key, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		//"obsolete" my ass
		//the "recommended" way doesn't work

		return item
	}

	init {
		if (undo != null)
			add(JMenuItem("Undo").apply {
				accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
				addActionListener {
					undo(it)
				}
			})

		if (redo != null)
			add(JMenuItem("Redo").apply {
				accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx) // we don't use CTRL+SHIFT+Z
				addActionListener {
					redo(it)
				}
			})
	}
}