package szewek.mcflux.util;

import it.unimi.dsi.fastutil.longs.*;

public class Timer {
	final String name, thName;
	private final int cachedHash;
	private long nano;
	private boolean started = false;
	private LongList measures = new LongArrayList();

	Timer(String s) {
		name = s;
		thName = Thread.currentThread().getName();
		cachedHash = name.hashCode() ^ 10000000;
	}

	void start() {
		if (!started) {
			nano = System.nanoTime();
			started = true;
		}
	}

	void stop() {
		if (started) {
			measures.add(System.nanoTime() - nano);
			started = false;
		}
	}

	long[] getMeasures() {
		return measures.toLongArray();
	}

	@Override public int hashCode() {
		return cachedHash;
	}
}
