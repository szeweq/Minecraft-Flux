package szewek.mcflux.network;

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
import szewek.mcflux.R;
import szewek.mcflux.network.msg.*;
import szewek.mcflux.util.MCFluxReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public enum MCFluxNetwork {
	NET;

	private static FMLEventChannel chan;
	private static List<Class<? extends FragileMsg>> ids = new ArrayList<>();

	public static void registerAll() {
		chan = NetworkRegistry.INSTANCE.newEventDrivenChannel(R.MF_NAME);
		chan.register(NET);
		ids.add(0, MsgUpdateClient.class);
		ids.add(1, MsgUpdateServer.class);
		ids.add(2, MsgNewVersion.class);
		ids.add(3, MsgFluidAmount.class);
	}

	public static void to(FragileMsg msg, EntityPlayerMP mp) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendTo(pp, mp);
	}

	public static void toAll(FragileMsg msg) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToAll(pp);
	}
	public static void toAllAround(FragileMsg msg, NetworkRegistry.TargetPoint tp) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToAllAround(pp, tp);
	}

	public static void toDimension(FragileMsg msg, int dim) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToDimension(pp, dim);
	}

	public static void toServer(FragileMsg msg) {
		FMLProxyPacket pp = makePacket(msg);
		if (pp != null)
			chan.sendToServer(pp);
	}

	private static FMLProxyPacket makePacket(FragileMsg fmsg) {
		PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
		byte id = (byte) ids.indexOf(fmsg.getClass());
		pb.writeByte(id);
		try {
			fmsg.saveBuffer(pb);
		} catch (Exception e) {
			MCFluxReport.sendException(e);
			return null;
		}
		return new FMLProxyPacket(pb, R.MF_NAME);
	}

	private static void processMsg(FMLProxyPacket pp, EntityPlayer p, IThreadListener mc, Side s) {
		PacketBuffer pb = (PacketBuffer) pp.payload();
		if (pb == null)
			pb = new PacketBuffer(pp.payload());
		try {
			byte id = pb.readByte();
			final FragileMsg fmsg = ids.get(id).newInstance();
			if (!mc.isCallingFromMinecraftThread()) {
				mc.addScheduledTask(new DecodeMsg(fmsg, pb, p, s));
			}
		} catch (Exception x) {
			MCFluxReport.sendException(x);
		}
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
		private final FragileMsg msg;
		private final PacketBuffer pbuf;
		private final EntityPlayer player;
		private final Side side;

		DecodeMsg(FragileMsg fm, PacketBuffer pb, EntityPlayer p, Side s) {
			msg = fm;
			pbuf = pb;
			player = p;
			side = s;
		}

		public void run() {
			try {
				msg.processMsg(pbuf, player, side);
			} catch (IOException e) {
				MCFluxReport.sendException(e);
			}
		}
	}
}
