package szewek.mcflux.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.L;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.util.TransferType;

import java.io.IOException;

public abstract class Msg {
	protected boolean broken = true;

	public abstract void decode(PacketBuffer pb) throws IOException;
	public abstract void encode(PacketBuffer pb) throws IOException;
	public void msgServer(EntityPlayer p) {}
	@SideOnly(Side.CLIENT) public void msgClient(EntityPlayer p) {}

	public static Msg update(BlockPos bp, TransferType[] tts) {
		Update m = new Update();
		m.pos = bp;
		m.sides = tts;
		m.broken = false;
		return m;
	}

	public static Msg newVersion(String v) {
		NewVersion m = new NewVersion();
		m.version = v;
		m.broken = false;
		return m;
	}

	public static Msg fluidAmount(BlockPos bp, int id, FluidStack fs) {
		FluidAmount m = new FluidAmount();
		m.pos = bp;
		m.id = id;
		if (fs == null)
			m.amount = 0;
		else {
			m.fluid = fs.getFluid();
			m.amount = fs.amount;
		}
		m.broken = false;
		return m;
	}

	static class Update extends Msg {
		private BlockPos pos = null;
		private TransferType[] sides = null;

		@Override public void decode(PacketBuffer pb) throws IOException {
			if (pb.readableBytes() < 8) {
				L.warn("Msg.Update incomplete");
				return;
			}
			pos = BlockPos.fromLong(pb.readLong());
			if (pb.readableBytes() == 14) {
				sides = new TransferType[6];
				TransferType[] ttv = TransferType.values();
				for (int i = 0; i < 6; i++)
					sides[i] = ttv[pb.readByte()];
			}
			broken = false;
		}

		@Override public void encode(PacketBuffer pb) throws IOException {
			pb.writeLong(pos.toLong());
			if (sides != null)
				for (int i = 0; i < 6; i++)
					pb.writeByte(sides[i].ord);
		}

		@Override public void msgServer(EntityPlayer p) {
			EntityPlayerMP mp = (EntityPlayerMP) p;
			if (mp != null) {
				TileEntity te = mp.world.getTileEntity(pos);
				if (te != null && te instanceof TileEntityEnergyMachine)
					MCFluxNetwork.to(update(pos, ((TileEntityEnergyMachine) te).getAllTransferSides()), mp);
			}
		}

		@SideOnly(Side.CLIENT) @Override public void msgClient(EntityPlayer p) {
			TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
			if (te != null && te instanceof TileEntityEnergyMachine)
				((TileEntityEnergyMachine) te).updateTransferSides(sides);
		}
	}

	static class NewVersion extends Msg {
		private String version = null;

		@Override public void decode(PacketBuffer pb) throws IOException {
			if (pb.readableBytes() < 2) {
				L.warn("Incompatible length");
				return;
			}
			version = pb.readString(32);
			broken = false;
		}

		@Override public void encode(PacketBuffer pb) throws IOException {
			pb.writeString(version);
		}

		@SideOnly(Side.CLIENT) @Override public void msgClient(EntityPlayer p) {
			p.sendMessage(new TextComponentTranslation("mcflux.update.newversion", version));
		}
	}

	static class FluidAmount extends Msg {
		private BlockPos pos;
		private Fluid fluid = null;
		private int id, amount;

		@Override public void decode(PacketBuffer pb) throws IOException {
			pos = BlockPos.fromLong(pb.readLong());
			id = pb.readInt();
			amount = pb.readInt();
			if (amount > 0)
				fluid = FluidRegistry.getFluid(pb.readString(32767));
			broken = false;
		}

		@Override public void encode(PacketBuffer pb) throws IOException {
			pb.writeLong(pos.toLong());
			pb.writeInt(id);
			pb.writeInt(amount);
			if (amount > 0)
				pb.writeString(fluid.getName());
		}

		@SideOnly(Side.CLIENT) @Override public void msgClient(EntityPlayer p) {
			TileEntity te = p.world.getTileEntity(pos);
			if (te instanceof TileEntityFluxGen)
				((TileEntityFluxGen) te).updateFluid(id, fluid, amount);
		}
	}
}
