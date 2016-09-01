package szewek.mcflux.fluxable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
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
		ENERGY_PLAYER = new ResourceLocation(R.MF_NAME, "PlayerEnergy"),
		ENERGY_ACTION = new ResourceLocation(R.MF_NAME, "ActionEnergy"),
		ENERGY_WORLD_CHUNK = new ResourceLocation(R.MF_NAME, "WorldChunkEnergy");
	
	@SubscribeEvent
	public void inject(AttachCapabilitiesEvent e) {
		if (e instanceof AttachCapabilitiesEvent.Entity) {
			AttachCapabilitiesEvent.Entity ee = (AttachCapabilitiesEvent.Entity) e;
			Entity ent = ee.getEntity();
			if (ent instanceof EntityPlayer)
				ee.addCapability(ENERGY_PLAYER, new PlayerEnergy((EntityPlayer) ent));
			else if (ent instanceof EntityPig || ent instanceof EntityCreeper)
				ee.addCapability(ENERGY_ACTION, new EntityActionEnergy((EntityCreature) ent));
		} else if (e instanceof AttachCapabilitiesEvent.World) {
			e.addCapability(ENERGY_WORLD_CHUNK, new WorldChunkEnergy());
		}
	}
}
