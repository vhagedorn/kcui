package me.vadim.ja.kc.wrapper;

/**
 * @author vadim
 */
public class PartOfSpeech {

	public final String name;
	public final Info info;

	public PartOfSpeech(String name){
		this(name, null);
	}

	public PartOfSpeech(String name, Info info) {
		this.name = name;
		this.info = info;
	}

	public boolean hasInfo(){ return info != null; }


	/**
	 * Extra info about the part of speech, such as "irregular" for verbs.
	 */
	public final class Info {

		public final String value;

		public Info(String value) {
			this.value = value;
		}

	}

}
