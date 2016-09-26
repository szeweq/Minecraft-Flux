package szewek.mcflux.wrapper;

import szewek.mcflux.util.MCFluxLocation;

public enum EnergyType {
	FORGE_ENERGY("fe"), TESLA("tesla"), EU("eu"), IF("if"), RF("rf"), NONE("");

	public static final EnergyType[] ALL;

	public final MCFluxLocation loc;

	EnergyType(String name) {
		loc = new MCFluxLocation(name);
	}

	static {
		ALL = values();
	}
}
