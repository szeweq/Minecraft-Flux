package szewek.mcflux.fluxcompat;

public enum EnergyType {
	EU, MEKANISM, REBORN_CORE, TESLA, FORGE_ENERGY, RF, OTHER, LAZY, NONE;

	public static final EnergyType[] ALL;

	static {
		ALL = values();
	}
}
