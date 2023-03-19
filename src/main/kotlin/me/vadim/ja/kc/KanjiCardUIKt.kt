package me.vadim.ja.kc

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.split2Pane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.ktswing.layout.cardLayout
import me.vadim.ja.kc.view.*
import me.vadim.ja.kc.wrapper.CurriculumManager
import me.vadim.ja.kc.wrapper.Kanji
import java.awt.CardLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.swing.JPanel
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

	val mgr: CurriculumManager = CurriculumManager.cringe
	var license: String by nonce()
	var version: String by nonce()
	var toolbar: Toolbar by nonce()
	var explorer: CurriculumExplorer by nonce()
	var editor: Editor by nonce()
	var preview: Preview by nonce()
	private var cardLayout: CardLayout by nonce()
	private var cardPanel: JPanel by nonce()

	fun populate(): Component {
		toolbar = Toolbar(this, License(frame, license))
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


	private val previewCache: Cache<Kanji, Array<BufferedImage>> = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build()
	fun postPreview(kanji: Kanji, useCached: Boolean = false) {
		val cached = previewCache.getIfPresent(kanji)

		val future =
			if (cached == null || !useCached)
				mgr.generatePreview(kanji, preview.gather())
			else
				CompletableFuture.completedFuture(cached)

		future.thenAccept {
			previewCache.put(kanji, it)
			preview.kanji = kanji
			preview.populate(*it)
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

	fun showEditor(kanji: Kanji? = null) {
		editor.preShow()
		if (kanji != null)
			editor.populate(kanji)
		preview.editable = true
		cardLayout.show(cardPanel, EDITOR)
	}

}