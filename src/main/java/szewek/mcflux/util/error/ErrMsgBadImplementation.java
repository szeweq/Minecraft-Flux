package szewek.mcflux.util.error;

import net.minecraft.util.EnumFacing;
import szewek.mcflux.L;

public class ErrMsgBadImplementation extends ErrMsg {
	private final EnumFacing face;

	public ErrMsgBadImplementation(String name, Class<?> cl, Throwable thrown, EnumFacing face) {
		super(name, cl, thrown);
		this.face = face;
	}

	@Override protected void printError() {
		L.warn("\n+----= An error occured when trying to attach a capability =----"
				+ "\n| Bad/incomplete " + name + " implementation (checked " + (face != null ? "WITH SIDE " + face : "SIDELESS") + ")"
				+ "\n| Capability provider class: " + cl.getName()
				+ "\n| Tell authors of this implementation about it!"
				+ "\n| This is not a Minecraft-Flux problem."
				+ "\n+----"
		);
		L.warn(msgThrown);
	}

	@Override protected void printShortError(int total, long delta) {
		L.warn("Bad/incomplete " + name + " implementation error for \"" + cl.getName() + "\" (checked "
				+ (face != null ? "WITH SIDE " + face : "SIDELESS")
				+ ") ×" + total + " in " + delta + " ms!");
	}

	@Override public int hashCode() {
		return super.hashCode() + (face.getIndex() << 28);
	}
}
