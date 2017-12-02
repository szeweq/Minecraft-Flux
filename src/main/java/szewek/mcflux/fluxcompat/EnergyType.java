package szewek.mcflux.fluxcompat;

public enum EnergyType {
	FORGE_ENERGY, MEKANISM, REBORN_CORE, EU, IF, TESLA, RF, OTHER, LAZY, NONE;

	public static final EnergyType[] ALL;

	static {
		ALL = values();
	}
}
