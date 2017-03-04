package szewek.mcflux;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import szewek.mcflux.fluxable.*;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.network.Msg;
import szewek.mcflux.special.SpecialEventHandler;
import szewek.mcflux.special.SpecialEventReceiver;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.util.MCFluxReport;

@SuppressWarnings("unused") enum MCFluxEvents {
	INSTANCE;

	private static final MCFluxLocation
			MF_WORLD_CHUNK = new MCFluxLocation("wce"),
			MF_SER = new MCFluxLocation("ser"),
			MF_PLAYER = new MCFluxLocation("PlayerEnergy"),
			MF_ACTION = new MCFluxLocation("ActionEnergy"),
			MF_FURNACE = new MCFluxLocation("FurnaceEnergy"),
			MF_MOB_SPAWNER = new MCFluxLocation("MobSpawnerEnergy");

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent e) {
		ResourceLocation rl = e.getName();
		if (rl.equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH) || rl.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || rl.equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
			LootTable lt = e.getTable();
			LootPool lp = lt.getPool("pool0");
			if (lp == null)
				lp = lt.getPool("main");
			if (lp != null) {
				lp.addEntry(new LootEntryItem(MCFluxResources.UPCHIP, 20, 0, new LootFunction[0], new LootCondition[0], "mcflux:loot/upchip"));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		if (MCFlux.UPDATE_CHECK_FINISHED && !MCFlux.NEWER_VERSION.isEmpty() && e.player instanceof EntityPlayerMP)
			MCFluxNetwork.to(Msg.newVersion(MCFlux.NEWER_VERSION), (EntityPlayerMP) e.player);
		if (SpecialEventHandler.getEventStatus() == SpecialEventHandler.EventStatus.DOWNLOADED) {
			SpecialEventReceiver ser = e.player.getCapability(SpecialEventReceiver.SELF_CAP, null);
			if (ser != null) {
				long[] seids = SpecialEventHandler.getEventIDs();
				for (long l : seids) {
					if (ser.alreadyReceived(l))
						continue;
					ItemStack is = new ItemStack(MCFluxResources.SPECIAL);
					is.setTagInfo("seid", new NBTTagLong(l));
					e.player.dropItem(is, false, true);
					ser.addReceived(l);
				}
			}
		}
	}

	@SubscribeEvent
	public void whyCantPlayerSleep(PlayerSleepInBedEvent e) {
		EntityPlayer p = e.getEntityPlayer();
		int l = p.world.getLightFor(EnumSkyBlock.BLOCK, e.getPos());
		if (l > 9) {
			e.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
			p.sendStatusMessage(new TextComponentTranslation("mcflux.sleep.tooBright"), true);
		}
	}

	@SubscribeEvent
	public void wrapWCE(AttachCapabilitiesEvent<World> e) {
		e.addCapability(MF_WORLD_CHUNK, new WorldChunkEnergy());
	}

	@SubscribeEvent
	public void wrapEntity(AttachCapabilitiesEvent<Entity> e) {
		Entity ent = e.getObject();
		if (ent instanceof EntityPlayer) {
			e.addCapability(MF_SER, new SpecialEventReceiver());
			e.addCapability(MF_PLAYER, new PlayerEnergy());
		} else if (ent.world == null) {
			MCFluxReport.addErrMsg(new ErrMsg.NoEntityWorld(ent.getClass()));
		} else if (ent instanceof EntityPig || (ent.world.getDifficulty() != EnumDifficulty.PEACEFUL && ent instanceof EntityCreeper)) {
			e.addCapability(MF_ACTION, new EntityActionEnergy((EntityCreature) ent));
		}
	}

	@SubscribeEvent
	public void wrapTile(AttachCapabilitiesEvent<TileEntity> e) {
		TileEntity te = e.getObject();
		if (te instanceof TileEntityFurnace)
			e.addCapability(MF_FURNACE, new FurnaceEnergy((TileEntityFurnace) te));
		else if (te instanceof TileEntityMobSpawner)
			e.addCapability(MF_MOB_SPAWNER, new MobSpawnerEnergy((TileEntityMobSpawner) te));
	}
}
