package szewek.mcflux.wrapper;

public enum EnergyType {
	FORGE_ENERGY("fe"), MEKANISM("mkj"), REBORN_CORE("rc"), EU("eu"), IF("if"), TESLA("tesla"), RF("rf"), NONE("");

	public static final EnergyType[] ALL;

	public final String name;

	EnergyType(String name) {
		this.name = name;
	}

	static {
		ALL = values();
	}

	public interface Converter {
		EnergyType getEnergyType();
	}
}
