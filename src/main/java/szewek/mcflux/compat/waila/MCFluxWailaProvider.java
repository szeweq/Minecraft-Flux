package szewek.mcflux.compat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public final class MCFluxWailaProvider {
	@SuppressWarnings("unused")
	public static void callbackRegister(IWailaRegistrar reg) {
		DataProvider dp = new DataProvider();
		EntityProvider ep = new EntityProvider();
		reg.registerBodyProvider(dp, TileEntity.class);
		reg.registerBodyProvider(ep, EntityPlayer.class);
		reg.registerBodyProvider(ep, EntityPig.class);
		reg.registerBodyProvider(ep, EntityCreeper.class);
	}

}
