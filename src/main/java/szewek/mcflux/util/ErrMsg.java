package szewek.mcflux.util;

import com.rollbar.Rollbar;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.L;
import szewek.mcflux.wrapper.MCFluxWrapper;

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

	ErrMsg(String name, Class<?> cl, Throwable thrown) {
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

	public void sendInfo(Rollbar rb) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("EM.Name", name);
		m.put("EM.Class", cl.getName());
		addInfo(m);
		rb.warning(msgThrown, m, getClass().getName() + ": " + msgThrown.getMessage());
	}

	protected void addInfo(Map<String, Object> m) {

	}

	protected abstract void printError();
	protected abstract void printShortError(int total, long delta);

	public static final class BadImplementation extends ErrMsg {
		private final EnumFacing face;

		public BadImplementation(String name, Class<?> cl, Throwable thrown, EnumFacing face) {
			super(name, cl, thrown);
			this.face = face;
			if (face != null)
				cachedHash += 1 + (face.getIndex() << 28);
		}

		@Override protected void printError() {
			L.warn("\n+--= Warning: Bad/incomplete " + name + " implementation =--"
					+ "\n| Checked " + (face != null ? "WITH SIDE " + face : "SIDELESS")
					+ "\n| Capability provider class: " + cl.getName()
					+ "\n| Possibly this is not meant to be an error."
					+ "\n| Tell authors of this implementation about it!"
					+ "\n+--"
			);
			L.warn(msgThrown);
		}

		@Override protected void printShortError(int total, long delta) {
			L.warn("Bad/incomplete " + name + " implementation: \"" + cl.getName() + "\" ("
					+ (face != null ? "SIDE " + face : "SIDELESS")
					+ ") ×" + total + " in " + delta + " ms!");
		}

		@Override protected void addInfo(Map<String, Object> m) {
			m.put("EM.Side", face);
		}

		@Override public String makeInfo() {
			return super.makeInfo() + "\n| Side: " + (face != null ? face : "none");
		}
	}

	public static final class NullInject extends ErrMsg {
		public NullInject(Class<?> cl) {
			super("nullinject", cl, new Throwable());
		}

		@Override protected void printError() {
			L.warn("An object with type \"" + cl.getName() + "\" is null! Minecraft-Flux can't inject any capability into an empty object");
			L.warn(msgThrown);
		}

		@Override protected void printShortError(int total, long delta) {
			L.warn('"' + cl.getName() + "\" null inject errors: " + total + " in " + delta + " ms");
		}
	}

	public static final class NullWrapper extends ErrMsg {
		private final boolean objectNull;
		public NullWrapper(boolean obj) {
			super("wrapper", MCFluxWrapper.class, null);
			objectNull = obj;
			if (obj)
				cachedHash++;
		}

		@Override protected void printError() {
			L.warn("A wrapp" + (objectNull ? "ed object" : "er") + " is null!");
		}

		@Override protected void printShortError(int total, long delta) {
			L.warn("Null wrapp" + (objectNull ? "ed object" : "er") + " errors: " + total + " in " + delta + " ms");
		}

		@Override protected void addInfo(Map<String, Object> m) {
			m.put("EM.NullObject", objectNull);
		}
	}
}
