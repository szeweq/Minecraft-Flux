package szewek.mcflux.proxy;

import net.minecraft.entity.player.EntityPlayer;
import szewek.mcflux.network.Msg;

public class ProxyCommon {
	public void preInit() {}

	public void init() {}

	public void processMsg(Msg msg, EntityPlayer p) {
		msg.msgServer(p);
	}

	public String side() {
		return "SERVER";
	}
}
