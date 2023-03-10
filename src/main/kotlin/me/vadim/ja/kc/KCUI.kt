package me.vadim.ja.kc

import io.github.mslxl.ktswing.*
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.SwingComponentBuilderScope
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.*
import me.vadim.ja.kc.wrapper.Curriculum
import me.vadim.ja.kc.wrapper.CurriculumManager
import me.vadim.ja.kc.wrapper.Group
import me.vadim.ja.kc.wrapper.PartOfSpeech
import me.vadim.ja.kc.wrapper.PronounciationType
import me.vadim.ja.swing.SortedComboBoxModel
import me.vadim.ja.swing.TransferFocus
import java.awt.*
import java.util.*
import java.util.function.Consumer
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.text.Document
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible


/**
 * @author vadim
 */
object KCUI {

	@JvmStatic
	fun JButton.hover(enter: Consumer<JButton>, exit: Consumer<JButton>) {
		//border does not work with system theme
//		border = RoundedBorder(3)
//		isContentAreaFilled = false
		border = null
		isFocusPainted = false
		background = null
		model.addChangeListener {
			val model = it.source as ButtonModel
			if (model.isRollover)
				enter.accept(this)
			else
				exit.accept(this)
		}
	}

	fun JTextField.removeWhitespace() {
		text = text.replace(Regex("\\s+"), "")
	}

	//override textField dsl method to apply TransferFocus patch automatically
	@OptIn(ExperimentalContracts::class)
	inline fun CanAddChildrenScope<*>.textField(
		doc: Document? = null,
		text: String? = null,
		column: Int = 0,
		block: BasicScope<JTextField>.() -> Unit
											   ): JTextField {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		return applyContainer(JTextField(doc, text, column).also { TransferFocus.patch(it) }, block)
	}


	//erases tooltip text
	@JvmStatic
	fun debugBorders(component: Container, enable: Boolean, original: Boolean = true) {
		for (child in component.components) {
			if (child is JComponent) {
				debugBorders(child, enable, false)
				if (enable) {
					if (child != null && child.border == null) {
						child.toolTipText = "debug"
						child.border = TitledBorder(
							try {
								child?.layout::class.java
							} catch (ignored: Exception) {
								child::class.java
							}?.simpleName ?: "null"
												   )
					}
				} else
					if (child.toolTipText == "debug") {
						child.border = null
						child.toolTipText = null
					}
			}
		}
		if (original)
			component.revalidate()
	}

	private lateinit var tabs: JTabbedPane

	private val tabIcons = listOf(KCIcon.EDIT, KCIcon.LIST, KCIcon.SETTINGS)

	@JvmStatic
	fun tabPane(frame: JFrame) = swing<JTabbedPane> {
		tabs = tabbedPane {
			editTab(frame)
			organizeTab(frame)
			configureTab(frame)
			attr {
				tabPlacement = JTabbedPane.BOTTOM

				fun populateIcons() {
					//populate icons
					for ((i, icon) in tabIcons.withIndex())
						setIconAt(i, KCTheme.getButtonIcon(
							if (i == self.selectedIndex) // current tab; set to selected icon (if present0
								icon.let { it?.secondary ?: it.primary }
							else // normal tab; set to default icon
								icon.primary))
				}

				populateIcons()
				addChangeListener {
					populateIcons()
				}
			}
		}
	}

	private const val WEIGHT_LEFT = 2
	private const val WEIGHT_RIGHT = 1

	private fun CanAddChildrenScope<*>.previewPane() {
		panel {
			attr {
				border = TitledBorder("Preview")
			}
			borderLayout {
				center {
					label("") {
						self.icon = KCIcon.LOGO.primary.asIcon()
					}
				}
			}
		}
	}


	private fun SwingComponentBuilderScope<Component>.splitPreview(name: String, scope: CanSetLayoutScope<JPanel>.() -> Unit) {
		panel {
			gridBagLayout {
				cell { // right side
					attr {
						cons {
							gridx = 1
							weightx = WEIGHT_RIGHT.toDouble()
							weighty = 1.0
							anchor = GridBagConstraints.EAST
							fill = GridBagConstraints.BOTH
						}
//						preferredSize = Dimension(300, height)
//						minimumSize = Dimension(500, 500)
					}

					previewPane()
				}

				cell { // left side
					attr {
						cons {
							gridx = 0
							weightx = WEIGHT_LEFT.toDouble()
							weighty = 1.0
							anchor = GridBagConstraints.WEST
							fill = GridBagConstraints.BOTH
						}
					}

					panel {
						attr {
							border = TitledBorder(name)
						}
						gridBagLayout {
							cell {
								attr {
									cons {
										gridx = 0
										weightx = 1.0
										weighty = 1.0
										anchor = GridBagConstraints.NORTH
										fill = GridBagConstraints.HORIZONTAL
									}
								}

								panel { // nested so the parent expands and has a border, but the center one grows with the elements (no border)
									scope()
								}
							}
						}
					}
				}
			}
		}
	}

