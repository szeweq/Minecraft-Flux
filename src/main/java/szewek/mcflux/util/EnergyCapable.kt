package szewek.mcflux.util

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import szewek.fl.FL

@Suppress("UNCHECKED_CAST")
abstract class EnergyCapable : szewek.fl.energy.IEnergy, ICapabilityProvider {
	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		return cap === FL.ENERGY_CAP
	}

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		return if (cap === FL.ENERGY_CAP) this as T else null
	}
}
