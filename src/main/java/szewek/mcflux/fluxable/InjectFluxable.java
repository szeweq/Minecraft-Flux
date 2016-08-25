package szewek.mcflux.fluxable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.R;

public enum InjectFluxable {
	INSTANCE;
	public static final ResourceLocation
		ENERGY_PLAYER = new ResourceLocation(R.MCFLUX_NAME, "PlayerEnergy"),
		ENERGY_PIG = new ResourceLocation(R.MCFLUX_NAME, "PigEnergy"),
		ENERGY_CREEPER = new ResourceLocation(R.MCFLUX_NAME, "CreeperEnergy"),
		ENERGY_WORLD_CHUNK = new ResourceLocation(R.MCFLUX_NAME, "WorldChunkEnergy");
	
	@SubscribeEvent
	public void inject(AttachCapabilitiesEvent e) {
		if (e instanceof AttachCapabilitiesEvent.Entity) {
			AttachCapabilitiesEvent.Entity ee = (AttachCapabilitiesEvent.Entity) e;
			Entity ent = ee.getEntity();
			if (ent instanceof EntityPlayer)
				ee.addCapability(ENERGY_PLAYER, new PlayerEnergy((EntityPlayer) ent));
			else if (ent instanceof EntityPig)
				ee.addCapability(ENERGY_PIG, new PigEnergy((EntityPig) ent));
			else if (ent instanceof EntityCreeper)
				ee.addCapability(ENERGY_CREEPER, new CreeperEnergy((EntityCreeper) ent));
		} else if (e instanceof AttachCapabilitiesEvent.World) {
			e.addCapability(ENERGY_WORLD_CHUNK, new WorldChunkEnergy());
		}
	}
}
