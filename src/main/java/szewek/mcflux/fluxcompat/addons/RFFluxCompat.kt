package szewek.mcflux.fluxcompat.addons

import cofh.redstoneflux.api.IEnergyHandler
import cofh.redstoneflux.api.IEnergyProvider
import cofh.redstoneflux.api.IEnergyReceiver
import net.minecraft.util.EnumFacing
import net.minecraftforge.energy.IEnergyStorage
import szewek.fl.energy.IEnergy
import szewek.mcflux.U
import szewek.mcflux.fluxcompat.EnergyType
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider
import szewek.mcflux.network.CloudUtils
import szewek.mcflux.util.InjectCond
import java.util.function.Predicate

@FluxCompat.Addon(requires = InjectCond.MOD, args = ["redstoneflux"])
class RFFluxCompat : FluxCompat.Lookup {
	override fun lookFor(lecp: LazyEnergyCapProvider, r: FluxCompat.Registry) {
		val cp = lecp.obj
		if (cp == null || cp !is IEnergyHandler) return
		r.register(EnergyType.RF, ::tileFactorize as FluxCompat.Factory)
	}

	private fun tileFactorize(lecp: LazyEnergyCapProvider) {
		val eh = lecp.obj as IEnergyHandler? ?: return
		val ep = eh as? IEnergyProvider
		val er = eh as? IEnergyReceiver
		val rfd = RFDelegate(eh, ep, er)
		val ets = arrayOfNulls<IEnergy>(7)
		for (i in 0 until U.FANCY_FACING.size) ets[i] = EnergyTile(rfd, U.FANCY_FACING[i])
		lecp.update(ets, IntArray(0), Predicate { eh.canConnectEnergy(it) }, true)
		CloudUtils.reportEnergy(eh.javaClass, null, "rf")
	}

	private class RFDelegate internal constructor(internal val handler: IEnergyHandler, internal val provider: IEnergyProvider?, internal val receiver: IEnergyReceiver?)

	private class EnergyTile internal constructor(private val delegate: RFDelegate, private val face: EnumFacing?) : IEnergy, FluxCompat.Convert, IEnergyStorage {

		override val energyType: EnergyType
			get() = EnergyType.RF

		override fun getEnergy(): Long = delegate.handler.getEnergyStored(face).toLong()

		override fun getEnergyCapacity(): Long = delegate.handler.getMaxEnergyStored(face).toLong()

		override fun canInputEnergy(): Boolean = delegate.receiver != null

		override fun canOutputEnergy(): Boolean = delegate.provider != null

		override fun inputEnergy(amount: Long, sim: Boolean) =
				(if (delegate.receiver != null) delegate.receiver.receiveEnergy(face, if (amount > Integer.MAX_VALUE) Integer.MAX_VALUE else amount.toInt(), sim) else 0).toLong()

		override fun outputEnergy(amount: Long, sim: Boolean) =
				(if (delegate.provider != null) delegate.provider.extractEnergy(face, if (amount > Integer.MAX_VALUE) Integer.MAX_VALUE else amount.toInt(), sim) else 0).toLong()

		override fun receiveEnergy(maxReceive: Int, simulate: Boolean) =
				if (delegate.receiver != null) delegate.receiver.receiveEnergy(face, maxReceive, simulate) else 0

		override fun extractEnergy(maxExtract: Int, simulate: Boolean) =
				if (delegate.provider != null) delegate.provider.extractEnergy(face, maxExtract, simulate) else 0

		override fun getEnergyStored() = delegate.handler.getEnergyStored(face)

		override fun getMaxEnergyStored() = delegate.handler.getMaxEnergyStored(face)

		override fun canExtract() = delegate.provider != null

		override fun canReceive() = delegate.receiver != null

		override fun hasNoEnergy() = delegate.handler.getEnergyStored(face) == 0

		override fun hasFullEnergy() =
				delegate.handler.getEnergyStored(face) == delegate.handler.getMaxEnergyStored(face)
	}
}
