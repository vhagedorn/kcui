package me.vadim.ja.kc.view

import me.vadim.ja.kc.KCIcon
import me.vadim.ja.kc.KCTheme
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.persist.io.JAXBStorage
import me.vadim.ja.kc.persist.wrapper.Card
import me.vadim.ja.kc.persist.wrapper.Curriculum
import me.vadim.ja.kc.view.dialog.About
import me.vadim.ja.kc.view.dialog.CurriculumExportDialog
import me.vadim.ja.kc.view.dialog.ImportDialog
import me.vadim.ja.kc.view.dialog.License
import java.awt.Container
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicLong
import javax.swing.*
import javax.swing.border.TitledBorder

/**
 * @author vadim
 */
class Toolbar(private val kt: KanjiCardUIKt, private val license: License, private val about: About) : JMenuBar() {

	private val frame = kt.frame as JFrame

	//erases tooltip text
	private fun debugBorders(component: Container, enable: Boolean, original: Boolean = true) {
		for (child in component.components) {
			if (child is JComponent) {
				debugBorders(child, enable, false)
				if (enable) {
					if (child != null && child.border == null) {
						try {
							child.toolTipText = "debug"
							child.border = TitledBorder(
								try {
									child?.layout::class.java
								} catch (ignored: Exception) {
									child::class.java
								}?.simpleName ?: "null"
													   )
						} catch (e: RuntimeException) {
							continue // some elements do not allow borders
						}
					}
				} else
					if (child.toolTipText == "debug") {
						child.border = null
						child.toolTipText = null
					}
			}
		}
		if (original)
			component.revalidate()
	}

	//toggleable atomic boolean
	private class Mock {

		private val count = AtomicLong(0)
		fun respond(): Boolean {
			return count.getAndIncrement() % 2 == 0L
		}
	}

	private val debugState = Mock()

	private val d0 = KCTheme.getButtonIcon(KCIcon.DEBUG.secondary)
	private val d1 = KCTheme.getButtonIcon(KCIcon.DEBUG.primary)

	init {
		val button = JButton(d1)
		button.background = null
		button.border = null
		button.addActionListener {
			val state: Boolean = debugState.respond()
			button.icon = if (state) d0 else d1
			debugBorders(frame, state)
		}

		menu("File") {
			item("Import...") {
				attr {
					accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
					mnemonic = KeyEvent.VK_I
				}
				onAction {
					ImportDialog(kt)
				}
			}
			subMenu("Export") {
				attr {
					mnemonic = KeyEvent.VK_E
				}
				item("Quick export to PDF") {
					attr {
						isEnabled = false
						accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
						mnemonic = KeyEvent.VK_Q
					}

					onAction {

					}
				}
				item("Export As...") {
					attr {
						isEnabled = false
						accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx or KeyEvent.SHIFT_DOWN_MASK)
						mnemonic = KeyEvent.VK_E
					}
				}
				item("Batch Export Curriculum") {
					attr {
						accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx or KeyEvent.ALT_DOWN_MASK)
						mnemonic = KeyEvent.VK_B
					}
					onAction {
						val chosen = JOptionPane.showInputDialog(
							frame, "Choose a curriculum:", "Export",
							JOptionPane.PLAIN_MESSAGE, null, kt.ctx.activeLibrary.curriculums.toTypedArray(), null
																) as Curriculum? ?: return@onAction

						CurriculumExportDialog(chosen, kt).display()
					}
				}
			}
		}

		menu("Edit") {}

		menu("About") {
			item("Info") {
				onAction {
					about.display()
				}
			}
			item("License") {
				attr {
					mnemonic = KeyEvent.VK_L
				}
				onAction {
					license.display()
				}
			}
			item("Preferences") {
				attr {
					accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
					mnemonic = KeyEvent.VK_P
				}

				onAction {
					TODO("Preferences not implemented yet.")
				}
			}
		}

		add(Box.createGlue()) // this un-centers the title :\
		add(button)
	}

	companion object {

		fun Container.menu(name: String, block: MenuScope.() -> Unit): JMenu {
			val menu = JMenu(name)
			MenuScope(menu).apply(block)
			add(menu)
			return menu
		}
	}

	abstract class Scope<T>(val self: T) {

		fun attr(block: T.() -> Unit) = self.apply(block)
	}

	class MenuScope(self: JMenu) : Scope<JMenu>(self) {

		fun item(name: String, block: ItemScope.() -> Unit): JMenuItem {
			val item = JMenuItem(name)
			ItemScope(item).apply(block)
			self.add(item)
			return item
		}

		fun subMenu(name: String, block: MenuScope.() -> Unit): JMenu = self.menu(name, block)
	}

	class ItemScope(self: JMenuItem) : Scope<JMenuItem>(self) {

		fun onAction(block: (ActionEvent) -> Unit) = self.addActionListener(block)
	}

	fun dispose() {
		license.dispose()
		about.dispose()
	}
}