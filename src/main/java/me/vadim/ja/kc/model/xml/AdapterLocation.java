package me.vadim.ja.kc.model.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author vadim
 */
class AdapterLocation extends XmlAdapter<String, Location> {

	@Override
	public Location unmarshal(String v) throws Exception {
		if (Location.empty.equals(v))
			return Location.EMPTY;

		if (v == null || v.isBlank())
			throw new IllegalArgumentException(v);
		String[] split = v.split(Location.DELIM);
		if (split.length > 2)
			throw new IllegalArgumentException(v);
		if (split.length == 1)
			if (v.startsWith(Location.DELIM)) // #group
				return new Location(null, new ImplGroup(null, split[0]));
			else // curriculum
				return new Location(new ImplCurriculum(split[0]), null);
		else // curriculum#group
			return new ImplGroup(new ImplCurriculum(split[0]), split[1]).toLocation();
	}

	@Override
	public String marshal(Location v) throws Exception {
		return v == null ? Location.empty : v.toString();
	}

}
