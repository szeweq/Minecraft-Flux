package szewek.mcflux.special

import it.unimi.dsi.fastutil.ints.IntArraySet
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable

@Suppress("UNCHECKED_CAST")
class SpecialEventReceiver : ICapabilityProvider, INBTSerializable<NBTBase> {

	private val received = IntArraySet()

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		return cap === SELF_CAP
	}

	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		return if (cap === SELF_CAP) this as T else null
	}

	override fun serializeNBT(): NBTBase {
		SpecialEventHandler.serNBT.add()
		return NBTTagIntArray(received.toIntArray())
	}

	override fun deserializeNBT(nbt: NBTBase) {
		if (nbt is NBTTagIntArray) {
			val ia = nbt.intArray
			for (i in ia)
				received.add(i)
		}
	}

	fun addReceived(l: Int) {
		received.add(l)
	}

	fun alreadyReceived(l: Int): Boolean {
		return received.contains(l)
	}

	companion object {
		@JvmField
		@CapabilityInject(SpecialEventReceiver::class)
		var SELF_CAP: Capability<SpecialEventReceiver>? = null
	}
}
