package me.vadim.ja.kc.view

import io.github.mslxl.ktswing.BasicScope
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.*
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.*
import me.vadim.ja.kc.wrapper.*
import me.vadim.ja.swing.SortedComboBoxModel
import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.text.Document
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author vadim
 */
class Editor(private val kt: KanjiCardUIKt) : JPanel() {
	@OptIn(ExperimentalContracts::class)
	private inline fun CanAddChildrenScope<*>.textField(
		doc: Document? = null,
		text: String? = null,
		column: Int = 0,
		block: BasicScope<JTextField>.() -> Unit
													   ): JTextField {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		return applyContainer(
			JTextField(doc, text, column)
				.apply {
					addKeyListener(object : KeyAdapter() {
						override fun keyReleased(e: KeyEvent) {
							if (e.isControlDown && e.keyCode == KeyEvent.VK_A) {
								this@apply.selectAll()
								e.consume()
							}
						}

						override fun keyTyped(e: KeyEvent) {
							if (e.isControlDown)
								if (e.keyChar == 'S') // don't modify on save
									return
							modified = true
						}
					})
				}, block
							 )
	}

	private val frame = kt.frame as JFrame

	private fun JTextField.removeWhitespace() {
		text = text.replace(Regex("\\s+"), "")
	}

	private lateinit var saveIndicator: JLabel

	private var showSaveIndicator = false
		set(value) {
			if (::saveIndicator.isInitialized) {
				saveIndicator.text = if (value) "*" else ""
				frame.revalidate()
				frame.repaint()
			}
			field = value
		}

	var modified = true
		set(value) {
			if (value)
				showSaveIndicator = true
			field = value
		}

	private lateinit var kanjiArea: JTextField
	private lateinit var curriculumSelector: JComboBox<Curriculum>
	private lateinit var groupSelector: JComboBox<Group>

	private lateinit var pronounciations: ResizableInputList<Pronounciation?>
	private lateinit var grammar: ResizableInputList<PartOfSpeech?>
	private lateinit var definitions: ResizableInputList<Definition?>

	private val timer = Timer(1000) {
		if (modified && kanji.hasId()) { // await id initialization from db... besides it's empty upon creation
			modified = false
			post()
		}
	}

	fun preShow() {
		if (!timer.isRunning)
			timer.start()
	}

	fun preHide() {
		if (timer.isRunning)
			timer.stop()
	}

	fun save() {
		modified = true
		showSaveIndicator = false
		kt.mgr.save(gather())
	}

