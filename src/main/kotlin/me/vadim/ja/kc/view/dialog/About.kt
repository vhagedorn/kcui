package me.vadim.ja.kc.view.dialog

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.group.swing
import java.awt.Dimension
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextPane

/**
 * @author vadim
 */
class About(frame: JFrame, private val version: String) : JDialog(frame, "KanjiCard UI v$version") {

	fun display() {
		pack()
		isVisible = true
		setLocationRelativeTo(null)
	}

	init {
		defaultCloseOperation = HIDE_ON_CLOSE
		add(swing<JPanel> {
			scrollPane {
				attr {
					size = Dimension(325, 175)
					preferredSize = size
					minimumSize = size
				}
				add(JTextPane().apply {
					contentType = "text/html"
					text = """
						<h1 style="text-align: center;">KanjiCard UI</h1>
						<h3 style="text-align: center; font-weight:normal;">Version $version</h3>
						<h2 style="text-align: center;">Copyright (C) 2023 Vadim Hagedorn</p>
						<p  style="text-align: center;">Licensed by the GNU General Public License v3.0</p>
					""".trimIndent()
					isEditable = false
				})
			}
		})
	}
}