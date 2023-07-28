package me.vadim.ja.kc

import java.awt.Dimension
import java.awt.Window
import javax.swing.JDialog

/**
 * @author vadim
 */
abstract class JModalDialog(private val ow: Window) : JDialog(ow, ModalityType.MODELESS) {

	abstract val minSize: Dimension

	init {
		defaultCloseOperation = DISPOSE_ON_CLOSE
	}

	open fun display() {
		minimumSize = minSize
		size = minSize
		preferredSize = minSize
		pack()
		setLocationRelativeTo(ow)
		isVisible = true
	}

}