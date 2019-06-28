package szewek.mcflux.fluxcompat.addons

import ic2.api.energy.EnergyNet
import ic2.api.energy.prefab.BasicSink
import ic2.api.energy.prefab.BasicSource
import ic2.api.energy.tile.IEnergySink
import ic2.api.energy.tile.IEnergySource
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import szewek.fl.energy.IEnergy
import szewek.fl.util.JavaUtils
import szewek.mcflux.MCFlux.Companion.L
import szewek.mcflux.U
import szewek.mcflux.config.MCFluxConfig.CFG_EU_VALUE
import szewek.mcflux.fluxcompat.EnergyType
import szewek.mcflux.fluxcompat.FluxCompat
import szewek.mcflux.fluxcompat.ForgeEnergyCapable
import szewek.mcflux.fluxcompat.LazyEnergyCapProvider
import szewek.mcflux.util.InjectCond
import szewek.mcflux.util.MCFluxReport
import java.lang.reflect.Method
import java.util.function.DoubleSupplier

@FluxCompat.Addon(requires = InjectCond.MOD, args = arrayOf("IC2", "IndustrialCraft 2"))
class IC2FluxCompat : FluxCompat.Lookup {
	private val IC2_TEB: Class<*>? = JavaUtils.getClassSafely("ic2.core.block.TileEntityBlock")
	private val IC2_ENERGY: Class<*>? = JavaUtils.getClassSafely("ic2.core.block.comp.Energy")
	private val COMPONENT: Method?
	private val CAPACITY: Method?
	private val ENERGY: Method?
	private val broken: Boolean

	init {
		if (IC2_TEB == null || IC2_ENERGY == null) {
			ENERGY = null
			CAPACITY = null
			COMPONENT = null
		} else {
			COMPONENT = JavaUtils.getMethodSafely(IC2_TEB, "getComponent", Class::class.java)
			CAPACITY = JavaUtils.getMethodSafely(IC2_ENERGY, "getCapacity")
			ENERGY = JavaUtils.getMethodSafely(IC2_ENERGY, "getEnergy")
		}
		broken = COMPONENT == null || CAPACITY == null || ENERGY == null
		if (broken)
			L!!.warn("IC2FluxCompat is broken")
	}

	override fun lookFor(lecp: LazyEnergyCapProvider, r: FluxCompat.Registry) {
		val cp = lecp.obj
		val cn = cp!!.javaClass.name
		if (cn == null) {
			L!!.warn("IC2FluxCompat: An obj doesn't have a class name: $cp")
			return
		}
		if (cn.startsWith("ic2.core") || cn.startsWith("cpw.mods.compactsolars"))
			r.register(EnergyType.EU, ::tileFactorize as FluxCompat.Factory)
	}

	private fun tileFactorize(lecp: LazyEnergyCapProvider) {
		if (broken) return
		val cp = lecp.obj
		if (cp == null || cp !is TileEntity) return
		val te = cp as TileEntity?
		val et = EnergyNet.instance.getTile(te!!.world, te.pos)
		val esrc = et as? IEnergySource
		val esnk = et as? IEnergySink
		var cfunc: DoubleSupplier? = if (et is BasicSource) DoubleSupplier { et.capacity } else if (et is BasicSink) DoubleSupplier { et.capacity } else null
		var efunc: DoubleSupplier? = if (et is BasicSource) DoubleSupplier { et.energyStored } else if (et is BasicSink) DoubleSupplier { et.energyStored } else null
		if (IC2_TEB!!.isInstance(cp)) {
			var o: Any? = null
			try {
				o = COMPONENT!!.invoke(cp, IC2_ENERGY)
			} catch (e: Exception) {
				MCFluxReport.sendException(e, "[IC2] FluxCompat factorize")
			}

			cfunc = doubleFunc(CAPACITY, o)
			efunc = doubleFunc(ENERGY, o)
		}
		val eud = EUDelegate(cfunc, efunc, esnk, esrc)
		val es = arrayOfNulls<IEnergy>(7)
		for (i in 0 until U.FANCY_FACING.size) {
			val f = U.FANCY_FACING[i]
			es[i] = EnergyTile(eud, f)
		}
		lecp.update(es, IntArray(0), null, true)
	}

	private fun doubleFunc(m: Method?, o: Any?): DoubleSupplier {
		return DoubleSupplier {
			try {
				m!!.invoke(o) as Double
			} catch (e: Exception) {
				0.0
			}
		}
	}

	private class EUDelegate internal constructor(internal val capMethod: DoubleSupplier?, internal val energyMethod: DoubleSupplier?, internal val sink: IEnergySink?, internal val source: IEnergySource?)

	private class EnergyTile internal constructor(private val delegate: EUDelegate, private val face: EnumFacing?) : ForgeEnergyCapable(), FluxCompat.Convert {

		override val energyType: EnergyType
			get() = EnergyType.EU

		override fun getEnergy(): Long {
			var dc = 0.0
			if (delegate.energyMethod != null)
				dc = delegate.energyMethod.asDouble
			return (dc * CFG_EU_VALUE).toLong()
		}

		override fun getEnergyCapacity(): Long {
			var dc = 0.0
			if (delegate.capMethod != null)
				dc = delegate.capMethod.asDouble
			return (dc * CFG_EU_VALUE).toLong()
		}

		override fun canInputEnergy(): Boolean {
			return delegate.sink != null && delegate.sink.acceptsEnergyFrom(null, face)
		}

		override fun canOutputEnergy(): Boolean {
			return delegate.source != null && delegate.source.emitsEnergyTo(null, face)
		}

		override fun inputEnergy(amount: Long, sim: Boolean): Long {
			if (amount < CFG_EU_VALUE) return 0
			if (delegate.sink != null) {
				val e = delegate.sink.demandedEnergy.toLong() * CFG_EU_VALUE
				var r = amount - amount % CFG_EU_VALUE
				if (r > e)
					r = e
				if (!sim) {
					delegate.sink.injectEnergy(face, r / CFG_EU_VALUE.toDouble(), EnergyNet.instance.getPowerFromTier(delegate.sink.sinkTier))
				}
				return r
			}
			return 0
		}

		override fun outputEnergy(amount: Long, sim: Boolean): Long {
			if (amount < CFG_EU_VALUE) return 0
			if (delegate.source != null) {
				val e = delegate.source.offeredEnergy.toLong() * CFG_EU_VALUE
				var r = amount - amount % CFG_EU_VALUE
				if (r > e)
					r = e
				if (!sim) {
					delegate.source.drawEnergy(r / CFG_EU_VALUE.toDouble())
				}
				return r
			}
			return 0
		}

		override fun hasNoEnergy(): Boolean {
			return delegate.energyMethod != null && delegate.energyMethod.asDouble == 0.0
		}

		override fun hasFullEnergy(): Boolean {
			return delegate.energyMethod != null && delegate.capMethod != null && delegate.energyMethod.asDouble == delegate.capMethod.asDouble
		}
	}
}
