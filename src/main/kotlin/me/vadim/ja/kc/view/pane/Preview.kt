package me.vadim.ja.kc.view.pane

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.*
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.ui.KCIcon
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.ui.Texture
import me.vadim.ja.kc.ui.impl.Icons
import me.vadim.ja.kc.model.wrapper.Card
import me.vadim.ja.kc.render.impl.svg.opt.DiagramOptions
import me.vadim.ja.kc.row
import me.vadim.ja.swing.ImagePanel
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException
import javax.swing.*

/**
 * @author vadim
 */
class Preview(private val kt: KanjiCardUIKt) : JPanel(BorderLayout()) {

	companion object {

		const val INITIAL_HEIGHT = 350
	}

	/**
	 * When `forcedWidth` AND `forcedHeight` are set, both are applied in that order (resulting image quality is undefined).
	 */
	@Suppress("JoinDeclarationAndAssignment")
	private class ImageCarousel(var forcedWidth: Int = -1, var forcedHeight: Int = -1) : JPanel(GridBagLayout()) {

		private companion object {

			@JvmStatic
			private fun smallButton(icon: Texture): Icon = icon.withSize(15, 15).asIcon()
		}

		private var index = 0
		private val panel: JPanel
		private val card: CardLayout
		private val back = JButton()
		private val next = JButton()

		fun render() {
			synchronized(panel.treeLock) {
				back.isEnabled = index > 0 && panel.componentCount > 1
				next.isEnabled = index < panel.componentCount - 1
				card.show(panel, index.toString())
				for (component in panel.components)
					if (component is ImagePanel)
						component.reapplyImage()
			}
			panel.revalidate()
			panel.repaint()
		}

		init {
			card = CardLayout()
			panel = JPanel(card)

			back.apply {
				icon = smallButton(KCIcon.LEFT.primary)
				isEnabled = false
				addActionListener {
					index--
					render()
				}
			}
			next.apply {
				icon = smallButton(KCIcon.RIGHT.primary)
				isEnabled = false
				addActionListener {
					index++
					render()
				}
			}

			add(panel, GridBagConstraints().apply {
				gridx = 1
				gridy = 0
				weightx = 1.0
				weighty = .9
				fill = GridBagConstraints.BOTH
				anchor = GridBagConstraints.CENTER
			})
			add(back, GridBagConstraints().apply {
				gridx = 0
				gridy = 1
				weightx = 1.0
				weighty = .1
				fill = GridBagConstraints.NONE
				anchor = GridBagConstraints.WEST
			})
			add(Box.createHorizontalGlue(), GridBagConstraints().apply {
				gridx = 1
				gridy = 1
				weightx = 1.0
				weighty = .1
				fill = GridBagConstraints.BOTH
				anchor = GridBagConstraints.CENTER
			})
			add(next, GridBagConstraints().apply {
				gridx = 2
				gridy = 1
				weightx = 1.0
				weighty = .1
				fill = GridBagConstraints.NONE
				anchor = GridBagConstraints.EAST
			})
		}

		private fun ImagePanel.reapplyImage() = applyImage(image)

		private fun ImagePanel.applyImage(ico: BufferedImage): ImagePanel {
			sticksTo = SwingConstants.CENTER
			val wrap = Icons.wrap(ico)
			if (forcedWidth > 0)
				wrap.withWidth(forcedWidth)
			if (forcedHeight > 0)
				wrap.withHeight(forcedHeight)
			image = wrap.asImage()
			return this
		}

		fun addImage(image: BufferedImage) {
			synchronized(panel.treeLock) {
				panel.add(ImagePanel().applyImage(image), panel.componentCount.toString())
			}
			render()
		}

		fun delImage(index: Int) {
			synchronized(panel.treeLock) {
				if (index < panel.componentCount)
					panel.remove(index)
//				while (this.index >= index)
//					this.index--
			}
			render()
		}

		fun setImage(index: Int, image: BufferedImage) {
			synchronized(panel.treeLock) {
				if (index < 0)
					throw IndexOutOfBoundsException("index $index out of bounds for len ${panel.componentCount}")
				if (index >= panel.componentCount)
					addImage(image)
				else {
					val img = panel.getComponent(index) as ImagePanel
					img.applyImage(image)
				}
			}
			render()
		}

		fun goto(i: Int) {
			if (i < 0 || i >= panel.componentCount)
				throw IndexOutOfBoundsException("index $i out of bounds for len ${panel.componentCount}")
			index = i
			render()
		}
	}

	var card: Card? = null
		set(value) {
			if (value != null)
				populate(value.renderOpts)
			field = value
		}

	var editable: Boolean = false
		set(value) {
			fun recurse(component: Container) {
				for (child in component.components) {
					if (child is Container)
						recurse(child)
				}
				component.isEnabled = value
			}
			recurse(strokePanel)
			field = value
		}

	private val images: ImageCarousel

	private lateinit var strokeOrder: JCheckBox
	private lateinit var strokePanel: JPanel
	private lateinit var drawFull: JCheckBox
	private lateinit var orientation: ButtonGroup
	private lateinit var wrapAfter: JSpinner

