package szewek.mcflux.util.error;

import szewek.mcflux.L;

public class ErrMsgOldAPI extends ErrMsg {
	public ErrMsgOldAPI(String name, Class<?> cl) {
		super(name, cl, null);
	}

	@Override protected void printError() {
		L.warn("\n+----= Warning: Use of old API (" + name + ") =----"
				+ "\n| Object class: " + cl.getName()
				+ "\n+----"
		);
	}

	@Override protected void printShortError(int total, long delta) {}
}
