package szewek.mcflux.util;

import com.rollbar.Rollbar;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.LinkedHashMap;
import java.util.Map;

public class Timer {
	final String name, thName;
	private final Object obj;
	private final int cachedHash;
	private long nano;
	long nanoTotal = 0, nanoMin = 0, nanoMax = 0;
	double nanoAvg = 0;
	private boolean started = false;
	private LongList measures = new LongArrayList();

	Timer(String s, Object o) {
		name = s;
		obj = o;
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

	void report(Rollbar rb) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("Extra", obj);
		m.put("Thread", thName);
		long[] l = measures.toLongArray();
		m.put("Count", l.length);
		nanoMin = nanoMax = l[0];
		for (int i = 0; i < l.length; i++) {
			if (i > 0) {
				if (l[i] < nanoMin)
					nanoMin = l[i];
				if (l[i] > nanoMax)
					nanoMax = l[i];
			}
			nanoTotal += l[i];
		}
		m.put("Sum", nanoTotal);
		if (l.length > 1) {
			m.put("Min", nanoMin);
			m.put("Max", nanoMax);
		}
		nanoAvg = (double) nanoTotal / l.length;
		m.put("Avg", nanoAvg);
		rb.info("Timer: " + name, m);
	}

	int getCount() {
		return measures.size();
	}

	long[] getMeasures() {
		return measures.toLongArray();
	}

	@Override public int hashCode() {
		return cachedHash;
	}
}
