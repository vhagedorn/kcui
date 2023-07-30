package me.vadim.ja.kc

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.GridBagLayoutCellScope
import io.github.mslxl.ktswing.layout.GridBagLayoutRootScope
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.ktswing.onAction
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.lang.IllegalArgumentException
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible

//todo

/**
 * @author vadim
 */
class ResizableInputTable<T>(
	scope: GridBagLayoutRootScope<*>,
	private val frame: JFrame,
	private val label: String,
	private val property: KMutableProperty<JPanel>,
	private val instance: Any,
	private val eachRow: CanAddChildrenScope<*>.(x: T?) -> Unit,
	private val block: GridBagLayoutCellScope<*>.() -> Unit
							) {

	init {
		property.apply {
			isAccessible = true
		}

		scope.apply {
			cell {
				val table = JTable()
				table.setShowGrid(false)
				val col = table.getColumn(2)
				col.cellEditor
				attr {
					cons {
						gridx = 0
						gridy = 0
						weightx = 1.0
						weighty = 1.0
						fill = GridBagConstraints.HORIZONTAL
						anchor = GridBagConstraints.NORTHWEST
					}
				}
				panel {
					flowLayout(FlowLayout.LEADING) {
						label(label) {
							attr {
								maximumSize = preferredSize
							}
						}
						panel {
							flowLayout(FlowLayout.LEADING, 0) {
								button(KCTheme.getButtonIcon(KCIcon.ADD.primary)) {
									onAction {
										createLine()
									}
								}
								button(KCTheme.getButtonIcon(KCIcon.REMOVE.primary)) {
									onAction {
										deleteLine()
									}
								}
							}
						}
					}
				}
			}

			cell {
				attr {
					cons {
						gridx = 1
						gridy = 0
						weightx = 1.0
						weighty = 1.0
						fill = GridBagConstraints.NONE
						anchor = GridBagConstraints.NORTHWEST
					}
				}
				block()
				createLine()
			}
		}
	}

	fun clear() {
		val list = property.getter.call()
		list.removeAll()
		frame.revalidate()
	}

	fun createLine(item: T? = null) {
		val list = property.getter.call()
		list.add(swing<JPanel> {
			panel {
				flowLayout(FlowLayout.LEADING) {
					eachRow(item)
				}
			}
		}, GridBagConstraints().apply {
			gridx = 0
			gridy = list.componentCount
			weightx = 1.0
			weighty = 1.0
			fill = GridBagConstraints.NONE
			anchor = GridBagConstraints.NORTHWEST
		})
		frame.revalidate()
	}

	fun deleteLine(i: Int = -1) {
		var idx = i
		val list = property.getter.call()

		if (idx < 0)
			idx = list.componentCount - 1

		if (idx > -1 && idx < list.componentCount && list.componentCount > 1)
			list.remove(idx)
		else if (i != -1)
			throw IllegalArgumentException("Unable to remove index $i from list with len=${list.components}.")

		frame.revalidate()
	}
}