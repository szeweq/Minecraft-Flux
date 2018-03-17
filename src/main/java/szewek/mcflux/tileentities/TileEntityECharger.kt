package szewek.mcflux.tileentities

import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import szewek.fl.FLU
import szewek.fl.annotations.NamedResource
import szewek.fl.energy.IEnergy
import java.util.*

@NamedResource("mcflux:echarger")
class TileEntityECharger : TileEntityWCEAware(), ITickable {
	private var sideIndex = -1
	private val sides = arrayOfNulls<IEnergy>(6)
	private var esrc: IEnergy? = null
	private val chargeables = ArrayList<IEnergy>()

	public override fun updateTile() {
		if (world.isRemote)
			return
		checkSources()
		nextSource()
		if (sideIndex == -1)
			return
		for (ie in chargeables) {
			var t: Long = 0
			do {
				if (esrc != null) {
					t = esrc!!.to(ie, 4000)
				}
				if (esrc == null || esrc!!.hasNoEnergy())
					nextSource()
			} while (t == 0L && sideIndex != -1)
			if (sideIndex == -1)
				return
		}
	}

	fun addEntityEnergy(ie: IEnergy) {
		chargeables += ie
	}

	fun removeEntityEnergy(ie: IEnergy) {
		chargeables.remove(ie)
	}

	private fun checkSources() {
		for (f in EnumFacing.VALUES) {
			val bp = pos.offset(f, 1)
			val te = world.getTileEntity(bp)
			if (te != null) {
				esrc = FLU.getEnergySafely(te, f.opposite)
				if (esrc != null)
					sides[f.index] = esrc
			}
		}
		sideIndex = -1
	}

	private fun nextSource() {
		sideIndex++
		sideIndex %= 7
		for (i in sideIndex..5) {
			if (sides[i] != null && sides[i]!!.energy > 0) {
				sideIndex = i
				esrc = sides[i]
				return
			}
		}
		if (bat != null && bat!!.energy > 0) {
			sideIndex = 6
			esrc = bat
			return
		}
		sideIndex = -1
	}
}
