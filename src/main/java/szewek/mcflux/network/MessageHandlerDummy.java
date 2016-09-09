package szewek.mcflux.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import szewek.mcflux.L;

public class MessageHandlerDummy implements IMessageHandler<UpdateMessageServer, IMessage> {
	@Override public IMessage onMessage(UpdateMessageServer message, MessageContext ctx) {
		L.warn("WRONG SIDE");
		return null;
	}
}
