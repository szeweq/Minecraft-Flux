package szewek.mcflux.fluxcompat.addons

import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
import net.darkhax.tesla.lib.TeslaUtils
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.ICapabilityProvider
import szewek.fl.energy.IEnergy
import szewek.mcflux.U
import szewek.mcflux.fluxcompat.EnergyType
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.fluxcompat.ForgeEnergyCapable
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider
import szewek.mcflux.network.CloudUtils
import szewek.mcflux.util.ErrMsg
import szewek.mcflux.util.InjectCond
import szewek.mcflux.util.MCFluxReport

@FluxCompat.Addon(requires = InjectCond.MOD, args = ["tesla", "TESLA"])
class TeslaFluxCompat : FluxCompat.Lookup {
	override fun lookFor(lecp: LazyEnergyCapProvider, r: FluxCompat.Registry) {
		val icp = lecp.obj ?: return
		var f: EnumFacing? = null
		try {
			for (i in 0 until U.FANCY_FACING.size) {
				f = U.FANCY_FACING[i]
				if (TeslaUtils.hasTeslaSupport(icp, f)) {
					r.register(EnergyType.TESLA, ::factorize as FluxCompat.Factory)
				}
			}
		} catch (e: Exception) {
			MCFluxReport.addErrMsg(ErrMsg.BadImplementation("TESLA", icp.javaClass, e, f))
		}

	}

	private fun factorize(lecp: LazyEnergyCapProvider) {
		val icp = lecp.obj
		val es = arrayOfNulls<IEnergy>(7)
		for (i in 0 until U.FANCY_FACING.size) {
			es[i] = Energy(icp, U.FANCY_FACING[i])
		}
		lecp.update(es, IntArray(0), null, true)
		CloudUtils.reportEnergy(icp!!.javaClass, (es[0] as Energy).holder!!.javaClass, "tesla")
	}

	private class Energy internal constructor(provider: ICapabilityProvider?, f: EnumFacing?) : ForgeEnergyCapable(), FluxCompat.Convert {
		internal val holder: ITeslaHolder? = TeslaUtils.getTeslaHolder(provider, f)
		private val consumer: ITeslaConsumer? = TeslaUtils.getTeslaConsumer(provider, f)
		private val producer: ITeslaProducer? = TeslaUtils.getTeslaProducer(provider, f)

		override val energyType
			get() = EnergyType.TESLA

		override fun canInputEnergy() = consumer != null

		override fun canOutputEnergy() = producer != null

		override fun inputEnergy(amount: Long, sim: Boolean) = consumer?.givePower(amount, sim) ?: 0

		override fun outputEnergy(amount: Long, sim: Boolean) = producer?.takePower(amount, sim) ?: 0

		override fun getEnergy() = holder?.storedPower ?: 0

		override fun getEnergyCapacity() = holder?.capacity ?: 0

		override fun hasNoEnergy() = holder != null && holder.storedPower == 0L

		override fun hasFullEnergy() = holder != null && holder.storedPower == holder.capacity
	}
}