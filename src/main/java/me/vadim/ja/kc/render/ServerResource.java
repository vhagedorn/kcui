package me.vadim.ja.kc.render;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author vadim
 */
public class ServerResource {

	private final String name;
	private final byte[] data;
	private final String mime;
	private final Charset encoding;

	public ServerResource(String name, String data, String mime) {
		this(name, data.getBytes(StandardCharsets.UTF_8), mime, StandardCharsets.UTF_8);
	}

	public ServerResource(String name, byte[] data, String mime, Charset encoding) {
		this.name     = name;
		this.data     = Arrays.copyOf(data, data.length);
		this.mime     = mime;
		this.encoding = encoding;
	}

	@NotNull
	public String getName() {
		return name;
	}

	public byte[] getSnapshot() {
		return Arrays.copyOf(data, data.length);
	}

	@Nullable
	public String getMimeType() {
		return mime;
	}

	@Nullable
	public Charset getEncoding() {
		return encoding;
	}

}
