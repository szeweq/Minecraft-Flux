package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.L;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum InjectWrappers {
	EVENTS;
	private static Set<IWrapperInject<TileEntity>> tileInjects = new HashSet<>();
	private static Set<IWrapperInject<ItemStack>> itemInjects = new HashSet<>();
	private static Set<IWrapperInject<Entity>> entityInjects = new HashSet<>();
	private static Set<IWrapperInject<World>> worldInjects = new HashSet<>();

	public static void registerTileWrapperInject(IWrapperInject<TileEntity> iwi) {
		tileInjects.add(iwi);
	}

	public static void registerItemWrapperInject(IWrapperInject<ItemStack> iwi) {
		itemInjects.add(iwi);
	}

	public static void registerEntityWrapperInject(IWrapperInject<Entity> iwi) {
		entityInjects.add(iwi);
	}

	public static void registerWorldWrapperInject(IWrapperInject<World> iwi) {
		worldInjects.add(iwi);
	}

	public static class Registry {
		public final Map<ResourceLocation, ICapabilityProvider> capMap;
		private final AttachCapabilitiesEvent event;
		private final Map<EnergyType, ICapabilityProvider> energyCapMap;

		private Registry(AttachCapabilitiesEvent<?> att) {
			event = att;
			capMap = att.getCapabilities();
			energyCapMap = new EnumMap<>(EnergyType.class);
		}

		public void add(EnergyType et, ICapabilityProvider icp) {
			energyCapMap.put(et, icp);
		}

		public void register(ResourceLocation rl, ICapabilityProvider icp) {
			event.addCapability(rl, icp);
		}
	}

	private static <T> void wrap(AttachCapabilitiesEvent<T> att, Iterable<IWrapperInject<T>> iwis) {
		T t = att.getObject();
		if (t == null) {
			L.warn("Cannot attach capabilities: Object is null");
			return;
		}
		Registry reg = new Registry(att);
		for (IWrapperInject<T> iwi : iwis)
			iwi.injectWrapper(t, reg);
		for (EnergyType et : EnergyType.ALL) {
			ICapabilityProvider icp = reg.energyCapMap.get(et);
			if (icp != null) {
				att.addCapability(et.loc, icp);
				break;
			}
		}
	}

	public static void wrapItem(AttachCapabilitiesEvent.Item ei, Iterable<IWrapperInject<ItemStack>> iwis) {
		ItemStack is = ei.getItemStack();
		if (is == null) {
			L.warn("Cannot attach capabilities: ItemStack is null");
			return;
		}
		Registry reg = new Registry(ei);
		for (IWrapperInject<ItemStack> iwi : iwis)
			iwi.injectWrapper(is, reg);
		for (EnergyType et : EnergyType.ALL) {
			ICapabilityProvider icp = reg.energyCapMap.get(et);
			if (icp != null) {
				ei.addCapability(et.loc, icp);
				break;
			}
		}
	}

	@SubscribeEvent
	public void wrapTile(AttachCapabilitiesEvent<TileEntity> att) {
		wrap(att, tileInjects);
	}

	@SubscribeEvent
	public void wrapStack(AttachCapabilitiesEvent<ItemStack> att) {
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
			wrap(att, itemInjects);
	}

	@SubscribeEvent
	public void wrapEntity(AttachCapabilitiesEvent<Entity> att) {
		wrap(att, entityInjects);
	}

	@SubscribeEvent
	public void wrapWorld(AttachCapabilitiesEvent<World> att) {
		wrap(att, worldInjects);
	}

	@SubscribeEvent
	public void wrapItem(AttachCapabilitiesEvent.Item ei) {
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
			wrapItem(ei, itemInjects);
	}
}
