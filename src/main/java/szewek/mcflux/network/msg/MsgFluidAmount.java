package szewek.mcflux.network.msg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.tileentities.TileEntityFluxGen;

import java.io.IOException;

public class MsgFluidAmount extends FragileMsg {
	private BlockPos pos;
	private Fluid fluid = null;
	private int id, amount;

	public static MsgFluidAmount with(BlockPos bp, int id, FluidStack fs) {
		MsgFluidAmount mfa = new MsgFluidAmount();
		mfa.pos = bp;
		mfa.id = id;
		if (fs == null) {
			mfa.amount = 0;
		} else {
			mfa.fluid = fs.getFluid();
			mfa.amount = fs.amount;
		}
		mfa.broken = false;
		return mfa;
	}

	@Override public void processMsg(PacketBuffer pb, EntityPlayer p, Side s) throws IOException {
		pos = BlockPos.fromLong(pb.readLong());
		id = pb.readInt();
		amount = pb.readInt();
		if (amount > 0)
			fluid = FluidRegistry.getFluid(pb.readString(32767));
		broken = false;
		if (s == Side.CLIENT) {
			TileEntity te = p.world.getTileEntity(pos);
			if (te instanceof TileEntityFluxGen)
				((TileEntityFluxGen) te).updateFluid(id, fluid, amount);
		}
	}

	@Override public void saveBuffer(PacketBuffer pb) throws IOException {
		if (broken)
			return;
		pb.writeLong(pos.toLong());
		pb.writeInt(id);
		pb.writeInt(amount);
		if (amount > 0)
			pb.writeString(fluid.getName());
	}
}
