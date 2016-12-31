package szewek.mcflux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.L;
import szewek.mcflux.util.TransferType;

public class UpdateMessageServer extends FragileMessage {
	private BlockPos pos = null;
	private TransferType[] sides = null;

	public UpdateMessageServer(BlockPos bp, TransferType[] tts) {
		pos = bp;
		sides = tts;
		broken = false;
	}

	public UpdateMessageServer() {}

	BlockPos getPos() {
		return pos;
	}

	TransferType[] getSides() {
		return sides;
	}

	@Override public void fromBytes(ByteBuf buf) {
		if (buf.readableBytes() < 14) {
			L.warn("Incomplete UMS");
			return;
		}
		pos = BlockPos.fromLong(buf.readLong());
		sides = new TransferType[6];
		TransferType[] ttv = TransferType.values();
		for (int i = 0; i < 6; i++)
			sides[i] = ttv[buf.readByte()];
		broken = false;
	}

	@Override public void toBytes(ByteBuf buf) {
		if (broken)
			return;
		buf.writeLong(pos.toLong());
		for (int i = 0; i < 6; i++)
			buf.writeByte(sides[i].ord);
	}
}
