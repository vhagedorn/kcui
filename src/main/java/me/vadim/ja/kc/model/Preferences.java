package me.vadim.ja.kc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import me.vadim.ja.kc.render.impl.svg.StrokePlotter;

/**
 * @author vadim
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pref")
public final class Preferences {

	@XmlElement
	public String kvg_dir;

	public void applyProperties() {
		String kvg = System.getProperty(StrokePlotter.KVG_DIR, null);
		if(kvg == null && kvg_dir != null) // allow users to set -Dkvg_dir from command-line
			System.setProperty(StrokePlotter.KVG_DIR, kvg_dir);
	}

}
