package szewek.mcflux.fluxable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;

import java.util.function.BiConsumer;

public class InjectFluxable {
	private static final MCFluxLocation
		ENERGY_PLAYER = new MCFluxLocation("PlayerEnergy"),
		ENERGY_ACTION = new MCFluxLocation("ActionEnergy"),
		ENERGY_WORLD_CHUNK = new MCFluxLocation("WorldChunkEnergy"),
		ENERGY_FURNACE = new MCFluxLocation("FurnaceEnergy"),
		ENERGY_MOB_SPAWNER = new MCFluxLocation("MobSpawnerEnergy");

	public static void registerWrappers() {
		InjectWrappers.registerTileWrapperInject(InjectFluxable::tileWrappers);
		InjectWrappers.registerEntityWrapperInject(InjectFluxable::entityWrappers);
		InjectWrappers.registerWorldWrapperInject(InjectFluxable::worldWrappers);
	}

	private static void tileWrappers(TileEntity te, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		if (te instanceof TileEntityFurnace)
			add.accept(ENERGY_FURNACE, new FurnaceEnergy((TileEntityFurnace) te));
		else if (te instanceof TileEntityMobSpawner)
			add.accept(ENERGY_MOB_SPAWNER, new MobSpawnerEnergy((TileEntityMobSpawner) te));
	}
	private static void entityWrappers(Entity ntt, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		if (ntt instanceof EntityPlayer)
			add.accept(ENERGY_PLAYER, new PlayerEnergy((EntityPlayer) ntt));
		else if (ntt instanceof EntityPig || ntt instanceof EntityCreeper)
			add.accept(ENERGY_ACTION, new EntityActionEnergy((EntityCreature) ntt));
	}
	private static void worldWrappers(World w, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		add.accept(ENERGY_WORLD_CHUNK, new WorldChunkEnergy());
	}
}
