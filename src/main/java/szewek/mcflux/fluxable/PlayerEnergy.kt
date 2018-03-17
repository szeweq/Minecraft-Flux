package szewek.mcflux.fluxable

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import szewek.fl.FL
import szewek.fl.energy.IEnergy
import szewek.mcflux.fluxable.FluxableCapabilities.CAP_PE

@Suppress("UNCHECKED_CAST")
class PlayerEnergy internal constructor(private val player: EntityPlayer?) : IEnergy, ICapabilityProvider, INBTSerializable<NBTBase> {

	private var venergy: Long = 0
	private var maxEnergy: Long = 0
	private var lvl: Byte = 0

	constructor() : this(null)

	fun updateLevel(): Byte {
		if (lvl.toInt() == 30)
			return -1
		++lvl
		maxEnergy = (100000 * lvl).toLong()
		return lvl
	}

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?) =
			cap === CAP_PE || maxEnergy > 0 && cap === FL.ENERGY_CAP

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?) =
			if (cap === CAP_PE || maxEnergy > 0 && cap === FL.ENERGY_CAP) this as T else null

	override fun outputEnergy(amount: Long, sim: Boolean): Long {
		if (amount == 0L)
			return 0
		var r = venergy
		if (amount < r)
			r = amount
		if (!sim)
			venergy -= r
		return r
	}

	override fun canInputEnergy() = maxEnergy > 0

	override fun canOutputEnergy() = maxEnergy > 0

	override fun inputEnergy(amount: Long, sim: Boolean): Long {
		if (amount == 0L)
			return 0
		if (maxEnergy == 0L) {
			player!!.attackEntityFrom(DamageSource.GENERIC, (amount / 100).toFloat())
			return 0
		}
		var r = maxEnergy - venergy
		if (amount < r)
			r = amount
		if (!sim)
			venergy += r
		return r
	}

	override fun getEnergy() = venergy

	override fun getEnergyCapacity() = maxEnergy

	override fun serializeNBT(): NBTBase {
		val nbt = NBTTagCompound()
		nbt.setByte("lvl", lvl)
		nbt.setLong("e", venergy)
		return nbt
	}

	override fun deserializeNBT(nbt: NBTBase) {
		if (nbt is NBTTagCompound) {
			val nbttc = nbt
			lvl = nbttc.getByte("lvl")
			venergy = nbttc.getLong("e")
		}
	}
}
