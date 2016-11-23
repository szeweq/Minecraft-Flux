package szewek.mcflux.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.L;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.util.TransferType;

public class MessageHandlerClient implements IMessageHandler<UpdateMessageServer, IMessage> {
	@Override public IMessage onMessage(UpdateMessageServer message, MessageContext ctx) {
		if (ctx.side != Side.CLIENT) {
			L.warn("Why this ran on a server?");
			return null;
		}
		if (message.isBroken()) {
			L.warn("Message from server is broken");
			return null;
		}
		Minecraft mc = Minecraft.getMinecraft();
		mc.addScheduledTask(() -> process(mc.world, message.getPos(), message.getSides()));
		return null;
	}

	private void process(final World w, BlockPos bp, TransferType[] tts) {
		TileEntity te = w.getTileEntity(bp);
		if (te instanceof TileEntityEnergyMachine) {
			((TileEntityEnergyMachine) te).updateTransferSides(tts);
		}
	}
}
