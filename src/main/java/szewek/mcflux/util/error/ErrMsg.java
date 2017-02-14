package szewek.mcflux.util.error;

import java.util.*;

public abstract class ErrMsg {
	private static final long ERRORS_TIME = 15000;
	protected final String name;
	protected final Class<?> cl;
	protected final List<Throwable> thrownList;
	protected final Set<Throwable> setOfThrown;
	public final Throwable msgThrown;
	protected int cachedHash;
	private int count, lastCount;
	private long nextShow;

	protected ErrMsg(String name, Class<?> cl, Throwable thrown) {
		this.name = name;
		this.cl = cl;
		thrownList = new ArrayList<>();
		setOfThrown = new HashSet<>();
		msgThrown = thrown;
		count = 0;
		cachedHash = (this.getClass().hashCode() << 24) + (cl.hashCode() << 16) + name.hashCode();
		addThrowable(thrown);
	}

	@Override public int hashCode() {
		return cachedHash;
	}

	@Override public boolean equals(Object obj) {
		return obj == this || obj instanceof ErrMsg && obj.hashCode() == cachedHash;
	}

	public void addThrowable(Throwable th) {
		if (th != null)
			thrownList.add(th);
	}

	public List<Throwable> getThrowables() {
		return Collections.unmodifiableList(thrownList);
	}

	public void addUp() {
		long now = System.currentTimeMillis();
		count++;
		if (count == 1) {
			printError();
			nextShow = now + ERRORS_TIME;
			lastCount = 1;
			return;
		}
		if (nextShow < now) {
			printShortError(count - lastCount, now - nextShow + ERRORS_TIME);
			nextShow = now + ERRORS_TIME;
			lastCount = count;
		}
	}

	public String makeInfo() {
		return "| Name: " + name + "\n| Class: " + cl.getName() + "\n| Count: " + count;
	}

	protected abstract void printError();
	protected abstract void printShortError(int total, long delta);
}
