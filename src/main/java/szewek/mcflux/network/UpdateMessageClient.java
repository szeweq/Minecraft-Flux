package szewek.mcflux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.L;

public class UpdateMessageClient extends FragileMessage {
	private BlockPos pos = null;

	public UpdateMessageClient(BlockPos bp) {
		pos = bp;
		broken = false;
	}

	public UpdateMessageClient() {}

	BlockPos getPos() {
		return pos;
	}

	@Override public void fromBytes(ByteBuf buf) {
		if (buf.readableBytes() < 8) {
			L.warn("Incomplete UMC");
			return;
		}
		pos = BlockPos.fromLong(buf.readLong());
		broken = false;
	}

	@Override public void toBytes(ByteBuf buf) {
		if (broken) return;
		buf.writeLong(pos.toLong());
	}
}
