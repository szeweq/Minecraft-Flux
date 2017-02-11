package szewek.mcflux.fluxable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectCollector;
import szewek.mcflux.wrapper.InjectWrappers;

public class InjectFluxable {
	private static final MCFluxLocation
			MF_PLAYER = new MCFluxLocation("PlayerEnergy"),
			MF_ACTION = new MCFluxLocation("ActionEnergy"),
			MF_WORLD_CHUNK = new MCFluxLocation("wce"),
			MF_FURNACE = new MCFluxLocation("FurnaceEnergy"),
			MF_MOB_SPAWNER = new MCFluxLocation("MobSpawnerEnergy");

	public static void registerWrappers() {
		InjectCollector ic = InjectWrappers.getCollector();
		if (ic == null)
			return;
		ic.addTileWrapperInject(InjectFluxable::tileWrappers);
		ic.addEntityWrapperInject(InjectFluxable::entityWrappers);
		ic.addWorldWrapperInject(InjectFluxable::worldWrappers);
	}

	private static void tileWrappers(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof TileEntityFurnace)
			reg.register(MF_FURNACE, new FurnaceEnergy((TileEntityFurnace) te));
		else if (te instanceof TileEntityMobSpawner)
			reg.register(MF_MOB_SPAWNER, new MobSpawnerEnergy((TileEntityMobSpawner) te));
	}
	private static void entityWrappers(Entity ntt, InjectWrappers.Registry reg) {
		if (ntt instanceof EntityPlayer)
			reg.register(MF_PLAYER, new PlayerEnergy((EntityPlayer) ntt));
		else if (ntt instanceof EntityPig || ntt instanceof EntityCreeper)
			reg.register(MF_ACTION, new EntityActionEnergy((EntityCreature) ntt));
	}
	private static void worldWrappers(World w, InjectWrappers.Registry reg) {
		reg.register(MF_WORLD_CHUNK, new WorldChunkEnergy());
	}
}
