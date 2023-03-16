package me.vadim.ja.kc.db.impl.blob;

import me.vadim.ja.kc.db.impl.DbMultimapAdapter;
import me.vadim.ja.kc.db.impl.lib.KanjiLibrary;
import me.vadim.ja.kc.wrapper.Kanji;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.sql.SQLException;
import java.util.List;

/**
 * Latest pdf render for kanji<p>
 * Saved with the {@link KanjiLibrary#getCards() library}
 * @author vadim
 */
class DocMultimap extends DbMultimapAdapter<Kanji, PDDocument> {

	@Override
	protected void createTables() throws SQLException {
		//tbd: not a good idea i don't think
	}

	@Override
	protected List<PDDocument> query(Kanji key) throws SQLException {
		return null;
	}

	@Override
	protected void delete(Kanji key) throws SQLException {

	}

	@Override
	protected void insert(Kanji key, List<PDDocument> values) throws SQLException {

	}
}
