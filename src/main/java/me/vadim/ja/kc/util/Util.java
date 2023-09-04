package me.vadim.ja.kc.util;

import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static void disableIllegalAccessWarning_v1() {
		try {
			final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			final Unsafe u = (Unsafe) theUnsafe.get(null);

			final Class<?> useless = Class.forName("jdk.internal.module.IllegalAccessLogger");
			final Field    logger  = useless.getDeclaredField("logger");
			u.putObjectVolatile(useless, u.staticFieldOffset(logger), null);
		} catch (final Exception ignored) { }
	}

	/**
	 * @link <a href="https://stackoverflow.com/a/61700723/1234484">Very clever, JRE-friendly solution.</a>
	 */
	@SuppressWarnings("WaitWhileHoldingTwoLocks")
	public static void disableIllegalAccessWarning_v2() {
		try {
			final Object lock = new Object();
			final Field  f    = FilterOutputStream.class.getDeclaredField("out");
			final Runnable r = () -> {
				f.setAccessible(true);
				synchronized (lock) { lock.notify(); }
			};
			final Object errorOutput;
			synchronized (lock) {
				synchronized (System.err) //lock System.err to delay the warning
				{
					new Thread(r).start(); //One of these 2 threads will
					new Thread(r).start(); //hang, the other will succeed.
					lock.wait(); //Wait 1st thread to end.
					errorOutput = f.get(System.err); //Field is now accessible, set
					f.set(System.err, null); // it to null to suppress the warning

				} //release System.err to allow 2nd thread to complete.
				lock.wait(); //Wait 2nd thread to end.
				f.set(System.err, errorOutput); //Restore System.err
			}
		} catch (final Exception ignored) { }
	}

	public static void launch(File file) {
		try {
			Desktop.getDesktop().open(file); // theres some weird undocument process attaching bullshit going on with this method
		} catch (IOException ignored) { }
	}

	public static void browse(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException ignored) { }
	}

	//@formatter:off
	/**
	 * @link https://stackoverflow.com/a/42698573/12344841
	 * @author Robert Hunt
	 */
	public static String imgToBase64String(final RenderedImage img, final String formatName)
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		try
		{
			ImageIO.write(img, formatName, os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		}
		catch (final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	//@formatter:on

	public static int floorDiv(int a, int b) {
		return (a / b);
	}

	/**
	 * @link <a href="https://stackoverflow.com/a/21830188/12344841">Option 1</a>
	 */
	public static int ceilDiv(int a, int b) {
		return a / b + ((a % b == 0) ? 0 : 1);
	}

	public static <T> CompletableFuture<List<T>> combineFutures(Stream<CompletableFuture<T>> tasks) {
		return combineFutures(tasks.collect(Collectors.toList()));
	}

	@SuppressWarnings("CodeBlock2Expr")
	public static <T> CompletableFuture<List<T>> combineFutures(List<CompletableFuture<T>> tasks) {
		return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new)).thenApply(x -> {
			return tasks.stream().map(CompletableFuture::join).collect(Collectors.toList());
		});
	}

}
