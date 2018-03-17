package szewek.mcflux.fluxcompat.addons

import net.minecraft.util.EnumFacing
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import szewek.fl.energy.IEnergy
import szewek.mcflux.U
import szewek.mcflux.fluxcompat.EnergyType
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider
import szewek.mcflux.network.CloudUtils
import szewek.mcflux.tileentities.TileEntityFluxGen
import szewek.mcflux.util.ErrMsg
import szewek.mcflux.util.InjectCond
import szewek.mcflux.util.MCFluxReport

@FluxCompat.Addon(requires = InjectCond.CLASS, args = ["net.minecraftforge.energy.IEnergyStorage"])
class ForgeFluxCompat : FluxCompat.Lookup {
	override fun lookFor(lecp: LazyEnergyCapProvider, r: FluxCompat.Registry) {
		val icp = lecp.obj
		if (icp == null || icp is TileEntityFluxGen || blacklist(icp)) return
		var f: EnumFacing? = null
		try {
			for (i in 0 until U.FANCY_FACING.size) {
				f = U.FANCY_FACING[i]
				if (icp.hasCapability(CapabilityEnergy.ENERGY, f)) {
					r.register(EnergyType.FORGE_ENERGY, ::forgeFactorize as FluxCompat.Factory)
				}
			}
		} catch (e: Exception) {
			MCFluxReport.addErrMsg(ErrMsg.BadImplementation("Forge Energy", icp.javaClass, e, f))
		}

	}

	private fun forgeFactorize(lecp: LazyEnergyCapProvider) {
		val icp = lecp.obj
		val es = arrayOfNulls<IEnergy>(7)
		var f: EnumFacing?
		val s = IntArray(7)
		var x = 0
		var i = 0
		M@ while (i < U.FANCY_FACING.size) {
			f = U.FANCY_FACING[i]
			val ies = icp!!.getCapability(CapabilityEnergy.ENERGY, f)
			for (j in 0 until x) {
				if ((es[j] as Energy).storage === ies) {
					s[i] = j
					i++
					continue@M
				}
			}
			es[x] = Energy(ies)
			s[i] = x++
			i++
		}
		lecp.update(es, s, null, false)
		if (es[0] != null && (es[0] as Energy).storage != null)
			CloudUtils.reportEnergy(icp!!.javaClass, (es[0] as Energy).storage!!.javaClass, "forge")
	}

	private fun blacklist(o: Any): Boolean {
		return o.javaClass.name.startsWith("ic2.core")
	}

	private class Energy internal constructor(internal val storage: IEnergyStorage?) : IEnergy, FluxCompat.Convert {

		override val energyType
			get() = EnergyType.FORGE_ENERGY

		override fun canInputEnergy() = storage?.canReceive() ?: false

		override fun canOutputEnergy() = storage?.canExtract() ?: false

		override fun inputEnergy(amount: Long, sim: Boolean) =
				(storage?.receiveEnergy(if (amount > Integer.MAX_VALUE) Integer.MAX_VALUE else amount.toInt(), sim) ?: 0).toLong()

		override fun outputEnergy(amount: Long, sim: Boolean) =
				(storage?.extractEnergy(if (amount > Integer.MAX_VALUE) Integer.MAX_VALUE else amount.toInt(), sim) ?: 0).toLong()

		override fun getEnergy() = (storage?.energyStored ?: 0).toLong()

		override fun getEnergyCapacity() = (storage?.maxEnergyStored ?: 0).toLong()

		override fun hasNoEnergy() = storage?.energyStored == 0

		override fun hasFullEnergy() = storage?.energyStored == storage?.maxEnergyStored
	}
}
