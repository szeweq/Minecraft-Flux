package szewek.mcflux.util.error;

import szewek.mcflux.L;

public class ErrMsgNullInject extends ErrMsg {
	public ErrMsgNullInject(Class<?> cl) {
		super("nullinject", cl, null);
	}

	@Override protected void printError() {
		L.warn("An object with type \"" + cl.getName() + "\" is null!");
		L.warn("Minecraft-Flux can't inject any capability into an empty object");
	}

	@Override protected void printShortError(int total, long delta) {
		L.warn('"' + cl.getName() + "\" null inject errors: " + total + " in " + delta + " ms");
	}
}
