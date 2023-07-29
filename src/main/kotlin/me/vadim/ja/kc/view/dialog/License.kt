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
class License(frame: JFrame, private val license: String) : JDialog(frame, "Licensed by the GNU General Public License v3.0") {

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
					size = Dimension(600, 700)
					preferredSize = size
					minimumSize = size
				}
				add(JTextPane().apply {
					contentType = "text/html"
					text = license
					isEditable = false
				})
			}
		})
	}
}