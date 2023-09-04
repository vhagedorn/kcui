package me.vadim.ja.kc.render.impl.ctx;

import me.vadim.ja.kc.render.Job;

import java.util.concurrent.CompletableFuture;

/**
 * @author vadim
 */
abstract class AbstractJob<T> implements Job<T> {

	protected final RenderContext context;

	AbstractJob(RenderContext context) {
		this.context = context;
	}


	@Override
	public CompletableFuture<T> recompute() {
		result = null;
		return getResult();
	}

	// for cachability
	protected CompletableFuture<T> result;

}
