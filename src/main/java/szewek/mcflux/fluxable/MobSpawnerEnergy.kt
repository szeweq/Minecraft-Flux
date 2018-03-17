package szewek.mcflux.fluxable

import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import szewek.fl.FL
import szewek.fl.energy.ForgeEnergyCompat
import szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE
import szewek.mcflux.util.EnergyCapable

@Suppress("UNCHECKED_CAST")
class MobSpawnerEnergy(private val spawner: TileEntityMobSpawner) : EnergyCapable() {
	private val fec = ForgeEnergyCompat(this)

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		return cap === FL.ENERGY_CAP || cap === CapabilityEnergy.ENERGY
	}

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		return if (cap === CapabilityEnergy.ENERGY) fec as T else super.getCapability(cap, f)
	}

	override fun getEnergy(): Long {
		return 0
	}

	override fun getEnergyCapacity(): Long {
		return MOB_SPAWNER_USE.toLong()
	}

	override fun canInputEnergy(): Boolean {
		return true
	}

	override fun canOutputEnergy(): Boolean {
		return false
	}

	override fun inputEnergy(amount: Long, sim: Boolean): Long {
		if (amount >= MOB_SPAWNER_USE) {
			spawner.update()
			return MOB_SPAWNER_USE.toLong()
		}
		return 0
	}

	override fun outputEnergy(amount: Long, sim: Boolean): Long {
		return 0
	}
}