	//todo: add scroll wheel if theres too many
	private fun GridBagLayoutRootScope<*>.resizableInputList(
		frame: JFrame,
		label: String,
		property: KMutableProperty<JPanel>,
		eachRow: CanAddChildrenScope<*>.() -> Unit,
		scope: GridBagLayoutCellScope<*>.() -> Unit
															) {
		property.apply {
			isAccessible = true
		}
		fun createLine() {
			val list = property.getter.call()
			list.add(swing<JPanel> {
				panel {
					flowLayout(FlowLayout.LEFT) {
						eachRow()
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

		fun deleteLine() {
			val list = property.getter.call()
			if (list.componentCount > 1)
				list.remove(list.componentCount - 1)
			frame.revalidate()
		}

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
			scope()
			createLine()
		}
	}

	private lateinit var kanjiArea: JTextField
	private lateinit var curriculumSelector: JComboBox<Curriculum>
	private lateinit var groupSelector: JComboBox<Group>

	private lateinit var pronounciations: JPanel
	private lateinit var grammar: JPanel
	private lateinit var definitions: JPanel
	private lateinit var strokeOrder: JCheckBox
	private lateinit var strokePanel: JPanel
	private lateinit var orientation: ButtonGroup
	private lateinit var wrapAfter: JSpinner

	@Suppress("UNCHECKED_CAST")
	private fun TabbedPaneScope.editTab(frame: JFrame) {
		tab("Edit") {
			splitPreview("Flashcard Editor") {
				fun GridBagLayoutRootScope<*>.row(y: Int, scope: GridBagLayoutCellScope<*>.() -> Unit) {
					cell {
						attr {
							cons {
								anchor = GridBagConstraints.NORTH
								gridx = 0
								gridy = y
								weightx = 1.0
								weighty = 1.0
								fill = GridBagConstraints.HORIZONTAL
							}
						}

						scope()
					}
				}

				gridBagLayout {
					row(0) {
						panel {
							flowLayout(FlowLayout.LEFT) {
								val genki = CurriculumManager.cringe.genki()
								label("Curriculum")
								curriculumSelector = comboBox(listOf(genki)) {}
								label("Group")
								groupSelector = comboBox(genki.groups.toList()) {}
							}
						}
					}

					row(1) {
						panel {
							flowLayout(FlowLayout.LEFT) {
								label("Kanji")
								kanjiArea = textField {
									attr {
										font = KCTheme.JP_FONT
									}
								}
							}
						}
					}

					row(2) {
						panel {
							gridBagLayout {
								resizableInputList(frame, "Pronounciations", KCUI::pronounciations, {
									textField {
										attr {
											columns = 10
											font = KCTheme.JP_FONT
										}
									}
									comboBox(enumValues<PronounciationType>().toList()) {
									}
								}) {
									pronounciations = panel {
										gridBagLayout {}
									}
								}
							}
						}
					}

					row(3) {
						panel {
							gridBagLayout {
								resizableInputList(frame, "Grammar", KCUI::grammar, {
									val info = comboBox<PartOfSpeech.Info>(emptyList()) {
										attr {
											isEnabled = false
										}
									}
									comboBox<PartOfSpeech>(
										SortedComboBoxModel(CurriculumManager.cringe.partsOfSpeechDistinct().toTypedArray(),
															Comparator.comparingInt<PartOfSpeech> { it.priority }))
									{
										self.addItemListener {
											val pos = it.item as PartOfSpeech
											info.removeAllItems()
											for (i in CurriculumManager.cringe.availableInfos(pos))
												info.addItem(i)
											info.isEnabled = info.itemCount > 0
											frame.revalidate()
										}
									}
								}) {
									grammar = panel {
										gridBagLayout {}
									}
								}
							}
						}
					}

					row(4) {
						panel {
							gridBagLayout {
								resizableInputList(frame, "Definitions", KCUI::definitions, {
									textField {
										attr {
											columns = 25
											font = KCTheme.JP_FONT
										}
									}
								}) {
									definitions = panel {
										gridBagLayout {}
									}
								}
							}
						}
					}

					row(5) {
						button("Apply") {
							onAction {
								kanjiArea.removeWhitespace()
								val k = CurriculumManager.cringe.createKanji(kanjiArea.text, groupSelector.selectedItem as Group)
								//pronounciations
								for (elem in pronounciations.components) {
									elem as JPanel

									val input = elem.components.filterIsInstance<JTextField>().first().apply { removeWhitespace() }
									val type = elem.components.filterIsInstance<JComboBox<PronounciationType>>().first()

									k.addPronounciation(type.selectedItem as PronounciationType, input.text)
								}
								//grammar
								val pos = mutableListOf<PartOfSpeech>()
								for (elem in grammar.components) {
									elem as JPanel

									val info = elem.components[0]!! as JComboBox<PartOfSpeech.Info>
									val type = elem.components[1]!! as JComboBox<PartOfSpeech>

									pos += (info.selectedItem as PartOfSpeech.Info?)?.partOfSpeech ?: (type.selectedItem as PartOfSpeech)
								}
								pos.distinctBy { it.name }.sortedBy { it.priority }.forEach { k.addPartOfSpeech(it) }
								//definitions
								for (elem in definitions.components) {
									elem as JPanel

									val input = elem.components.first() as JTextField
									k.addDefinition(input.apply { removeWhitespace() }.text)
								}

								frame.revalidate()
								CurriculumManager.cringe.submit(k)
							}
						}
					}

					row(6) {
						panel {
							gridBagLayout {
								cell {
									attr {
										cons {
											gridx = 0
											gridy = 0
											weightx = 1.0
											weighty = 1.0
											fill = GridBagConstraints.NONE
											anchor = GridBagConstraints.WEST
											insets.apply { // 5 is default for FlowLayout (what the other rows use)
												left = 5
											}
										}
									}

									strokeOrder = checkBox("Show stroke order?", true) {
										attr {
											horizontalTextPosition = SwingConstants.LEFT
										}

										onAction {
											val enable = self.isSelected
											fun recurse(component: Container) {
												for (child in component.components) {
													if (child is Container)
														recurse(child)
												}
												component.isEnabled = enable
											}
											recurse(strokePanel)
										}
									}
								}

								cell {
									attr {
										cons {
											gridx = 0
											gridy = 1
											weightx = 1.0
											weighty = 1.0
											fill = GridBagConstraints.NONE
											anchor = GridBagConstraints.WEST
											insets.apply {
												left = 10
											}
										}
									}

									strokePanel = panel {
										gridBagLayout {
											orientation = ButtonGroup()
											row(0) {
												panel {
													flowLayout(FlowLayout.LEFT) {
														label("Orientation:")
														radioButton("X", false, orientation) {
															attr {
																horizontalTextPosition = SwingConstants.LEFT
															}
														}
														radioButton("Y", true, orientation) {
															attr {
																horizontalTextPosition = SwingConstants.LEFT
															}
														}
													}
												}
											}

											row(1) {
												panel {
													flowLayout(FlowLayout.LEFT) {
														label("DPI:")
														label("200") {
															attr {
																toolTipText = "Change in 'Configure' tab."
															}
														}
													}
												}
											}

											row(2) {
												panel {
													flowLayout(FlowLayout.LEFT) {
														label("Wrap after")
														wrapAfter = spinner(SpinnerNumberModel(5, 1, Int.MAX_VALUE, 1)) {
															attr {
																preferredSize = Dimension(55, preferredSize.height)
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
		}
	}

	private fun TabbedPaneScope.organizeTab(frame: JFrame) {
		tab("Organize") {
			splitPreview("Group Organizer") {
				gridBagLayout {
					cell {
						attr {
							cons {
								anchor = GridBagConstraints.NORTH
								gridx = 0
								gridy = 0
								weightx = 1.0
								weighty = 1.0
								fill = GridBagConstraints.HORIZONTAL
							}
						}

						panel {
							flowLayout {
								label("Test")
							}
						}
					}
				}
			}
		}
	}

	private fun TabbedPaneScope.configureTab(frame: JFrame) {
		tab("Configure") {
			panel {
				borderLayout {
					center {
						label("settings go here")
					}
				}
			}
		}
	}

}