	init {
		add(swing<JPanel> {
			panel {
				gridBagLayout {
					cell {
						attr {
							cons {
								gridx = 0
								gridy = 0
								weightx = 1.0
								weighty = .7
								fill = GridBagConstraints.BOTH
								anchor = GridBagConstraints.CENTER
							}
						}
						images = ImageCarousel(forcedHeight = INITIAL_HEIGHT)
						images.addComponentListener(object : ComponentAdapter() {
							override fun componentResized(e: ComponentEvent) {
								images.forcedHeight = Math.floorDiv(e.component.size.height, 50) * 50 // round to lowest multiple of 50
								images.render()
							}
						})
						add(images)
					}
					cell {
						attr {
							cons {
								gridx = 0
								gridy = 1
								weightx = 1.0
								weighty = .3
								fill = GridBagConstraints.HORIZONTAL
								anchor = GridBagConstraints.SOUTH
							}
						}
						strokePanel = panel {
							gridBagLayout {
								cell {
									attr {
										cons {
											gridx = 0
											gridy = 1
											weightx = 1.0
											weighty = 1.0
											fill = GridBagConstraints.NONE
											anchor = GridBagConstraints.WEST
											insets.apply { // 5 is default for FlowLayout (what the other rows use)
												left = 5
											}
										}
									}

									panel {
										flowLayout(FlowLayout.LEADING) {
											strokeOrder = checkBox("Show stroke order?", true) {
												attr {
													isEnabled = false
													horizontalTextPosition = SwingConstants.LEFT
												}

												onAction {
													isEnabled = false
													self.isSelected = true
//													kt.editor.modified = true
//													editable = self.isSelected
												}
											}

											button("Apply curriculum defaults") {
												attr {
													isEnabled = true
												}
												onAction {
													val k = card
													if (k != null) {
														k.setRenderOptsOverride(null)
														populate(k.location.curriculum.defaultRenderOpts)
														kt.editor.modified = true
													}
												}
											}
										}
									}
								}

								cell {
									attr {
										cons {
											gridx = 0
											gridy = 2
											weightx = 1.0
											weighty = 1.0
											fill = GridBagConstraints.NONE
											anchor = GridBagConstraints.WEST
											insets.apply {
												left = 10
											}
										}
									}

									panel {
										gridBagLayout {
											row(0) {
												panel {
													flowLayout(FlowLayout.LEADING) {
														drawFull = checkBox("Draw full kanji?", true) {
															attr {
																toolTipText = "Render the whole kanji in the first panel?"
																horizontalTextPosition = SwingConstants.LEFT
																addActionListener {
																	kt.editor.modified = true
																}
															}
														}
													}
												}
											}

											orientation = ButtonGroup()
											row(1) {
												panel {
													flowLayout(FlowLayout.LEADING) {
														label("Orientation:")
														radioButton("X", false, orientation) {
															attr {
																actionCommand = DiagramOptions.X
																horizontalTextPosition = SwingConstants.LEFT
																addActionListener {
																	kt.editor.modified = true
																}
															}
														}
														radioButton("Y", true, orientation) {
															attr {
																actionCommand = DiagramOptions.Y
																horizontalTextPosition = SwingConstants.LEFT
																addActionListener {
																	kt.editor.modified = true
																}
															}
														}
													}
												}
											}

											row(2) {
												panel {
													flowLayout(FlowLayout.LEADING) {
														label("DPI:")
														label("200") {
															attr {
																toolTipText = "Change in preferences."
															}
														}
													}
												}
											}

											row(3) {
												panel {
													flowLayout(FlowLayout.LEADING) {
														label("Wrap after")
														wrapAfter = spinner(SpinnerNumberModel(5, 1, Int.MAX_VALUE, 1)) {
															attr {
																preferredSize = Dimension(55, preferredSize.height)
																addChangeListener {
																	kt.editor.modified = true
																}
															}
														}
														label("panels.")
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}, BorderLayout.CENTER)

		populate()
		editable = false
	}

	val zoomFactor: Float
		get() = images.forcedHeight / INITIAL_HEIGHT.toFloat()

	fun gather(): Int = DiagramOptions.createBitmask(200, drawFull.isSelected, wrapAfter.value as Int, orientation.selection.actionCommand)

	fun populate(renderOpts: Int) {
		val diag = DiagramOptions.fromBitmask(renderOpts)

		drawFull.isSelected = diag.drawFullKanji
		wrapAfter.value = diag.wrapAt.toInt()
		orientation.selection.actionCommand = diag.orientation
		repaint()
	}

	fun populate(vararg imgs: BufferedImage) {
		if (imgs.isEmpty()) {
			card = null
			images.setImage(0, KCIcon.PREVIEW_EMPTY.primary.unedited())
			images.delImage(1)
		} else {
			if (imgs.size != 2)
				throw IllegalArgumentException("expected len 2, got: ${imgs.size}")

			images.setImage(0, imgs[0])
			images.setImage(1, imgs[1])
		}
		images.render()
		repaint()
	}
}