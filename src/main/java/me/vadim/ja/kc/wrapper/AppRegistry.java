package me.vadim.ja.kc.wrapper;

import me.vadim.ja.kc.KanjiCardUI;
import me.vadim.ja.kc.db.DbEnum;
import me.vadim.ja.kc.db.impl.lib.KanjiLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author vadim
 */
public class AppRegistry {

	private final KanjiLibrary lib;

	private final ExecutorService worker = KanjiCardUI.singleThread("Library I/O connector");

	public AppRegistry(KanjiLibrary lib) {
		this.lib = lib;
	}

	public boolean alive() {
		return lib.isConnected();
	}

	public void start(){
		worker.submit(lib::connect);
	}

	public void cease(){
		worker.submit(lib::disconnect);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		cease();
	}

	public void saveKanji(Kanji kanji){
		worker.submit(() -> lib.getCards().update(kanji));
	}

	public void deleteKanji(Kanji kanji){
		if(kanji.hasId())
			worker.submit(() -> lib.getCards().delete(kanji.id()));
	}

	public CompletableFuture<List<Kanji>> loadKanji(){
		return CompletableFuture.supplyAsync(lib.getCards()::values, worker).thenApply(Arrays::asList);
	}

	public CompletableFuture<List<PartOfSpeech>> loadGrammar() {
		return CompletableFuture.supplyAsync(lib.getPartOfSpeech()::values, worker).thenApply(Arrays::asList);
	}

	public void saveGrammar(List<PartOfSpeech> parts) {
		DbEnum<PartOfSpeech> pos = lib.getPartOfSpeech();
		/*return*/ CompletableFuture.supplyAsync(pos::values, worker)
				.thenApplyAsync(values -> {
					List<CompletableFuture<?>> updates = new ArrayList<>();
					for (PartOfSpeech part : parts) // insert all the new parts
						updates.add(CompletableFuture.supplyAsync(() -> {
							pos.update(part);
							return null;
						}, worker));

					return CompletableFuture.allOf(updates.toArray(CompletableFuture[]::new))
//									 .thenApplyAsync(x -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
							.thenApplyAsync(x -> {
								List<Long> ids = parts.stream().map(Identifiable::id).collect(Collectors.toList());
								List<PartOfSpeech> buf = new ArrayList<>();
								for (PartOfSpeech part : values) // filter parts in the db which weren't passed to this method
									if(!ids.contains(part.id()))
										buf.add(part);

								List<CompletableFuture<?>> deletes = new ArrayList<>();

								for (PartOfSpeech part : buf) // delete the ones not present (must have been deleted from the UI)
									deletes.add(CompletableFuture.supplyAsync(() -> {
										pos.delete(part.id());
										return null;
									}, worker));

								return CompletableFuture.allOf(deletes.toArray(CompletableFuture[]::new));
							}, worker);
				});
	}

	private void saveG(List<PartOfSpeech> parts) {
		DbEnum<PartOfSpeech> pos = lib.getPartOfSpeech();
		PartOfSpeech[] old = pos.values();

		for (PartOfSpeech part : parts) // insert all the new parts
				pos.update(part);

		@SuppressWarnings("DuplicatedCode")
		List<Long>         ids = parts.stream().map(Identifiable::id).collect(Collectors.toList());
		List<PartOfSpeech> buf = new ArrayList<>();
		for (PartOfSpeech part : old) // filter parts in the db which weren't passed to this method
			if(!ids.contains(part.id()))
				buf.add(part);

		for (PartOfSpeech part : buf) // delete the ones not present (must have been deleted from the UI)
			pos.delete(part.id());
	}

	public CompletableFuture<Void> saveCurriculum(Curriculum curriculum){
		return CompletableFuture.supplyAsync(() -> {
			lib.getCurriculums().update(curriculum);
			return null;
		}, worker);
	}

	public void deleteCurriculum(Curriculum curriculum){
		if(curriculum.hasId())
			worker.submit(() -> lib.getCurriculums().delete(curriculum.id()));
	}

	public CompletableFuture<List<Curriculum>> loadCurriculums(){
		return CompletableFuture.supplyAsync(lib.getCurriculums()::values, worker).thenApply(Arrays::asList);
	}

}
