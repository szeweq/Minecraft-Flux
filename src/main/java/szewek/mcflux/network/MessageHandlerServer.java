package szewek.mcflux.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.L;
import szewek.mcflux.MCFlux;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

public class MessageHandlerServer implements IMessageHandler<UpdateMessageClient, IMessage> {
	@Override public IMessage onMessage(final UpdateMessageClient message, MessageContext ctx) {
		if (ctx.side != Side.SERVER) {
			L.warn("Why this ran on a client?");
			return null;
		}
		if (message.isBroken()) {
			L.warn("The message from client is broken");
			return null;
		}
		EntityPlayerMP mp = ctx.getServerHandler().playerEntity;
		mp.getServerWorld().addScheduledTask(() -> this.respond(message.getPos(), mp));
		return null;
	}

	private void respond(BlockPos bp, EntityPlayerMP mp) {
		TileEntity te = mp.world.getTileEntity(bp);
		if (te instanceof TileEntityEnergyMachine) {
			MCFlux.SNW.sendTo(new UpdateMessageServer(bp, ((TileEntityEnergyMachine) te).getAllTransferSides()), mp);
		}
	}
}
