package me.vadim.ja.kc.model;

import com.google.common.hash.HashCode;
import me.vadim.ja.kc.model.wrapper.Card;
import me.vadim.ja.kc.model.wrapper.Curriculum;
import me.vadim.ja.kc.model.wrapper.Group;
import me.vadim.ja.kc.model.wrapper.Library;
import me.vadim.ja.kc.model.xml.Location;
import me.vadim.ja.kc.render.impl.ctx.RenderContext;

/**
 * @author vadim
 */
public interface LibraryContext {

	Library getActiveLibrary();

	Preferences getPreferences();

	RenderContext getRenderContext();

	void save(Card kanji);

	void savePreferences();

	void saveLibrary(boolean quiet);

	void delete(HashCode code, Location location);

	void delete(Card kanji);

	void delete(Curriculum curriculum);

	void delete(Group group);

	void shutdown();

}
