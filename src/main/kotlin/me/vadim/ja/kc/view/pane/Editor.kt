package me.vadim.ja.kc.view.pane

import com.google.common.hash.HashCode
import io.github.mslxl.ktswing.BasicScope
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.*
import io.github.mslxl.ktswing.onAction
import me.vadim.ja.kc.*
import me.vadim.ja.kc.persist.EnumeratedItem
import me.vadim.ja.kc.persist.LinguisticElement
import me.vadim.ja.kc.persist.PartOfSpeech
import me.vadim.ja.kc.persist.PronounciationType
import me.vadim.ja.kc.persist.SpokenElement
import me.vadim.ja.kc.persist.impl.Location
import me.vadim.ja.kc.persist.wrapper.Card
import me.vadim.ja.kc.persist.wrapper.Curriculum
import me.vadim.ja.kc.persist.wrapper.Group
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

	private lateinit var pronounciations: ResizableInputList<SpokenElement?>
	private lateinit var grammar: ResizableInputList<PartOfSpeech?>
	private lateinit var definitions: ResizableInputList<LinguisticElement?>

	private val timer = Timer(1000) {
		if (modified) { // huh
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
		if(last.code != card.hash() || card.location != last) // if the card has moved, then delete the previous one
			kt.ctx.delete(last.code, last)
		val k = gather()
		card = k
		last = CardLocation(k.hash(), k.location)
		kt.ctx.save(k)
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
								val curriculums = kt.ctx.activeLibrary.curriculums
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
													kt.ctx.cacheImgs(gather())
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
								pronounciations = resizableInputList<SpokenElement?>(frame, "Pronounciations") {
									textField {
										attr {
											columns = 10
											font = KCTheme.JP_FONT
											text = it?.describe()
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
									val variant = comboBox<EnumeratedItem<String>>(it?.variants ?: emptyList()) {
										attr {
											selectedItem = it?.variants?.firstOrNull()
											isEnabled = selectedItem != null
											addActionListener {
												modified = true
											}
										}
									}
									comboBox<PartOfSpeech>(
										SortedComboBoxModel(PartOfSpeech.values(), Comparator.comparingInt(PartOfSpeech::ordinal))
														  )
									{
										attr {
											selectedItem = it
											if (it == null) // we cannot have a null row here
												selectedIndex = 0
											addActionListener {
												modified = true
											}
											addItemListener { e ->
												val pos = e.item as PartOfSpeech
												variant.removeAllItems()
												for (i in pos.variants)
													variant.addItem(i)
												variant.isEnabled = variant.itemCount > 0
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
											text = it?.describe()
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
										kt.ctx.submitAsync(gather(), kt.preview.gather())
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

	// do not override equals
	private class CardLocation(val code: HashCode, location: Location) : Location(location.curriculum, location.group)

	private lateinit var card: Card
	private lateinit var last: CardLocation

	fun populate(k: Card) {
		card = k
		last = CardLocation(k.hash(), k.location)
		kanjiArea.text = k.describeJapanese()

		curriculumSelector.removeAllItems()
		kt.ctx.activeLibrary.curriculums.forEach { curriculumSelector.addItem(it) }
		curriculumSelector.selectedItem = k.location.curriculum

		groupSelector.removeAllItems()
		k.location.curriculum.groups.forEach { groupSelector.addItem(it) }
		groupSelector.selectedItem = k.location.group

		//definitions
		definitions.clear()
		for (def in k.english.let {
			if (it.isEmpty())
				k.setEnglish("")
			k.english
		})
			definitions.createLine(def)

		//grammar
		grammar.clear()
		for (pos in k.grammar.let {
			if (it.isEmpty())
				k.setGrammar(PartOfSpeech.NOUN.asLinguistic())
			k.grammar
		})
			grammar.createLine(PartOfSpeech.fromLinguistic(pos))

		//pronounciations
		pronounciations.clear()
		for (pron in k.spoken.let {
			if (it.isEmpty())
				k.setSpoken(PronounciationType.UNKNOWN.toSpoken(""))
			k.spoken
		})
			pronounciations.createLine(pron)

		frame.revalidate()
		frame.repaint()
	}

	@Suppress("UNCHECKED_CAST")
	fun gather(): Card {
		kanjiArea.removeWhitespace()
		val k = card
		k.setJapanese(kanjiArea.text)
		k.location = Location(curriculumSelector.selectedItem as Curriculum, groupSelector.selectedItem as Group)

		//definitions
		val def = mutableListOf<String>()
		for (elem in definitions.components) {
			elem as JPanel

			val input = elem.components.first() as JTextField
			def += input.text
		}
		k.setEnglish(*def.toTypedArray())

		//grammar
		val pos = mutableMapOf<PartOfSpeech, LinguisticElement>()
		for (elem in grammar.components) {
			elem as JPanel

			val variant = elem.components[0]!! as JComboBox<EnumeratedItem<String>>
			val part = (elem.components[1]!! as JComboBox<PartOfSpeech>).selectedItem as PartOfSpeech

			// choose more specific PoS if applicable
			pos[part] = part.asLinguistic((variant.selectedItem as EnumeratedItem<String>?)?.index ?: PartOfSpeech.NO_VARIANT)
		}
		k.setGrammar(*pos.keys.distinctBy { it.name }.sortedBy { it.ordinal }.map { pos[it] }.toTypedArray())

		//pronounciations
		val pron = mutableListOf<SpokenElement>()
		for (elem in pronounciations.components) {
			elem as JPanel

			val input = elem.components.filterIsInstance<JTextField>().first().apply { removeWhitespace() }
			val type = elem.components.filterIsInstance<JComboBox<PronounciationType>>().first()

			pron += (type.selectedItem as PronounciationType).toSpoken(input.text)
		}
		k.setSpoken(*pron.toTypedArray())

		frame.revalidate()
		frame.repaint()

		card = k
		return k
	}
}