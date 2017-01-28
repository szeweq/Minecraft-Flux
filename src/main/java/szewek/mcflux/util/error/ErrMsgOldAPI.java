package szewek.mcflux.util.error;

import szewek.mcflux.L;

public class ErrMsgOldAPI extends ErrMsg {
	public ErrMsgOldAPI(String name, Class<?> cl) {
		super(name, cl, null);
	}

	@Override protected void printError() {
		L.warn("\n+----= Warning: Use of old API =----"
				+ "\n| Minecraft-Flux has detected use of API: " + name
				+ "\n| This API may be not supported in the future"
				+ "\n| Object class: " + cl.getName()
				+ "\n+----"
		);
	}

	@Override protected void printShortError(int total, long delta) {}
}
