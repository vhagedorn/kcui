package me.vadim.ja.kc.persist.io;

import com.google.common.hash.HashCode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import me.vadim.ja.kc.persist.impl.Kard;
import me.vadim.ja.kc.persist.impl.Lib;
import me.vadim.ja.kc.persist.impl.Location;
import me.vadim.ja.kc.persist.wrapper.Card;
import me.vadim.ja.kc.persist.wrapper.Library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author vadim
 */
@SuppressWarnings("StaticNonFinalField")
public final class JAXBStorage {

	/**
	 * The base directory, namely where lib.xml is stored.
	 */
	public static File prefDir = new File(".");
	/**
	 * The directory where cards are stored.
	 */
	public static File cardDir = new File(prefDir, "cards");
	/**
	 * Freely floating groups not tied to a curriculum.
	 */
	public static File freeDir = new File(cardDir, ".group");

	private JAXBStorage() { }

	public static File card2file(Card card) {
		return card2file(card.getLocation(), card.hash());
	}

	public static File card2file(Location location, HashCode code) {
		return new File(location2file(location), code + ".xml");
	}

	private static File location2file(Location location) {
		if(location.getGroup() != null && location.getCurriculum() == null) // handle floating groups
			return new File(freeDir, location.getGroup().getName());

		File file = cardDir;
		if(location.getCurriculum() != null)
			file = new File(file, location.getCurriculum().getName());

		if(location.getGroup() != null)
			file = new File(file, location.getGroup().getName());

		return file;
	}

	public static Card readCard(Location location, HashCode code) {
		try {
			File file = card2file(location, code);
			if (file.isFile()) {
				JAXBContext  context = JAXBContext.newInstance(Kard.class);
				Unmarshaller umar    = context.createUnmarshaller();

				return (Card) umar.unmarshal(new FileReader(file));
			}
		} catch (JAXBException e) {
			System.err.println("Problem reading card: " + code.toString());
			e.printStackTrace();
			// todo: static UI error message queue
		} catch (FileNotFoundException ignored) {
			// never thrown
		}
		return null;
	}

	public static void dumpCard(Card card) {
		try {
			File file = card2file(card);
			file.getParentFile().mkdirs();

			JAXBContext context = JAXBContext.newInstance(Kard.class);
			Marshaller  mar     = context.createMarshaller();

			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			mar.marshal(card, file);
		} catch (JAXBException e) {
			System.err.println("Problem dumping card: " + card.hash());
			e.printStackTrace();
			// todo: static UI error message queue
		}
	}

	public static Library readLib(File libfile) {
		try {
			if (libfile.isFile()) {
				JAXBContext  context = JAXBContext.newInstance(Lib.class, Kard.class);
				Unmarshaller umar    = context.createUnmarshaller();

				return (Library) umar.unmarshal(new FileReader(libfile));
			}
		} catch (JAXBException e) {
			System.err.println("Problem reading library at " + libfile);
			e.printStackTrace();
			// todo: static UI error message queue
		} catch (FileNotFoundException ignored) {
			// never thrown
		}
		return null;
	}

	public static void dumpLib(Library library, File libfile) {
		try {
			libfile.getParentFile().mkdirs();

			JAXBContext context = JAXBContext.newInstance(Lib.class, Kard.class);
			Marshaller  mar     = context.createMarshaller();

			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			mar.marshal(library, libfile);
		} catch (JAXBException e) {
			System.err.println("Problem dumping library to " + libfile);
			e.printStackTrace();
			// todo: static UI error message queue
		}
	}

}
