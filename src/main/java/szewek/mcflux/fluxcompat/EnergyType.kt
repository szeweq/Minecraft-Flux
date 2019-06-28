package szewek.mcflux.fluxcompat

enum class EnergyType {
	EU, MEKANISM, REBORN_CORE, FORGE_ENERGY, RF, OTHER, LAZY, NONE;

	companion object {
		val ALL: Array<EnergyType> = values()
	}
}
