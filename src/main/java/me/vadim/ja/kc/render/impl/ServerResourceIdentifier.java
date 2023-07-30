package me.vadim.ja.kc.render.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author vadim
 */
public class ServerResourceIdentifier {

	public final String path, name;
	public final String mimeType;
	public final String resourceURL;
	public final Charset charset;

	public ServerResourceIdentifier(String path, String name, String mimeType, String resourceURL) {
		this(path, name, mimeType, resourceURL, StandardCharsets.UTF_8);
	}

	public ServerResourceIdentifier(String path, String name, String mimeType, String resourceURL, Charset charset) {
		this.path        = path;
		this.name        = name;
		this.mimeType    = mimeType;
		this.resourceURL = resourceURL;
		this.charset     = charset;
	}

}
