package szewek.mcflux.special

import com.google.gson.JsonObject
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTException
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import szewek.mcflux.util.MCFluxReport

class SpecialEvent private constructor(val description: String, internal val colorBox: Int, internal val colorRibbon: Int, private val items: Array<SpecialItem>, val endTime: Long) {

	fun createItems(): Array<ItemStack> {
		val iss = arrayOfNulls<ItemStack>(items.size)
		for (i in items.indices) {
			if (items[i].item != Items.AIR) {
				val stk = ItemStack(items[i].item!!, items[i].amount.toInt(), items[i].meta.toInt())
				if (items[i].tag != null) {
					stk.tagCompound = items[i].tag!!.copy()
				}
				iss[i] = stk
			} else {
				iss[i] = ItemStack.EMPTY
			}
		}
		@Suppress("UNCHECKED_CAST")
		return iss as Array<ItemStack>
	}

	internal class SpecialItem(val name: String, val amount: Byte, val meta: Short, t: String?) {
		val item = Item.REGISTRY.getObject(ResourceLocation(name)) ?: Items.AIR
		val tag: NBTTagCompound?

		init {
			if (t != null && item != Items.AIR) {
				var nbt: NBTTagCompound?
				try {
					nbt = JsonToNBT.getTagFromJson(t)
				} catch (e: NBTException) {
					MCFluxReport.sendException(e, "NBT Decoding")
					nbt = null
				}

				tag = nbt
			} else
				tag = null
		}
	}

	companion object {

		@Suppress("UNCHECKED_CAST")
		@JvmStatic
		internal fun fromJSON(jo: JsonObject): SpecialEvent? {
			var ev: SpecialEvent? = null
			try {
				val d = jo["desc"].asString
				val cb = jo["box"].asInt
				val cr = jo["ribbon"].asInt
				val et = jo["ends"].asLong
				val ja = jo["items"].asJsonArray
				val sis = arrayOfNulls<SpecialItem>(ja.size())
				for (i in 0 until ja.size()) {
					val ji = ja[i].asJsonArray
					var m: Short = 0
					var t: String? = null
					if (ji.size() > 2)
						m = ji[2].asShort
					if (ji.size() > 3)
						t = ji[3].asString
					sis[i] = SpecialItem(ji[0].asString, ji[1].asByte, m, t)
				}
				ev = SpecialEvent(d, cb, cr, sis as Array<SpecialItem>, et)
			} catch (e: Exception) {
				MCFluxReport.sendException(e, "JSON decoding")
			}

			return ev
		}
	}
}
