package szewek.mcflux.fluxable

import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import szewek.fl.FL
import szewek.fl.energy.ForgeEnergyCompat
import szewek.mcflux.config.MCFluxConfig.FURNACE_CAP
import szewek.mcflux.util.EnergyCapable

@Suppress("UNCHECKED_CAST")
class FurnaceEnergy(private val furnace: TileEntityFurnace) : EnergyCapable() {
	private val fec = ForgeEnergyCompat(this)

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		return cap === FL.ENERGY_CAP || cap === CapabilityEnergy.ENERGY
	}

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		return if (cap === CapabilityEnergy.ENERGY) fec as T else super.getCapability(cap, f)
	}

	override fun getEnergy(): Long {
		val e = furnace.getField(0)
		return (if (e > FURNACE_CAP) FURNACE_CAP else e).toLong()
	}

	override fun getEnergyCapacity(): Long {
		return (if (canInputEnergy()) FURNACE_CAP else 0).toLong()
	}

	override fun canInputEnergy(): Boolean {
		val is0 = furnace.getStackInSlot(0)
		if (is0.isEmpty)
			return false
		val stk = FurnaceRecipes.instance().getSmeltingResult(is0)
		if (stk.isEmpty)
			return false
		val is2 = furnace.getStackInSlot(2)
		if (is2.isEmpty)
			return true
		if (!is2.isItemEqual(stk))
			return false
		val r = is2.count + stk.count
		return r <= furnace.inventoryStackLimit && r <= is2.maxStackSize
	}

	override fun canOutputEnergy(): Boolean {
		return false
	}

	override fun inputEnergy(amount: Long, sim: Boolean): Long {
		if (canInputEnergy() && amount > 0) {
			val f = furnace.getField(0)
			if (f >= FURNACE_CAP) return 0
			val fm = furnace.getField(1)
			if (fm < FURNACE_CAP)
				furnace.setField(1, FURNACE_CAP)
			var r = FURNACE_CAP - f
			if (r > amount)
				r = amount.toInt()
			if (!sim)
				furnace.setField(0, f + r)
			return r.toLong()
		}
		return 0
	}

	override fun outputEnergy(amount: Long, sim: Boolean): Long {
		return 0
	}
}
