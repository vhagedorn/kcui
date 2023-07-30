package me.vadim.ja.kc.util;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author vadim
 */
public final class Util {

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void fuckUncheckedExceptions(Throwable throwable) throws T {
		throw (T) throwable;
	}

	public static void sneaky(Throwable throwable) {
		Util.<RuntimeException>fuckUncheckedExceptions(throwable);
	}

	/**
	 * @link <a href="https://stackoverflow.com/a/4237934/12344841">Source</a>
	 */
	public static final Pattern xml11pattern = Pattern.compile("[^"
															   + "\u0001-\uD7FF"
															   + "\uE000-\uFFFD"
															   + "\ud800\udc00-\udbff\udfff"
															   + "]+");

	public static String sanitizeXML(String dirty) {
		return dirty == null ? null : xml11pattern.matcher(dirty).replaceAll("");
	}

	public static void traverse(DefaultMutableTreeNode root, Consumer<DefaultMutableTreeNode> onEachUserObject) {
		Stack<DefaultMutableTreeNode> stack = new Stack<>();
		stack.push(root);

		while (!stack.isEmpty()) {
			DefaultMutableTreeNode node = stack.pop();
			onEachUserObject.accept(node);

			// Add child nodes to the stack (right to left to maintain order)
			for (int i = node.getChildCount() - 1; i >= 0; i--) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
				stack.push(childNode);
			}
		}
	}

	public static List<DefaultMutableTreeNode> getChildren(DefaultMutableTreeNode root) {
		List<DefaultMutableTreeNode> nodes = new ArrayList<>(100);
		traverse(root, nodes::add);
		return nodes;
	}

}
