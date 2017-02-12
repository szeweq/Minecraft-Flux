package szewek.mcflux.util.error;

import szewek.mcflux.L;
import szewek.mcflux.wrapper.MCFluxWrapper;

public final class ErrMsgNullWrapper extends ErrMsg {
	private final boolean objectNull;
	public ErrMsgNullWrapper(boolean obj) {
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
}
