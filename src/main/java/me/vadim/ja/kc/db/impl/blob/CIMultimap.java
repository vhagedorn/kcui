package me.vadim.ja.kc.db.impl.blob;

import me.vadim.ja.kc.db.impl.DbMultimapAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vadim
 */
@Deprecated
class CIMultimap extends DbMultimapAdapter<CachedImage.Key, CachedImage.Value> {

	@Override
	protected void createTables() throws SQLException {
		connection.prepareStatement("create table if not exists IMGDAT(id INT primary key, img BLOB)").execute();
	}

	@Override
	protected List<CachedImage.Value> query(CachedImage.Key key) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select img from IMGDAT where id=?");
		statement.setLong(1, key.id());

		ResultSet               result = statement.executeQuery();
		List<CachedImage.Value> buf    = new ArrayList<>();
		while (result.next())
			buf.add(CachedImage.value(result.getBytes(1)));
		return buf;
	}

	@Override
	protected void delete(CachedImage.Key key) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("delete from IMGDAT where id=?");
		statement.setLong(1, key.id());
		statement.execute();
	}

	@Override
	protected void insert(CachedImage.Key key, List<CachedImage.Value> values) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("replace into IMGDAT (id, img) VALUES (?, ?)");
		for (CachedImage.Value value : values) {
			statement.setLong(1, key.id());
			statement.setBytes(2, value.decode());
			statement.addBatch();
		}
		statement.executeBatch();
	}

}
