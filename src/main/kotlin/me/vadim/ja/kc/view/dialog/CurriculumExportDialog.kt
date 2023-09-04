package me.vadim.ja.kc.view.dialog

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.gridBagLayout
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.JModalDialog
import me.vadim.ja.kc.KanjiCardUI
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.model.wrapper.Curriculum
import me.vadim.ja.kc.model.wrapper.Group
import me.vadim.ja.kc.render.impl.factory.PDFUtil
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.table.DefaultTableModel


/**
 * @author vadim
 */
class CurriculumExportDialog(private val curriculum: Curriculum, private val kt: KanjiCardUIKt) : JModalDialog(kt.frame) {

	private val table: JTable
	private lateinit var split: JCheckBox

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
						panel {
							attr {
								border = TitledBorder("Export Options").apply {
									titleJustification = TitledBorder.CENTER
								}
							}
							gridBagLayout {
								cell {
									attr {
										cons {
											anchor = GridBagConstraints.WEST
											gridx = 0
											gridy = 1
											weightx = 1.0
											weighty = 1.0
											fill = GridBagConstraints.HORIZONTAL
										}
									}
									split = checkBox("Split front/back")
									split.isSelected = true
								}
							}
						}
					}
					center {
						panel {
							borderLayout {
								top {
									label("Choose groups from $curriculum to export.")
								}
								center {
									scrollPane {
										add(table)
									}
								}
							}
						}
					}
					bottom {
						button("Export") {
							onAction {
								val groups = mutableListOf<Group>()
								for (i in 0 until table.rowCount)
									if (table.getValueAt(i, 1) == true)
										groups.add(table.getValueAt(i, 0) as Group)

								val job = kt.ctx.renderContext.createExport(groups).orderByGroups()

								val pb = object : JModalDialog(this@CurriculumExportDialog) {
									override val minSize = Dimension(250, 20)

									val progress = JProgressBar(1, job.expectedUpdatePollCount)

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

								job.sendProgressUpdates {
									SwingUtilities.invokeLater {
										pb.progress.value++
									}
								}

								val dual = split.isSelected
								job.splitFrontAndBack(dual).result.thenAccept {
									pb.dispose()

									try {
										if (it != null) {
											val fc = JFileChooser(".")
											fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
											val resp = fc.showSaveDialog(this@CurriculumExportDialog)

											val date = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
											val names = if (dual) arrayOf("front", "back") else arrayOf("all")
											if (resp == JFileChooser.APPROVE_OPTION)
												for (i in it.indices)
													it[i].save(File(fc.selectedFile, "export_${date}_${curriculum}-${names[i]}.pdf"))
										} else
											KanjiCardUI.postError("(1) There was an error while exporting!")
									} catch (e: Exception) {
										e.printStackTrace()
										KanjiCardUI.postError("(2) There was an error while exporting!", e)
									} finally {
										PDFUtil.closeSafely(*it)
									}

									dispose()
								}.exceptionally {
									KanjiCardUI.postError("(3) There was an error while exporting!", it)
									it.printStackTrace()
									null
								}
							}
						}
					}
				}
			}
		}, BorderLayout.CENTER)
	}
}