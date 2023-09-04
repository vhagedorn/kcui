package me.vadim.ja.kc.view.pane

import com.formdev.flatlaf.util.SystemInfo
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.comboBox
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.flowLayout
import me.vadim.ja.kc.KanjiCardUIKt
import me.vadim.ja.kc.model.PartOfSpeech
import me.vadim.ja.kc.model.xml.KCFactory
import me.vadim.ja.kc.model.wrapper.Card
import me.vadim.ja.kc.model.wrapper.Curriculum
import me.vadim.ja.kc.model.wrapper.Group
import me.vadim.ja.kc.util.Util
import me.vadim.ja.kc.view.HistoryCtx
import me.vadim.ja.swing.NaturalOrderComparator
import me.vadim.ja.swing.SimpleTreeNode
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.event.*
import java.util.Objects
import javax.swing.*
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.*

// todo: locate ungrouped (or invalidly grouped, or detached) cards, and put them in a special curriculum (or group)

/**
 * @author vadim
 */
class CurriculumExplorer(private val kt: KanjiCardUIKt) : JPanel(BorderLayout()) {

	class Ctx(
		central: JComponent,
		edit: ((ActionEvent) -> Unit)? = null,
		new: ((ActionEvent) -> Unit)? = null,
		export: ((ActionEvent) -> Unit)? = null,
		delete: ((ActionEvent) -> Unit)? = null,
			 ) : HistoryCtx(central) {

		var edit: JMenuItem? = null
			private set

		var new: JMenuItem? = null
			private set

		var export: JMenuItem? = null
			private set

		var delete: JMenuItem? = null
			private set

		init {
			//reverse because of zero-index insert
			if (delete != null)
				this.delete = add(JMenuItem("Delete").apply {
					accelerator = DELETE_KEY
					addActionListener {
						delete(it)
					}
				}, 0) as JMenuItem
			if (export != null)
				this.export = add(JMenuItem("Export").apply {
					addActionListener {
						export(it)
					}
				}, 0) as JMenuItem
			if (new != null)
				this.new = add(JMenuItem("New").apply {
					accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx)
					addActionListener {
						new(it)
					}
				}, 0) as JMenuItem
			if (edit != null)
				this.edit = add(JMenuItem("Edit").apply {
					accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK)
					addActionListener {
						edit(it)
					}
				}, 0) as JMenuItem
		}
	}

	private fun newCurriculum() {
		// meh
	}

	private fun newGroup(curriculum: Curriculum) {
		val name = JOptionPane.showInputDialog(
			this@CurriculumExplorer,
			"Enter the name of the group:", "New Group",
			JOptionPane.QUESTION_MESSAGE
											  ) ?: return
		curriculum.addGroup(name)
		populate(curriculum)
	}

	private fun newCard(group: Group) {
		kt.showEditor(kt.ctx.activeLibrary.createCard(group.toLocation()))
		populate(group.curriculum)
	}

	private fun createAction(comp: Any) {
		when (comp) {
			is Curriculum -> newGroup(comp)
			is Group      -> newCard(comp)
			else          -> System.err.println("w: Illegal ctx menu access (`New` for class ${comp.javaClass.canonicalName})")
		}
		kt.ctx.saveLibrary(false)
	}

	private fun selectAction(comp: Any, edit: Boolean, toggle: Boolean = false) {
		if (edit) {
			println("> Edit $comp")
			kt.ctx.saveLibrary(false)
		}
		if (comp !is Card) {
			val path = tree.selectionPath
			if (toggle)
				if (tree.isCollapsed(path) || (path?.lastPathComponent as DefaultMutableTreeNode?)?.isRoot == true) // never collapse root node
					tree.expandPath(path)
				else
					tree.collapsePath(path)
			kt.preview.populate()
		}
		when (comp) {
			is Card       ->
				if (edit || toggle)
					kt.showEditor(comp)
				else
					kt.postPreview(comp, true)

			is Group      ->
				if (edit) {
					val name = JOptionPane.showInputDialog(
						this@CurriculumExplorer,
						"Enter the name of the group:", "Rename Group",
						JOptionPane.INFORMATION_MESSAGE
														  ) ?: return
					comp.name = name
					populate(comp.curriculum)
				}

			is Curriculum ->
				if (edit) {
					val name = JOptionPane.showInputDialog(
						this@CurriculumExplorer,
						"Enter the name of the curriculum:", "Rename Curriculum",
						JOptionPane.INFORMATION_MESSAGE
														  ) ?: return
					comp.name = name
					populate(comp)
				}
		}
	}

	private fun deleteAction(comp: Any) {
		val resp = JOptionPane.showConfirmDialog(
			this,
			"Are you sure?\n" +
					"This CANNOT be undone with ${KeyEvent.getModifiersExText(KeyEvent.CTRL_DOWN_MASK)}+Z!",
			"Confirm deletion " + (if (comp.toString().isBlank()) "" else "of $comp"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
												)

		println("DELETE $comp ? ${if (resp == JOptionPane.YES_OPTION) "yes" else "no"}")

		if (resp == JOptionPane.YES_OPTION)
			when (comp) {
				is Card       -> kt.ctx.delete(comp)
				is Group      -> kt.ctx.delete(comp)
				is Curriculum -> kt.ctx.delete(comp)
			}
		repopulate()
		kt.ctx.saveLibrary(false)
	}

	private val ctx: Ctx
	val tree: JTree = JTree(DefaultMutableTreeNode()).apply {
		addTreeSelectionListener {
			selectAction(selectedObject ?: return@addTreeSelectionListener, false)
		}
		addKeyListener(object : KeyAdapter() {
			override fun keyPressed(e: KeyEvent) {
				if (e.keyCode == KeyEvent.VK_ENTER && !e.isControlDown && !e.isAltDown)
					selectAction(selectedObject ?: return, false, toggle = true)
				if (e.keyCode == DELETE_KEY.keyCode && e.modifiersEx == DELETE_KEY.modifiers)
					deleteAction(selectedObject ?: return)
			}
		})
		addTreeWillExpandListener(object : TreeWillExpandListener {
			override fun treeWillExpand(event: TreeExpansionEvent) {}
			override fun treeWillCollapse(event: TreeExpansionEvent) {
				if ((event.path.lastPathComponent as DefaultMutableTreeNode).isRoot)
					throw ExpandVetoException(event)
			}
		})
	}

	private companion object {

		private val NATTY = NaturalOrderComparator()

		private val PLACEHOLDER_CURRICULUM = KCFactory.newLibrary("").getCurriculum("New...")

		/* Del or CMD+Delete (delete==backspace on MacOS)*/
		private val DELETE_KEY = KeyStroke.getKeyStroke(
			if (SystemInfo.isMacOS) KeyEvent.VK_BACK_SPACE else KeyEvent.VK_DELETE,
			if (SystemInfo.isMacOS) Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx else 0
													   )
	}

	private lateinit var curriculumSelector: JComboBox<Curriculum>
	private lateinit var createGroupButton: JButton

	private val selectedObject: Any?
		get() = (tree.selectionPath?.lastPathComponent as DefaultMutableTreeNode?)?.userObject

	private var initd = false // hacky hack hack

	init {
		ctx = Ctx(this, new = { // right click -> context menu -> action
			createAction(selectedObject ?: return@Ctx)
		}, edit = {
			selectAction(selectedObject ?: return@Ctx, true)
		}, delete = {
			deleteAction(selectedObject ?: return@Ctx)
		})
		tree.componentPopupMenu = ctx
		tree.addMouseListener(object : MouseAdapter() {
			override fun mouseClicked(e: MouseEvent) { // left click -> action
				val node = tree.lastSelectedPathComponent as DefaultMutableTreeNode? ?: return
				val uo: Any? = node.userObject
				if (uo != null && SwingUtilities.isLeftMouseButton(e))
					selectAction(uo, uo is Card && e.clickCount == 2)
			}

			override fun mousePressed(e: MouseEvent) { // right click -> context menu
				if (SwingUtilities.isRightMouseButton(e)) {
					val path = tree.getClosestPathForLocation(e.x, e.y) ?: return
					val item = (path.lastPathComponent ?: return) as DefaultMutableTreeNode
					val b = tree.getRowBounds(tree.getRowForPath(path)) ?: return

					fun disable(): String {
						ctx.new?.text = "New..."
						ctx.edit?.text = "Edit..."
						ctx.new?.isEnabled = false
						ctx.edit?.isEnabled = false
						return "..."
					}

					if (e.y >= b.minY && e.y <= b.maxY) { // this only fires when it's inside the tree's bounds, so no need to check for X (rows span the entirety of the width)
						tree.selectionPath = TreePath(item.path)
						ctx.new?.isEnabled = true
						ctx.new?.text = "New" + when (item.userObject) {
							is Group      -> " Card"
							is Curriculum -> " Group"
							else          -> disable()
						}
						ctx.edit?.isEnabled = true
						ctx.edit?.text = when (item.userObject) {
							is Group, is Curriculum -> "Rename"
							is Card                 -> "Edit"
							else                    -> disable()
						}
					} else // not close enough
						disable()

					ctx.revalidate()
					ctx.repaint()
				}
			}
		})

		val scroll = JScrollPane()
		scroll.setViewportView(tree)
		add(swing<JPanel> {
			panel {
				borderLayout {
					left {
						panel {
							flowLayout(FlowLayout.LEADING) {
								label("Curriculum")
								curriculumSelector = comboBox(kt.ctx.activeLibrary.curriculums.toMutableSet().apply { add(PLACEHOLDER_CURRICULUM) }.toMutableList()) {
									attr {
										selectedIndex = 0
										if (selectedItem === PLACEHOLDER_CURRICULUM)
											selectedItem = null
										addActionListener {
											if (!initd) return@addActionListener // hacky hack hack
											var selected = self.selectedItem as Curriculum? ?: return@addActionListener
											if (selected === PLACEHOLDER_CURRICULUM) {
												val name = JOptionPane.showInputDialog(
													this@CurriculumExplorer,
													"Enter the name of the curriculum:", "New Curriculum",
													JOptionPane.QUESTION_MESSAGE
																					  )
													?: return@addActionListener (run {
														selectedItem = null
														repopulate()
													})
												selected = kt.ctx.activeLibrary.getCurriculum(name)
												(self.model as MutableComboBoxModel<Curriculum>).insertElementAt(selected, 0)
											}
											populate(selected)
										}
									}
								}
							}
						}
					}
				}
			}
		}, BorderLayout.NORTH)
		add(scroll, BorderLayout.CENTER)
		repopulate()
		requestFocusInWindow()
		initd = true // hacky hack hack
	}

	fun MutableTreeNode.insert(child: MutableTreeNode) = (tree.model as DefaultTreeModel).insertNodeInto(child, this, childCount)

	fun MutableTreeNode.delete() = (tree.model as DefaultTreeModel).removeNodeFromParent(this)

	fun repopulate() {
		populate(curriculumSelector.selectedItem as Curriculum?)
	}

	fun populate(curriculum: Curriculum?) {
		var root = tree.model.root
		if (root !is SimpleTreeNode || root.userObject != curriculum) { // clear & repopulate
			root = SimpleTreeNode(curriculum, NaturalOrderComparator())
			tree.model = DefaultTreeModel(root)
			println("[tree] <-> curriculum $curriculum")
		}

		if (curriculum != null) {
			curriculum.flatten()
			val groups = curriculum.groups.toMutableList()
			for (node in Util.getChildren(root)) { // iterate over existing groups
				val obj = node.userObject
				if (obj is Card) // clear & readd
					node.delete()
				if (obj is Group) {
					if (groups.remove(obj)) // group is not new
						for (card in kt.ctx.activeLibrary.getCards(obj)) // readd cards (group node should preserve expansion state)
							node.insert(SimpleTreeNode(card))
					else // group has been deleted
						node.delete()
				}
			}
			for (group in groups) { // add new groups
				println("[tree] + group $group")
				val node = SimpleTreeNode(group) { node1, node2 -> // custom sort
					if(node1 !is DefaultMutableTreeNode || node2 !is DefaultMutableTreeNode)
						return@SimpleTreeNode NATTY.compare(node1, node2)

					val a = node1.userObject
					val b = node2.userObject
					if (a !is Card || b !is Card)
						return@SimpleTreeNode NATTY.compare(a, b)

					fun ord(o: Card) = PartOfSpeech.fromLinguistic(o.grammar?.firstOrNull())?.ordinal ?: 0
					fun ver(o: Card) = PartOfSpeech.variantFromLinguistic(o.grammar?.firstOrNull())

					var comp = Integer.compare(ord(a), ord(b))
					if (comp == 0)
						comp = Integer.compare(ver(a), ver(b))
					if (comp == 0)
						comp = NATTY.compare(a.english?.firstOrNull(), b.english?.firstOrNull())
					if (comp == 0)
						comp = NATTY.compare(a.japanese?.firstOrNull(), b.japanese?.firstOrNull())
					comp
				}
				for (card in kt.ctx.activeLibrary.getCards(group))
					node.insert(SimpleTreeNode(card))
				root.insert(node)
			}
		}

		curriculumSelector.apply {
			removeAllItems()
			kt.ctx.activeLibrary.curriculums.toMutableSet().apply { add(PLACEHOLDER_CURRICULUM) }.forEach {
				addItem(it)
			}
			if (curriculum == null) {
				selectedIndex = 0
				if (selectedItem === PLACEHOLDER_CURRICULUM)
					selectedItem = null
			} else {
				selectedItem = curriculum
			}
		}

		root.sort()
		tree.expandPath(TreePath(root.path))
		repaint()
	}
}