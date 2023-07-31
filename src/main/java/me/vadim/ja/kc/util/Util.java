package me.vadim.ja.kc.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

	public static @NotNull InputStream loadResource(String name) throws ResourceNotFoundException {
		if (name == null)
			throw new ResourceNotFoundException(null);
		URL resource = Util.class.getClassLoader().getResource(name);
		try {
			if (resource != null)
				return resource.openStream();
		} catch (IOException e) {
			throw new ResourceNotFoundException(name, e);
		}
		throw new ResourceNotFoundException(name);
	}

	//@formatter:off
	public static <T> void reverse(T[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			T temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(byte[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			byte temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(short[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			short temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(long[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			long temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(float[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			float temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(double[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			double temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(boolean[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			boolean temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	public static void reverse(char[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			char temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	//@formatter:on


}
