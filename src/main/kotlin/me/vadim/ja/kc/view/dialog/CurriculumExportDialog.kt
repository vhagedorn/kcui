package me.vadim.ja.kc.view.dialog

import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.JModalDialog
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.model.wrapper.Card
import me.vadim.ja.kc.model.wrapper.Curriculum
import me.vadim.ja.kc.model.wrapper.Group
import me.vadim.ja.kc.render.impl.factory.PDFUtil
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JTable
import javax.swing.table.DefaultTableModel


/**
 * @author vadim
 */
class CurriculumExportDialog(private val curriculum: Curriculum, private val kt: KanjiCardUIKt) : JModalDialog(kt.frame) {

	private val table: JTable

	override val minSize = Dimension(350, 350)

	init {
		defaultCloseOperation = DISPOSE_ON_CLOSE
		layout = BorderLayout()
		add(swing<JPanel> {
			panel {
				val dat = curriculum.groups.map { arrayOf(it, java.lang.Boolean.TRUE) }.toTypedArray()
				val model = object : DefaultTableModel(dat, arrayOf("Group", "Exporting?")) {
					override fun getColumnClass(columnIndex: Int): Class<*> =
						when (columnIndex) {
							0    -> java.lang.String::class.java
							1    -> java.lang.Boolean::class.java
							else -> java.lang.Object::class.java
						}
				}

				table = JTable(model)
					.apply {
						preferredScrollableViewportSize = preferredSize
					}
				borderLayout {
					top {
						label("Choose groups from $curriculum to export.")
					}
					center {
						scrollPane {
							add(table)
						}
					}
					bottom {
						button("Export") {
							onAction {
								val groups = mutableListOf<Group>()
								for (i in 0 until table.rowCount)
									if (table.getValueAt(i, 1) == true)
										groups.add(table.getValueAt(i, 0) as Group)
								val cards = groups.flatMap { kt.ctx.activeLibrary.getCards(it) }.toList()

								val pb = object : JModalDialog(this@CurriculumExportDialog) {
									override val minSize = Dimension(250, 20)

									val progress = JProgressBar(0, 4) // see comment on export method

									init {
										layout = BorderLayout()
										progress.isIndeterminate = false
										progress.string = "Exporting..."
										title = "Exporting..."
										isUndecorated = true
										add(progress, BorderLayout.CENTER)
									}
								}
								pb.display()
								kt.ctx.export(cards, kt.preview.gather()) {
									synchronized(pb) {
										pb.progress.value++
									}
								}.thenAccept {
									pb.dispose()

									if(it != null) {
										val fc = JFileChooser(".")
										fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
										val resp = fc.showSaveDialog(this@CurriculumExportDialog)

										val names = arrayOf("front", "back")
										if (resp == JFileChooser.APPROVE_OPTION)
											for (i in it.indices)
												it[i].save(File(fc.selectedFile, "$curriculum-${names[i]}.pdf"))

										PDFUtil.closeSafely(*it)
									} else {
										JOptionPane.showMessageDialog(this@CurriculumExportDialog, "There was an error while exporting!", "Export Error", JOptionPane.ERROR_MESSAGE)
									}

									dispose()
								}
							}
						}
					}
				}
			}
		}, BorderLayout.CENTER)
	}
}