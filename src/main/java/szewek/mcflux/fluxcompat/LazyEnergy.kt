package szewek.mcflux.fluxcompat

import szewek.fl.energy.IEnergy

class LazyEnergy : ForgeEnergyCapable(), FluxCompat.Convert {
	internal var notEnergy = false
	internal var ie: IEnergy? = null

	val isReady: Boolean get() = ie != null

	override fun canInputEnergy() = ie != null && ie!!.canInputEnergy()
	override fun canOutputEnergy() = ie != null && ie!!.canOutputEnergy()
	override fun inputEnergy(amount: Long, sim: Boolean) = ie?.inputEnergy(amount, sim) ?: 0
	override fun outputEnergy(amount: Long, sim: Boolean) = ie?.outputEnergy(amount, sim) ?: 0
	override fun getEnergy() = ie?.energy ?: 0
	override fun getEnergyCapacity() = ie?.energyCapacity ?: 0

	override val energyType get() = if (notEnergy) EnergyType.NONE else EnergyType.LAZY
}
