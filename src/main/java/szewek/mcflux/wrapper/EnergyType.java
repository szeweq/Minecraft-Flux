package szewek.mcflux.wrapper;

public enum EnergyType {
	FORGE_ENERGY("fe"), TESLA("tesla"), MEKANISM("mkj"), REBORN_CORE("rc"), EU("eu"), IF("if"), RF("rf"), NONE("");

	public static final EnergyType[] ALL;

	public final String name;

	EnergyType(String name) {
		this.name = name;
	}

	static {
		ALL = values();
	}
}
