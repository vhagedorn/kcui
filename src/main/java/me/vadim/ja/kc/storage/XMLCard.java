package me.vadim.ja.kc.storage;

import kotlin.NotImplementedError;
import me.vadim.ja.kc.storage.model.ReadContext;
import me.vadim.ja.kc.storage.model.impl.CardModel;
import me.vadim.ja.kc.storage.model.impl.CurriculumModel;
import me.vadim.ja.kc.storage.model.impl.GroupModel;
import me.vadim.ja.kc.storage.model.impl.LibraryModel;
import me.vadim.ja.kc.wrapper.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * @author vadim
 */
public class XMLCard implements XMLinator<Kanji> {

	/* elements */

	public static final String ELEM_CARD            = "Card";
	public static final String ELEM_PARTS_OF_SPEECH = "PartsOfSpeech";
	public static final String ELEM_PRONOUNCIATIONS = "Pronounciations";
	public static final String ELEM_DEFINITIONS     = "Definitions";

	/* tags */

	public static final String TAG_CARD_INFO      = "value";
	public static final String TAG_PART_OF_SPEECH = "p";
	public static final String TAG_PRONOUNCIATION = "p";
	public static final String TAG_DEFINITION     = "d";

	/* attributes */

	public static final String ATTR_ID        = "id";
	public static final String ATTR_CURRICULUM = "curriculum";
	public static final String ATTR_GROUP      = "group";
	public static final String ATTR_NAME       = "name";
	public static final String ATTR_INFO       = "info";
	public static final String ATTR_PID         = "pid";
	public static final String ATTR_INDEX      = "i";
	public static final String ATTR_TYPE       = "type";

	/* values */

	public static final String VAL_NULL = "null";


	@Override
	public Kanji read(Document doc) {
		throw new NotImplementedError();
	}

	@Override
	public void read(Document doc, ReadContext ctx) {
		Element card = doc.getElementById(ELEM_CARD);
		String cur = card.getAttribute(ATTR_CURRICULUM);
		String grp = card.getAttribute(ATTR_GROUP);
		String kid = card.getAttribute(ATTR_ID);
		String val = card.getElementsByTagName(TAG_CARD_INFO).item(0).getTextContent();

		//todo: hmmmmmmm
		ctx.advanceCurriculum(cur);
		ctx.advanceGroup(grp);
		ctx.advanceCard(val);
		Kanji model = ctx.currentCard().model;
		model.setId(Long.parseLong(kid));

		NodeList nl;

		Element pos = doc.getElementById(ELEM_PARTS_OF_SPEECH);
		nl = pos.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node p = nl.item(i);
			p.getAttributes().getNamedItem(ATTR_NAME);
			p.getAttributes().getNamedItem(ATTR_INFO);
			p.getAttributes().getNamedItem(ATTR_PID);
		}

		Element pron = doc.getElementById(ELEM_PRONOUNCIATIONS);
		nl = pron.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node p = nl.item(i);
			p.getAttributes().getNamedItem(ATTR_TYPE);
			p.getTextContent();
		}

		Element defs = doc.getElementById(ELEM_DEFINITIONS);
		nl = defs.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node d = nl.item(i);
			d.getTextContent();
			d.getAttributes().getNamedItem(ATTR_INDEX);
		}
	}

	@Override
	public void write(Kanji kanji, Document doc) {
		Element card = doc.createElement(ELEM_CARD);
		card.setAttribute(ATTR_CURRICULUM, String.valueOf(kanji.curriculum.id()));
		card.setAttribute(ATTR_GROUP, String.valueOf(kanji.group.id()));
		card.setAttribute(ATTR_ID, ELEM_CARD);

		Element c = doc.createElement(TAG_CARD_INFO);
		c.setTextContent(kanji.value);
		card.appendChild(c);

		Element pos = doc.createElement(ELEM_PARTS_OF_SPEECH);
		pos.setAttribute(ATTR_ID, ELEM_PARTS_OF_SPEECH);
		for (PartOfSpeech part : kanji.getPartsOfSpeech()) {
			Element p = doc.createElement(TAG_PART_OF_SPEECH);
			p.setAttribute(ATTR_NAME, part.name);
			p.setAttribute(ATTR_INFO, part.hasInfo() ? part.info.value : VAL_NULL);
			p.setAttribute(ATTR_PID, String.valueOf(part.id()));
			pos.appendChild(p);
		}
		card.appendChild(pos);

		Element pron = doc.createElement(ELEM_PRONOUNCIATIONS);
		pron.setAttribute(ATTR_ID, ELEM_PRONOUNCIATIONS);
		for (Pronounciation pr : kanji.getPronounciations()) {
			Element p = doc.createElement(TAG_PRONOUNCIATION);
			p.setTextContent(pr.value);
			p.setAttribute(ATTR_TYPE, String.valueOf(pr.type.id));
			pron.appendChild(p);
		}
		card.appendChild(pron);

		Element defs = doc.createElement(ELEM_DEFINITIONS);
		defs.setAttribute(ATTR_ID, ELEM_DEFINITIONS);
		for (Definition def : kanji.getDefinitions()) {
			Element d = doc.createElement(TAG_DEFINITION);
			d.setTextContent(def.value);
			d.setAttribute(ATTR_INDEX, String.valueOf(def.getIndex()));
			defs.appendChild(d);
		}
		card.appendChild(defs);

		doc.appendChild(card);
	}

	public static void main(String[] args) throws Exception {

		// my plan for this is to load all the cards into memory
		// and then parse the entire library before slotting it into the GUI
		// ...grouping parts of speech, etc.
		//
		// i'm thinking ahead to where people con share cards. it should:
		// 	[1] match identical parts of speech together
		//	[2] attempt to merge incoming groups / curriculums (ask which curriculum to dump to)
		//	[3] automatically re-id incoming objects (to prevent clashing, then back to step [1])
		//
		// ideally there is only 3 kinds of XML files:
		//	- card.xml: contains shareable, persistent card information. ideally able to share this directly, but i think share.xml might easen the process for me (zip file with share.xml manifest
		//	and
		//	cards)
		//	- share.xml: (TBD) contains share information, such as incoming ids, groups, parts of speech, etc.
		// 	- lib.xml: contains library information (potentially make this a SQLite, but I'm tired of SQLite). this is like a permanent local share.xml
		LibraryModel lib = new LibraryModel();
		CurriculumModel cur = lib.newModel("Genki I");

		GroupModel gpm = cur.newModel("Lesson 4");

		CardModel card = gpm.newModel("<kanji>");
		///


		File file = new File("test.xml");

		Curriculum curriculum = new Curriculum("curr");
		curriculum.setId(1);
		Group group = curriculum.createGroup("grp");
		group.setId(10);
		Kanji k = new Kanji("maiban", group);
		k.addPronounciation(PronounciationType.UNKNOWN, "MAI-BAN");
		k.addDefinition("every night");
		k.addPartOfSpeech(PartOfSpeech.builder().name("noun").id(1).build());
		k.addPartOfSpeech(PartOfSpeech.builder().name("adverb").id(2).build());

		XMLCard  ke  = new XMLCard(XML.newDocument(), k);
		Document out = XML.newDocument();
		ke.write(out);
		XML.save(out, file);
	}
}