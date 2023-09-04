package me.vadim.ja.kc.render;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an async job that may be re-used.
 * @author vadim
 */
public interface Job<T> {

	/**
	 * @return a new or cached {@link CompletableFuture} that will compute the result
	 */
	CompletableFuture<T> getResult();

	/**
	 * Discard previous results and recompute.
	 * @return a fresh {@linkplain #getResult() result}
	 */
	CompletableFuture<T> recompute();

}
