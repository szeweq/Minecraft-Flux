package szewek.mcflux.fluxable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.util.MCFluxLocation;

public enum InjectFluxable {
	INSTANCE;
	public static final MCFluxLocation
		ENERGY_PLAYER = new MCFluxLocation("PlayerEnergy"),
		ENERGY_ACTION = new MCFluxLocation("ActionEnergy"),
		ENERGY_WORLD_CHUNK = new MCFluxLocation("WorldChunkEnergy"),
		ENERGY_FURNACE = new MCFluxLocation("FurnaceEnergy"),
		ENERGY_MOB_SPAWNER = new MCFluxLocation("MobSpawnerEnergy");
	
	@SubscribeEvent
	public void inject(AttachCapabilitiesEvent e) {
		if (e instanceof AttachCapabilitiesEvent.TileEntity) {
			AttachCapabilitiesEvent.TileEntity ete = (AttachCapabilitiesEvent.TileEntity) e;
			TileEntity te = ete.getTileEntity();
			if (te instanceof TileEntityFurnace)
				ete.addCapability(ENERGY_FURNACE, new FurnaceEnergy((TileEntityFurnace) te));
			else if (te instanceof TileEntityMobSpawner)
				ete.addCapability(ENERGY_MOB_SPAWNER, new MobSpawnerEnergy((TileEntityMobSpawner) te));
		} else if (e instanceof AttachCapabilitiesEvent.Entity) {
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
