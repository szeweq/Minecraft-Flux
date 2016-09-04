package ic2.core.block;

import ic2.core.block.comp.TileEntityComponent;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlock extends TileEntity {
	public <T extends TileEntityComponent> T getComponent(Class<T> cls) {
		return null;
	}
}
