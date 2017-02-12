package szewek.mcflux.network.msg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.L;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import java.io.IOException;

public final class MsgUpdateClient extends FragileMsg {
	private BlockPos pos;

	public static MsgUpdateClient with(BlockPos bp) {
		MsgUpdateClient muc = new MsgUpdateClient();
		muc.pos = bp;
		muc.broken = false;
		return muc;
	}

	@Override public void processMsg(PacketBuffer pb, EntityPlayer p, Side s) throws IOException {
		if (pb.readableBytes() < 8) {
			L.warn("Incomplete UMC");
			return;
		}
		pos = BlockPos.fromLong(pb.readLong());
		broken = false;
		if (s == Side.SERVER) {
			EntityPlayerMP mp = (EntityPlayerMP) p;
			if (mp != null) {
				TileEntity te = mp.worldObj.getTileEntity(pos);
				if (te != null && te instanceof TileEntityEnergyMachine)
					MCFluxNetwork.to(MsgUpdateServer.with(pos, ((TileEntityEnergyMachine) te).getAllTransferSides()), mp);
			}
		}
	}

	@Override public void saveBuffer(PacketBuffer pb) throws IOException {
		if (broken)
			return;
		pb.writeLong(pos.toLong());
	}
}
