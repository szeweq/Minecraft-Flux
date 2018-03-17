package szewek.mcflux.fluxable

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongArraySet
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants.NBT
import net.minecraftforge.common.util.INBTSerializable
import szewek.fl.energy.Battery
import szewek.mcflux.config.MCFluxConfig
import szewek.mcflux.fluxable.FluxableCapabilities.CAP_WCE

@Suppress("UNCHECKED_CAST")
/**
 * World Chunk Energy implementation.
 */
class WorldChunkEnergy : ICapabilityProvider, INBTSerializable<NBTBase> {

	private val eChunks = Long2ObjectOpenHashMap<Battery>()

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean = cap === CAP_WCE

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?) = if (cap === CAP_WCE) this as T else null

	/**
	 * Gets energy chunk (16x16x16) if available. If not, it creates a new one.
	 *
	 * @param bx Block X position
	 * @param by Block Y position
	 * @param bz Block Z position
	 * @return Chunk battery
	 */
	fun getEnergyChunk(bx: Int, by: Int, bz: Int): Battery {
		val l = packLong(bx / 16, by / 16, bz / 16)
		if (eChunks.containsKey(l)) {
			return eChunks.get(l)
		}
		val bat = Battery(MCFluxConfig.WORLDCHUNK_CAP.toLong())
		eChunks[l] = bat
		return bat
	}

	override fun serializeNBT(): NBTBase {
		val nbtl = NBTTagList()
		val poss = LongArraySet()
		poss.addAll(eChunks.keys)
		for (l in poss) {
			val nbt = NBTTagCompound()
			nbt.setLong("cp", l)
			val e = eChunks.get(l)
			if (e != null)
				nbt.setTag("e", e.serializeNBT())
			nbtl.appendTag(nbt)
		}
		return nbtl
	}

	override fun deserializeNBT(nbtb: NBTBase) {
		if (nbtb is NBTTagList) {
			for (i in 0 until nbtb.tagCount()) {
				val nbt = nbtb.getCompoundTagAt(i)
				var hasPos = false
				var l: Long = 0
				if (nbt.hasKey("x", NBT.TAG_INT) && nbt.hasKey("y", NBT.TAG_INT) && nbt.hasKey("z", NBT.TAG_INT)) {
					l = packLong(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"))
					hasPos = true
				} else if (nbt.hasKey("cp", NBT.TAG_LONG)) {
					l = nbt.getLong("cp")
					hasPos = true
				}
				if (hasPos) {
					if (nbt.hasKey("e")) {
						val eb = Battery(MCFluxConfig.WORLDCHUNK_CAP.toLong())
						eb.deserializeNBT(nbt.getTag("e"))
						eChunks[l] = eb
					}
				}
			}
		}
	}

	companion object {
		private const val X_BITS = 22
		private const val Z_BITS = 22
		private const val Y_BITS = 4
		private const val Y_SHIFT = Z_BITS
		private const val X_SHIFT = Y_SHIFT + Y_BITS
		private const val X_MASK = (1L shl X_BITS) - 1L
		private const val Y_MASK = (1L shl Y_BITS) - 1L
		private const val Z_MASK = (1L shl Z_BITS) - 1L

		private fun packLong(x: Int, y: Int, z: Int): Long {
			return x.toLong() and X_MASK shl X_SHIFT or (y.toLong() and Y_MASK shl Y_SHIFT) or (z.toLong() and Z_MASK)
		}
	}
}
