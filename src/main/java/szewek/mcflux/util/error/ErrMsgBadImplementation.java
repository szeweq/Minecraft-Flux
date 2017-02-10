package szewek.mcflux.util.error;

import net.minecraft.util.EnumFacing;
import szewek.mcflux.L;

public class ErrMsgBadImplementation extends ErrMsg {
	private final EnumFacing face;

	public ErrMsgBadImplementation(String name, Class<?> cl, Throwable thrown, EnumFacing face) {
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
}
