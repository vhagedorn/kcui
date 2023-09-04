package me.vadim.ja.swing;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Comparator;

/**
 * <a href="https://stackoverflow.com/a/53245415/12344841">source</a>
 *
 * @author Candamir
 */
public class SimpleTreeNode extends DefaultMutableTreeNode {

	private final Comparator<Object> comparator;

	public SimpleTreeNode(Object userObject, Comparator<Object> comparator) {
		super(userObject);
		this.comparator = comparator;
	}

	public SimpleTreeNode(Object userObject) {
		this(userObject, null);
	}

	@Override
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		if (this.comparator != null) {
			this.children.sort(this.comparator);
		}
	}

	public void sort() { // vadim -- add recursive sort method
		if (children != null) {
			children.forEach(it -> {
				if (it instanceof SimpleTreeNode)
					((SimpleTreeNode) it).sort();
			});
			if (comparator != null)
				children.sort(comparator);
		}
	}

}