	init {
		// timer not started: the UI does not open to this screen.
		// therefore, preShow() will be called before a card switch
		// and the timer will be started accordingly
		layout = BorderLayout()
		add(swing<JPanel> {
			panel {
				attr {
					registerKeyboardAction(
						{
							save()
						},
						"Save",
						KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx),
						JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
										  )
					border = TitledBorder(null, "Card Editor", TitledBorder.TOP, TitledBorder.CENTER)
				}

				gridBagLayout {
					row(0) {
						panel {
							borderLayout {
								left {
									button(KCIcon.BACK.primary.withSize(25, 25).asIcon()) {
										attr {
											size = Dimension(35, 35)
											preferredSize = size
											minimumSize = size
											maximumSize = size
										}
										onAction {
											// Go back
											kt.showExplorer()
										}
									}
								}
								right {
									saveIndicator = label("") {
										attr {
											font = font.deriveFont(25f)
										}
									}
								}
							}
						}
					}
					row(1) {
						panel {
							flowLayout {
								val curriculums = kt.mgr.curriculums
								label("Curriculum")
								curriculumSelector = comboBox(curriculums) {
									attr {
										addActionListener {
											modified = true
											groupSelector.removeAllItems()
											for (group in (self.selectedItem as Curriculum?)?.groups ?: emptySet())
												groupSelector.addItem(group)
										}
									}
								}
								label("Group")
								groupSelector = comboBox(emptyList()) {
									attr {
										addActionListener {
											modified = true
										}
									}
								}
							}
						}
					}

					row(2) {
						panel {
							flowLayout(FlowLayout.LEADING) {
								label("Kanji")
								kanjiArea = textField {
									attr {
										font = KCTheme.JP_FONT
										var needsCaching = false
										addKeyListener(object : KeyAdapter() {
											override fun keyTyped(e: KeyEvent) {
												if (!e.isActionKey)
													needsCaching = true
											}
										})
										addFocusListener(object : FocusAdapter() {
											override fun focusLost(e: FocusEvent?) {
												if (needsCaching) {
													kt.mgr.cacheImgs(gather())
													needsCaching = false
												}
											}
										})
									}
								}
							}
						}
					}

					row(3) {
						panel {
							gridBagLayout {
								pronounciations = resizableInputList<Pronounciation?>(frame, "Pronounciations") {
									textField {
										attr {
											columns = 10
											font = KCTheme.JP_FONT
											text = it?.value
										}
									}
									comboBox(enumValues<PronounciationType>().toList()) {
										attr {
											selectedItem = it?.type ?: PronounciationType.UNKNOWN
											addActionListener {
												modified = true
											}
										}
									}
								}
							}
						}
					}

					row(4) {
						panel {
							gridBagLayout {
								grammar = resizableInputList(frame, "Grammar") {
									val info = comboBox<PartOfSpeech.Info>(kt.mgr.availableInfos(it)) {
										attr {
											selectedItem = it?.info
											isEnabled = selectedItem != null
											addActionListener {
												modified = true
											}
										}
									}
									comboBox<PartOfSpeech>(
										SortedComboBoxModel(kt.mgr.partsOfSpeechDistinct().toTypedArray(), Comparator.comparingInt(PartOfSpeech::getPriority)))
									{
										attr {
											println(kt.mgr.partsOfSpeechDistinct())
											selectedItem = it
											if(it == null) // we cannot have a null row here
												selectedIndex = 0
											addActionListener {
												modified = true
											}
											addItemListener { e ->
												val pos = e.item as PartOfSpeech
												info.removeAllItems()
												for (i in CurriculumManager.cringe.availableInfos(pos))
													info.addItem(i)
												info.isEnabled = info.itemCount > 0
												frame.revalidate()
											}
										}
									}
								}
							}
						}
					}

					row(5) {
						panel {
							gridBagLayout {
								definitions = resizableInputList(frame, "Definitions") {
									textField {
										attr {
											columns = 25
											font = KCTheme.JP_FONT
											text = it?.value
										}
									}
								}
							}
						}
					}

					row(6) {
						panel {
							flowLayout {
								button("Save") {
									onAction {
										save()
									}
								}
								button("Export") {
									onAction {
										kt.mgr.submitAsync(gather(), kt.preview.gather())
									}
								}
								button("Preview") {
									onAction {
										post()
									}
								}
							}
						}
					}
				}
			}
		}, BorderLayout.CENTER)
	}

	private fun post() {
		println("Regenerating preview.")
//		kt.preview.populate(*kt.mgr.preview(gather(), kt.preview.gather()))
		kt.postPreview(gather())
	}

	private lateinit var kanji: Kanji

	fun populate(k: Kanji) {
		kanji = k
		kanjiArea.text = k.value

		curriculumSelector.removeAllItems()
		kt.mgr.curriculums.forEach { curriculumSelector.addItem(it) }
		curriculumSelector.selectedItem = k.curriculum

		groupSelector.removeAllItems()
		k.curriculum.groups.forEach { groupSelector.addItem(it) }
		groupSelector.selectedItem = k.group

		//pronounciations
		pronounciations.clear()
		for (pron in k.getPronounciations().apply {
			if (isEmpty())
				add(Pronounciation.EMPTY)
		})
			pronounciations.createLine(pron)

		//grammar
		grammar.clear()
		for (pos in k.getPartsOfSpeech().apply {
			if (isEmpty())
				add(kt.mgr.partsOfSpeech()[0])
		})
			grammar.createLine(pos)

		//definitions
		definitions.clear()
		for (def in k.getDefinitions().apply {
			if (isEmpty())
				add(Definition.EMPTY)
		})
			definitions.createLine(def)
		frame.revalidate()
		frame.repaint()
	}

	@Suppress("UNCHECKED_CAST")
	fun gather(): Kanji {
		kanjiArea.removeWhitespace()
		val k = kanji.copy().value(kanjiArea.text).group(groupSelector.selectedItem as Group).id(kanji.id).build()
		//pronounciations
		k.pronounciations.clear()
		for (elem in pronounciations.components) {
			elem as JPanel

			val input = elem.components.filterIsInstance<JTextField>().first().apply { removeWhitespace() }
			val type = elem.components.filterIsInstance<JComboBox<PronounciationType>>().first()

			k.addPronounciation(type.selectedItem as PronounciationType, input.text)
		}
		//grammar
		k.partsOfSpeech.clear()
		val pos = mutableListOf<PartOfSpeech>()
		for (elem in grammar.components) {
			elem as JPanel

			val info = elem.components[0]!! as JComboBox<PartOfSpeech.Info>
			val type = elem.components[1]!! as JComboBox<PartOfSpeech>

			pos += (info.selectedItem as PartOfSpeech.Info?)?.partOfSpeech ?: (type.selectedItem as PartOfSpeech) // choose more specific PoS if applicable
		}
		pos.distinctBy { it.name }.sortedBy { it.priority }.forEach { k.addPartOfSpeech(it) }
		//definitions
		k.definitions.clear()
		for (elem in definitions.components) {
			elem as JPanel

			val input = elem.components.first() as JTextField
			k.addDefinition(input.text)
		}
		frame.revalidate()
		frame.repaint()

		kanji = k
		return k
	}
}