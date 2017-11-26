package szewek.mcflux.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import szewek.fl.network.FLNetChannel;
import szewek.mcflux.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public final class MCFluxNetwork {
	private static FLNetChannel CHAN;

	public static void registerAll() {
		CHAN = new FLNetChannel(R.MF_NAME, Arrays.asList(
				Msg.Update.class,
				Msg.NewVersion.class,
				Msg.FluidAmount.class
		));
	}

	public static void to(Msg msg, EntityPlayerMP mp) {
		CHAN.to(msg, mp);
	}

	public static void toAll(Msg msg) {
		CHAN.toAll(msg);
	}

	public static void toAllAround(Msg msg, NetworkRegistry.TargetPoint tp) {
		CHAN.toAllAround(msg, tp);
	}

	public static void toDimension(Msg msg, int dim) {
		CHAN.toDimension(msg, dim);
	}

	public static void toServer(Msg msg) {
		CHAN.toServer(msg);
	}

	public static JsonObject downloadGistJSON(String hash, String name) throws IOException {
		final URL url = new URL("https", "gist.githubusercontent.com", 443, "/Szewek/" + hash + "/raw/" + name, null);
		final HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		final InputStreamReader isr = new InputStreamReader(huc.getInputStream());
		return new JsonParser().parse(isr).getAsJsonObject();
	}

	private MCFluxNetwork() {}
}
