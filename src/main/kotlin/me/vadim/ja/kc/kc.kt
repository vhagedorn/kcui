package me.vadim.ja.kc

import io.github.mslxl.ktswing.BasicScope
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.applyContainer
import io.github.mslxl.ktswing.layout.GridBagLayoutCellScope
import io.github.mslxl.ktswing.layout.GridBagLayoutRootScope
import me.vadim.ja.swing.HintTextField
import java.awt.GridBagConstraints
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.lang.UnsupportedOperationException
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

fun <T> nonce() = SetOnce<T>()

class SetOnce<T> : ReadWriteProperty<Any, T> {

	private val lock = Any()
	private var set = false
	private var value: T? = null

	override fun getValue(thisRef: Any, property: KProperty<*>): T = value ?: throw UninitializedPropertyAccessException("lazy value not initialized")

	override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
		synchronized(lock) {
			if (set) throw UnsupportedOperationException("SetOnce property may not be written to again")
			this.value = value
		}
	}
}

fun GridBagLayoutRootScope<*>.row(y: Int, block: GridBagLayoutCellScope<*>.() -> Unit) {
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

		block()
	}
}

//todo: scroll wheel a bit buggy
fun <T> GridBagLayoutRootScope<*>.resizableInputList(
	frame: JFrame,
	label: String,
	eachRow: CanAddChildrenScope<*>.(x: T?) -> Unit
													) = ResizableInputList(this, frame, label, eachRow)

@OptIn(ExperimentalContracts::class)
inline fun CanAddChildrenScope<*>.hintField(
	hint: String? = null,
	block: BasicScope<JTextField>.() -> Unit
												   ): HintTextField {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	return applyContainer(
		HintTextField(hint)
			.apply {
				addKeyListener(object : KeyAdapter() {
					override fun keyReleased(e: KeyEvent) {
						if (e.isControlDown && e.keyCode == KeyEvent.VK_A) {
							this@apply.selectAll()
							e.consume()
						}
					}
				})
			}, block
						 ) as HintTextField
}