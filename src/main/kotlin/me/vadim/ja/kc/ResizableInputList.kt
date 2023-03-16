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
import javax.management.Query
import javax.management.Query.attr
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible

/**
 * @author vadim
 */
class ResizableInputList<T>(
	scope: GridBagLayoutRootScope<*>,
	private val frame: JFrame,
	private val label: String,
	private val property: KMutableProperty<JPanel>,
	private val eachRow: CanAddChildrenScope<*>.(x: T?) -> Unit,
	private val assign: GridBagLayoutCellScope<*>.() -> Unit
						) {

	init {
		property.apply {
			isAccessible = true
		}

		scope.apply {

			cell {
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
					flowLayout(FlowLayout.LEFT) {
						label(label) {
							attr {
								maximumSize = preferredSize
							}
						}
						panel {
							flowLayout(FlowLayout.LEFT, 0) {
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
				assign()
				createLine()
			}
		}
	}

	fun clear(){
		val list = property.getter.call()
		list.removeAll()
		frame.revalidate()
	}

	fun createLine(item: T? = null) {
		val list = property.getter.call()
		list.add(swing<JPanel> {
			panel {
				flowLayout(FlowLayout.LEFT) {
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

		if(idx < 0)
			idx = list.componentCount - 1

		if(idx > -1 && idx < list.componentCount)
			list.remove(idx)
		else if (idx != -1)
			throw IllegalArgumentException("$i >= ${list.componentCount}")

		frame.revalidate()
	}

}