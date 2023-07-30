package me.vadim.ja.kc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author vadim
 */
public interface ResourceAccess {

	default InputStream loadResource(String name) {
		if (name == null) return null;
		URL resource = getClass().getClassLoader().getResource(name);
		try {
			if (resource != null)
				return resource.openStream();
		} catch (IOException ignored) { }
		return null;
	}

}
