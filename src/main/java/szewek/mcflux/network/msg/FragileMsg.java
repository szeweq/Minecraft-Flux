package szewek.mcflux.network.msg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public abstract class FragileMsg {
	protected boolean broken = true;

	public FragileMsg() {}

	boolean isBroken() {
		return broken;
	}

	public abstract void processMsg(PacketBuffer pb, EntityPlayer p, Side s) throws IOException;
	public abstract void saveBuffer(PacketBuffer pb) throws IOException;
}
