package me.vadim.ja.kc.view.dialog

import io.github.mslxl.ktswing.BasicScope
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.JModalDialog
import me.vadim.ja.kc.ui.KCTheme
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.hintField
import me.vadim.ja.kc.model.wrapper.Library
import me.vadim.ja.kc.util.Util
import me.vadim.ja.swing.HintTextField
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * @author vadim
 */
class AuthorDialog(private val library: Library, private val kt: KanjiCardUIKt) : JModalDialog(kt.frame) {

	override val minSize = Dimension(200, 125)

	private lateinit var name: HintTextField

	init {
		defaultCloseOperation = HIDE_ON_CLOSE
		layout = BorderLayout()
		add(swing<JPanel> {
			panel {
				borderLayout {
					top {
						label("Enter your name:") {
							attr {
								toolTipText = "Visible when sharing media."
							}
						}
					}
					center {
						name = hintField("Author") {
							attr {
								font = KCTheme.JP_FONT
								toolTipText = "Visible when sharing media."
							}
						}
					}
					bottom {
						panel {
							flowLayout {
								button("OK") {
									attr {
										toolTipText = "Set name seen when sharing. Change in Preferences."
									}
									onAction {
										library.author = Util.sanitizeXML(name.text)
										this@AuthorDialog.isVisible = false
									}
								}
								button("Skip") {
									attr {
										toolTipText = "May be changed in Preferences."
									}
									onAction {
										library.author = "Anonymous"
										this@AuthorDialog.isVisible = false
									}
								}
							}
						}

					}
				}
			}
		}, BorderLayout.CENTER)
	}
}