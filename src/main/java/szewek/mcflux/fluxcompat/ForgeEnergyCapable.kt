package szewek.mcflux.fluxcompat

import szewek.fl.energy.IEnergy

abstract class ForgeEnergyCapable : IEnergy, net.minecraftforge.energy.IEnergyStorage {
	override fun receiveEnergy(maxReceive: Int, simulate: Boolean) =
			inputEnergy(maxReceive.toLong(), simulate).toInt()

	override fun extractEnergy(maxExtract: Int, simulate: Boolean) =
			outputEnergy(maxExtract.toLong(), simulate).toInt()

	override fun getEnergyStored(): Int {
		val e = energy
		return if (e > Integer.MAX_VALUE) Integer.MAX_VALUE else e.toInt()
	}

	override fun getMaxEnergyStored(): Int {
		val e = energyCapacity
		return if (e > Integer.MAX_VALUE) Integer.MAX_VALUE else e.toInt()
	}

	override fun canExtract() = canOutputEnergy()

	override fun canReceive() = canInputEnergy()
}
