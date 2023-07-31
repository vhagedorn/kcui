package me.vadim.ja.kc.util.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author vadim
 */
public class LocalExecutors {

	public static ExecutorService newExtendedThreadPool() {
		return new ExtendedExecutor(0, Integer.MAX_VALUE,
									60L, TimeUnit.SECONDS,
									new SynchronousQueue<Runnable>());
	}

	public static ExecutorService newExtendedThreadPool(ThreadFactory threadFactory) {
		return new ExtendedExecutor(0, Integer.MAX_VALUE,
									60L, TimeUnit.SECONDS,
									new SynchronousQueue<Runnable>(),
									threadFactory);
	}

}
