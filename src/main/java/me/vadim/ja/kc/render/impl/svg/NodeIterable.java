package me.vadim.ja.kc.render.impl.svg;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * @author vadim
 */
public class NodeIterable<T extends Node> extends AbstractList<T> {

	private final NodeList nodeList;

	public NodeIterable(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) nodeList.item(index);
	}

	@Override
	public int size() {
		return nodeList.getLength();
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return index < nodeList.getLength();
			}

			@Override
			@SuppressWarnings("unchecked")
			public T next() {
				return (T) nodeList.item(index++);
			}
		};
	}

}
