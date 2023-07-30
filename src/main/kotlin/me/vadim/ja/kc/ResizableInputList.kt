package me.vadim.ja.kc

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.GridBagLayoutRootScope
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.ktswing.layout.gridBagLayout
import io.github.mslxl.ktswing.onAction
import org.intellij.lang.annotations.JdkConstants.BoxLayoutAxis
import java.awt.*
import java.lang.IllegalArgumentException
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * @author vadim
 */
class ResizableInputList<T>(
	scope: GridBagLayoutRootScope<*>,
	private val frame: JFrame,
	private val label: String,
	private val eachRow: CanAddChildrenScope<*>.(x: T?) -> Unit,
						   ) {

	private val list: JPanel

	val components: Array<out Component>
		get() = list.components

	init {
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
						weighty = 0.0
						fill = GridBagConstraints.NONE
						anchor = GridBagConstraints.NORTHWEST
					}
				}
				scrollPane {
					attr {
						horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
						verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
						border = null
						viewportBorder = null
					}
					list = panel {
						attr {
							layout = BoxLayout(self, BoxLayout.Y_AXIS)
						}
					}
				}
			}
		}
		createLine()
		//todo: max size not working, but that's ok
		//todo: the issue is that it will "snap" to minimum size if it exceeds the space it has available
		//todo: this works alright, however; so i'm done fighting it for now
		list.parent.apply {
			minimumSize = Dimension(preferredSize.width, preferredSize.height * 3)
			maximumSize = Dimension(preferredSize.width * 3, preferredSize.height * 5)
		}
	}

	fun clear() {
		list.removeAll()
		frame.revalidate()
	}

	fun createLine(item: T? = null) {
		list.add(swing<JPanel> {
			panel {
				flowLayout(FlowLayout.LEADING) {
					eachRow(item)
				}
			}
		})
		frame.revalidate()
		frame.repaint()
	}

	fun deleteLine(i: Int = -1) {
		var idx = i

		if (idx < 0)
			idx = list.componentCount - 1

		if (idx > -1 && idx < list.componentCount && list.componentCount > 1)
			list.remove(idx)
		else if (i != -1)
			throw IllegalArgumentException("Unable to remove index $i from list with len=${list.components}.")

		frame.revalidate()
		frame.repaint()
	}
}