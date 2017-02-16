package szewek.mcflux;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
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
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.network.msg.MsgNewVersion;
import szewek.mcflux.special.SpecialEventHandler;
import szewek.mcflux.special.SpecialEventReceiver;
import szewek.mcflux.util.MCFluxLocation;

@SuppressWarnings("unused")
enum MCFluxEvents {
	INSTANCE;

	private static final MCFluxLocation MF_WORLD_CHUNK = new MCFluxLocation("wce"), MF_SER = new MCFluxLocation("ser");

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
			MCFluxNetwork.to(MsgNewVersion.with(MCFlux.NEWER_VERSION), (EntityPlayerMP) e.player);
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
		int l = p.world.getLight(e.getPos());
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
	public void wrapSER(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(MF_SER, new SpecialEventReceiver());
		}
	}
}
