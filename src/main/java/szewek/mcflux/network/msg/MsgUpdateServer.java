package szewek.mcflux.network.msg;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.L;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.util.TransferType;

import java.io.IOException;

public class MsgUpdateServer extends FragileMsg {
	private BlockPos pos = null;
	private TransferType[] sides = null;

	public static MsgUpdateServer with(BlockPos bp, TransferType[] tts) {
		MsgUpdateServer mus = new MsgUpdateServer();
		mus.pos = bp;
		mus.sides = tts;
		mus.broken = false;
		return mus;
	}

	@Override public void processMsg(PacketBuffer pb, EntityPlayer p, Side s) throws IOException {
		if (pb.readableBytes() < 14) {
			L.warn("Incomplete UMS");
			return;
		}
		pos = BlockPos.fromLong(pb.readLong());
		sides = new TransferType[6];
		TransferType[] ttv = TransferType.values();
		for (int i = 0; i < 6; i++)
			sides[i] = ttv[pb.readByte()];
		broken = false;
		if (s == Side.CLIENT) {
			Minecraft mc = Minecraft.getMinecraft();
			TileEntity te = mc.world.getTileEntity(pos);
			if (te != null && te instanceof TileEntityEnergyMachine)
				((TileEntityEnergyMachine) te).updateTransferSides(sides);
		}
	}

	@Override public void saveBuffer(PacketBuffer pb) throws IOException {
		if (broken)
			return;
		pb.writeLong(pos.toLong());
		for (int i = 0; i < 6; i++)
			pb.writeByte(sides[i].ord);
	}
}
