package szewek.mcflux.util.error;

import szewek.mcflux.L;
import szewek.mcflux.wrapper.MCFluxWrapper;

public class ErrMsgNullWrapper extends ErrMsg {
	public ErrMsgNullWrapper() {
		super("wrapper", MCFluxWrapper.class, null);
	}

	@Override protected void printError() {
		L.warn("A wrapper is null or a wrapped object is null!");
	}

	@Override protected void printShortError(int total, long delta) {
		L.warn("Null wrapper errors: " + total + " in " + delta + " ms");
	}
}
