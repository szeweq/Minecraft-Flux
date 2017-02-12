package szewek.mcflux.util.error;

import szewek.mcflux.L;

public final class ErrMsgOldAPI extends ErrMsg {
	public ErrMsgOldAPI(String name, Class<?> cl) {
		super(name, cl, null);
	}

	@Override protected void printError() {
		L.warn("Warning: Use of old API (" + name + ") on class: " + cl.getName());
	}

	@Override protected void printShortError(int total, long delta) {}
}
