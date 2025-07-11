package me.vadim.ja.kc

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.split2Pane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.ktswing.layout.cardLayout
import me.vadim.ja.kc.model.LibraryContext
import me.vadim.ja.kc.model.wrapper.Card
import me.vadim.ja.kc.model.xml.KCFactory
import me.vadim.ja.kc.view.*
import me.vadim.ja.kc.view.dialog.About
import me.vadim.ja.kc.view.dialog.AuthorDialog
import me.vadim.ja.kc.view.dialog.License
import me.vadim.ja.kc.view.dialog.PrefDialog
import me.vadim.ja.kc.view.pane.Editor
import me.vadim.ja.kc.view.pane.Preview
import me.vadim.ja.kc.view.pane.CurriculumExplorer
import java.awt.CardLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

/**
 * @author vadim
 */
@Suppress("MemberVisibilityCanBePrivate")
class KanjiCardUIKt(val frame: KanjiCardUI) {

	companion object {

		private const val EDITOR = "Editor"
		private const val EXPLORER = "Explorer"
	}

	val ctx: LibraryContext = KCFactory.loadDefault()
	var license: String by nonce()
	var version: String by nonce()
	var author: AuthorDialog by nonce()
	var pref: PrefDialog by nonce()
	var toolbar: Toolbar by nonce()
	var explorer: CurriculumExplorer by nonce()
	var editor: Editor by nonce()
	var preview: Preview by nonce()
	private var cardLayout: CardLayout by nonce()
	private var cardPanel: JPanel by nonce()

	fun populate(): Component {
		ctx.activeLibrary.author

		author = AuthorDialog(ctx.activeLibrary, this)
		pref = PrefDialog(this)
		toolbar = Toolbar(this, License(frame, license), About(frame, version))
		frame.jMenuBar = toolbar
//		ui.add(KCUI.tabPane(ui), BorderLayout.CENTER)
		editor = Editor(this)
		preview = Preview(this)

		explorer = CurriculumExplorer(this)

		return swing {
			split2Pane {
				left {
					cardPanel = panel {
						cardLayout = cardLayout {
							card(EDITOR) {
								add(editor)
							}
							card(EXPLORER) {
								add(explorer)
							}
							show(EXPLORER)
						}
						attr { // attr block must be after
							val s = preferredSize
							preferredSize = Dimension((s.width * 1.1).roundToInt(), (s.height * 1.1).roundToInt())
							minimumSize = s
						}
					}
				}
				right {
					panel {
						borderLayoutCenter {
							add(preview)
						}
					}
				}
			}.apply {
				setDividerLocation(.75)
			}
		}
	}


	fun postPreview(kanji: Card, useCached: Boolean = false) {
		val job = ctx.renderContext.cachedPreview(kanji).withOpts(kanji.renderOpts).zoomTo(preview.zoomFactor)

		val future =
			if (useCached)
				job.result
			else
				job.recompute()

		future.thenAccept {
			SwingUtilities.invokeLater {
				preview.card = kanji
				preview.populate(*it)
			}
		}
	}

	fun showExplorer() {
		editor.preHide()
		editor.gather()
		explorer.repopulate()
		preview.populate()
		preview.editable = false
		cardLayout.show(cardPanel, EXPLORER)
	}

	fun showEditor(kanji: Card? = null) {
		editor.preShow()
		if (kanji != null)
			editor.populate(kanji)
		preview.editable = true
		cardLayout.show(cardPanel, EDITOR)
	}

	fun showFirstLaunch() {
		if (ctx.activeLibrary.author == null)
			author.display()

		if (ctx.preferences.kvg_dir == null)
			pref.display()
	}

	fun shutdown() {
		ctx.savePreferences()
		ctx.saveLibrary(true)
		author.dispose()
		toolbar.dispose()
		ctx.shutdown()
	}
}