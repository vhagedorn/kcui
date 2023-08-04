package me.vadim.ja.kc.view.dialog

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
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.net.URI
import javax.swing.*


/**
 * @author vadim
 */
class PrefDialog(private val kt: KanjiCardUIKt) : JModalDialog(kt.frame) {

	override val minSize = Dimension(250, 125)

	private lateinit var kvg: HintTextField

	init {
		defaultCloseOperation = DO_NOTHING_ON_CLOSE
		layout = BorderLayout()
		add(swing<JPanel> {
			panel {
				borderLayout {
					top {
						label("Enter the path to the KanjiVG folder:") {
							attr {
								toolTipText = "Required to render stroke order diagrams."
								addMouseListener(object : MouseAdapter() {
									override fun mouseClicked(e: MouseEvent) {
										Util.browse(URI("https://github.com/KanjiVG/kanjivg"))
									}
								})
							}
						}
					}
					center {
						kvg = hintField("KanjiVG Directory") {
							attr {
								font = KCTheme.JP_FONT
							}
						}
					}
					bottom {
						panel {
							flowLayout {
								button("Set") {
									attr {
										toolTipText = "May be changed in Preferences."
									}
									onAction {
										kt.ctx.preferences.kvg_dir = File(kvg.text).absolutePath
										kt.ctx.preferences.applyProperties()
										kt.ctx.savePreferences()
										this@PrefDialog.isVisible = false
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