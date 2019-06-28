package szewek.mcflux.fluxcompat

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import szewek.fl.FL
import szewek.fl.energy.IEnergy
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
class LazyEnergyCapProvider internal constructor(lo: ICapabilityProvider) : ICapabilityProvider {
	private val sides = arrayOfNulls<IEnergy>(7)
	var obj: ICapabilityProvider? = null
		internal set
	internal var status = Status.CREATED
	private var compatFE = false
	private var connectFunc: Predicate<EnumFacing?>? = null

	init {
		for (i in 0..6) sides[i] = LazyEnergy()
		obj = lo
	}

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		if (status != Status.NOT_ENERGY && (cap === FL.ENERGY_CAP || compatFE && cap === CapabilityEnergy.ENERGY)) {
			if (connectFunc != null) return connectFunc!!.test(f)
			if (status == Status.CREATED && sides[f?.index ?: 6] is LazyEnergy)
				FluxCompat.findActiveEnergy(this)
			return true
		}
		return false
	}

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		return if (hasCapability(cap, f)) sides[f?.index ?: 6] as T else null
	}

	fun update(ies: Array<IEnergy?>, l: IntArray, func: Predicate<EnumFacing?>?, fe: Boolean) {
		when (l.size) {
			0 -> {
				if (ies.size < 7) return
				for (i in 0..6) {
					val le = sides[i] as LazyEnergy
					le.ie = ies[i]
					sides[i] = ies[i]
				}
			}
			7 -> for (i in 0..6) {
				val x = l[i]
				val le = sides[i] as LazyEnergy
				le.ie = ies[x]
				sides[i] = ies[x]
			}
		}
		if (func != null) connectFunc = func
		compatFE = fe
		status = Status.READY
		obj = null
	}

	internal fun setNotEnergy() {
		status = Status.NOT_ENERGY
		for (i in 0..6) sides[i] = null
	}

	internal enum class Status {
		CREATED, ACTIVATED, READY, NOT_ENERGY
	}
}
