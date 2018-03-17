package szewek.mcflux.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.network.NetworkRegistry
import szewek.fl.network.FLNetChannel
import szewek.mcflux.R
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object MCFluxNetwork {
	private lateinit var CHAN: FLNetChannel

	fun registerAll() {
		CHAN = FLNetChannel(R.MF_NAME, Arrays.asList(
				Msg.Update::class.java,
				Msg.NewVersion::class.java,
				Msg.FluidAmount::class.java
		))
	}

	@JvmStatic
	fun to(msg: Msg, mp: EntityPlayerMP) = CHAN.to(msg, mp)

	@JvmStatic
	fun toAll(msg: Msg) = CHAN.toAll(msg)

	@JvmStatic
	fun toAllAround(msg: Msg, tp: NetworkRegistry.TargetPoint) = CHAN.toAllAround(msg, tp)

	@JvmStatic
	fun toDimension(msg: Msg, dim: Int) = CHAN.toDimension(msg, dim)

	@JvmStatic
	fun toServer(msg: Msg) = CHAN.toServer(msg)

	@Throws(IOException::class)
	fun downloadGistJSON(hash: String, name: String): JsonObject {
		val url = URL("https", "gist.githubusercontent.com", 443, "/Szewek/$hash/raw/$name", null)
		val huc = url.openConnection() as HttpURLConnection
		val isr = InputStreamReader(huc.inputStream)
		return JsonParser().parse(isr).asJsonObject
	}
}
