package szewek.mcflux.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.fl.network.FLNetMsg;
import szewek.mcflux.R;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.TransferType;

import java.io.IOException;

public abstract class Msg extends FLNetMsg {

	@Override
	protected void exception(Exception x) {
		MCFluxReport.sendException(x, "Msg Exception from: " + this.getClass().getName());
	}

	public static Msg update(BlockPos bp, TransferType[] tts) {
		final Update m = new Update();
		m.pos = bp;
		m.sides = tts;
		m.broken = false;
		return m;
	}

	public static Msg newVersion(String v) {
		final NewVersion m = new NewVersion();
		m.version = v;
		m.broken = false;
		return m;
	}

	public static Msg fluidAmount(BlockPos bp, int id, FluidStack fs) {
		final FluidAmount m = new FluidAmount();
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

	public static final class Update extends Msg {
		private BlockPos pos = null;
		private TransferType[] sides = null;

		@Override
		protected void decode(PacketBuffer pb) throws IOException {
			final int rb = pb.readableBytes();
			if (rb != 8 && rb != 14)
				throw new IOException("Msg.Update incomplete - too few readable bytes");
			pos = BlockPos.fromLong(pb.readLong());
			if (rb == 14) {
				sides = new TransferType[6];
				TransferType[] ttv = TransferType.values();
				for (int i = 0; i < 6; i++)
					sides[i] = ttv[pb.readByte()];
			}
			broken = false;
		}

		@Override
		protected void encode(PacketBuffer pb) throws IOException {
			pb.writeLong(pos.toLong());
			if (sides != null)
				for (int i = 0; i < 6; i++)
					pb.writeByte(sides[i].ord);
		}

		@Override
		protected void srvmsg(EntityPlayer p) {
			EntityPlayerMP mp = (EntityPlayerMP) p;
			if (mp != null) {
				TileEntity te = mp.world.getTileEntity(pos);
				if (te != null && te instanceof TileEntityEnergyMachine)
					MCFluxNetwork.to(update(pos, ((TileEntityEnergyMachine) te).getAllTransferSides()), mp);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		protected void climsg(EntityPlayer p) {
			if (sides == null || sides.length != 6) {
				return;
			}
			TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
			if (te != null && te instanceof TileEntityEnergyMachine)
				((TileEntityEnergyMachine) te).updateTransferSides(sides);
		}
	}

	public static final class NewVersion extends Msg {
		private String version = null;

		@Override
		protected void decode(PacketBuffer pb) throws IOException {
			if (pb.readableBytes() < 2)
				throw new IOException("Msg.NewVersion incomplete - too few readable bytes");
			version = pb.readString(32);
			broken = false;
		}

		@Override
		protected void encode(PacketBuffer pb) throws IOException {
			pb.writeString(version);
		}

		@SideOnly(Side.CLIENT)
		@Override
		protected void climsg(EntityPlayer p) {
			final TextComponentTranslation tt = new TextComponentTranslation("mcflux.update.newversion", version);
			tt.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, R.MF_URL));
			p.sendMessage(tt);
		}
	}

	public static final class FluidAmount extends Msg {
		private BlockPos pos;
		private Fluid fluid = null;
		private int id, amount;

		@Override
		protected void decode(PacketBuffer pb) throws IOException {
			pos = BlockPos.fromLong(pb.readLong());
			id = pb.readInt();
			amount = pb.readInt();
			if (amount > 0)
				fluid = FluidRegistry.getFluid(pb.readString(32767));
			broken = false;
		}

		@Override
		protected void encode(PacketBuffer pb) throws IOException {
			pb.writeLong(pos.toLong());
			pb.writeInt(id);
			pb.writeInt(amount);
			if (amount > 0)
				pb.writeString(fluid.getName());
		}

		@Override
		protected void climsg(EntityPlayer p) {
			TileEntity te = p.world.getTileEntity(pos);
			if (te instanceof TileEntityFluxGen)
				((TileEntityFluxGen) te).updateFluid(id, fluid, amount);
		}
	}
}
