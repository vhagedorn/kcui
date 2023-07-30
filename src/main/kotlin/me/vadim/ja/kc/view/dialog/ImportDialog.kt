package me.vadim.ja.kc.view.dialog

import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.JModalDialog
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.persist.io.JAXBStorage
import me.vadim.ja.kc.persist.wrapper.Card
import me.vadim.ja.kc.persist.wrapper.Curriculum
import me.vadim.ja.kc.persist.wrapper.Group
import me.vadim.ja.kc.render.impl.factory.PDFUtil
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JTable
import javax.swing.table.DefaultTableModel


/**
 * @author vadim
 */
class ImportDialog(private val kt: KanjiCardUIKt) : JModalDialog(kt.frame) {

	override val minSize = Dimension(350, 350)

	private val cards: List<Card>

	init {
		defaultCloseOperation = DISPOSE_ON_CLOSE
		layout = BorderLayout()
		isVisible = false

		val fc = JFileChooser()
		fc.currentDirectory = File(".")

		fc.fileSelectionMode = JFileChooser.FILES_ONLY
		fc.isMultiSelectionEnabled = true
		val resp = fc.showOpenDialog(this@ImportDialog)

		if (resp == JFileChooser.APPROVE_OPTION) {
			val kards = mutableListOf<Card?>()
			for (file in fc.selectedFiles)
				kards += JAXBStorage.readCard(file)
			this.cards = kards.filterNotNull().toList()

			add(swing<JPanel> {
				panel {
					borderLayout {
						top {
							label("Import cards?")
						}
						center {
							scrollPane {
								add(list(cards))
							}
						}
						bottom {
							panel {
								flowLayout {
									button("Import") {
										onAction {
											for (card in cards) {
												val k = kt.ctx.activeLibrary.createCard(card.location)
												k.copyDataFrom(card)
												card.location.group.createInParent(k.location.curriculum) // todo: location refactor upon import
											}
											kt.explorer.repopulate()
											isVisible = false
											dispose()
										}
									}
									button("Cancel") {
										onAction {
											kt.explorer.repopulate()
											isVisible = false
											dispose()
										}
									}
								}
							}
						}
					}
				}
			}, BorderLayout.CENTER)
			display()
		} else {
			isVisible = false
			dispose()
			this.cards = emptyList()
		}
	}
}