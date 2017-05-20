package szewek.mcflux.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.MCFlux;
import szewek.mcflux.R;
import szewek.mcflux.util.MCFluxReport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public enum MCFluxNetwork {
	NET;

	private static FMLEventChannel chan;
	private static List<Class<? extends Msg>> ids = new ArrayList<>();

	public static void registerAll() {
		chan = NetworkRegistry.INSTANCE.newEventDrivenChannel(R.MF_NAME);
		chan.register(NET);
		ids.add(0, Msg.Update.class);
		ids.add(1, Msg.NewVersion.class);
		ids.add(2, Msg.FluidAmount.class);
	}

	public static void to(Msg msg, EntityPlayerMP mp) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendTo(pp, mp);
	}

	public static void toAll(Msg msg) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToAll(pp);
	}

	public static void toAllAround(Msg msg, NetworkRegistry.TargetPoint tp) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToAllAround(pp, tp);
	}

	public static void toDimension(Msg msg, int dim) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToDimension(pp, dim);
	}

	public static void toServer(Msg msg) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToServer(pp);
	}

	private static FMLProxyPacket makePacket(Msg fmsg) {
		PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
		byte id = (byte) ids.indexOf(fmsg.getClass());
		pb.writeByte(id);
		try {
			fmsg.encode(pb);
		} catch (Exception e) {
			MCFluxReport.sendException(e, "Creating a packet");
			return null;
		}
		return new FMLProxyPacket(pb, R.MF_NAME);
	}

	private static void processMsg(FMLProxyPacket pp, final EntityPlayer p, IThreadListener mc, final Side s) {
		PacketBuffer pb = (PacketBuffer) pp.payload();
		if (pb == null)
			pb = new PacketBuffer(pp.payload());
		try {
			byte id = pb.readByte();
			final Msg fmsg = ids.get(id).newInstance();
			if (!mc.isCallingFromMinecraftThread()) {
				mc.addScheduledTask(new DecodeMsg(fmsg, pb, p, s));
			}
		} catch (Exception x) {
			MCFluxReport.sendException(x, "Processing message (" + s + "-side)");
		}
	}

	public static JsonObject downloadGistJSON(String hash, String name) throws IOException {
		URL url = new URL("https", "gist.githubusercontent.com", 443, "/Szewek/" + hash + "/raw/" + name, null);
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		InputStreamReader isr = new InputStreamReader(huc.getInputStream());
		return new JsonParser().parse(isr).getAsJsonObject();
	}

	@SubscribeEvent
	public void serverPacket(FMLNetworkEvent.ServerCustomPacketEvent e) {
		EntityPlayer p = ((NetHandlerPlayServer) e.getHandler()).player;
		processMsg(e.getPacket(), p, p.getServer(), Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void clientPacket(FMLNetworkEvent.ClientCustomPacketEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		processMsg(e.getPacket(), mc.player, mc, Side.CLIENT);
	}

	static final class DecodeMsg implements java.lang.Runnable {
		private final Msg msg;
		private final PacketBuffer pbuf;
		private final EntityPlayer player;
		private final Side side;

		DecodeMsg(Msg fm, PacketBuffer pb, EntityPlayer p, Side s) {
			msg = fm;
			pbuf = pb;
			player = p;
			side = s;
		}

		public void run() {
			try {
				msg.decode(pbuf);
				MCFlux.PROXY.processMsg(msg, player);
			} catch (Exception e) {
				MCFluxReport.sendException(e, "Decoding message (" + side + "-side) with PROXY " + MCFlux.PROXY.side());
			}
		}
	}
}
