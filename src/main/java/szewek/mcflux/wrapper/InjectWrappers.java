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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import szewek.mcflux.U;
import szewek.mcflux.util.ErrorReport;
import szewek.mcflux.util.error.ErrMsgNullInject;
import szewek.mcflux.util.error.ErrMsgNullWrapper;
import szewek.mcflux.util.error.ErrMsgThrownException;

import java.util.*;

@SuppressWarnings("unused")
public enum InjectWrappers {
	EVENTS;
	private static InjectCollector collect = new InjectCollector();
	private static IWrapperInject<TileEntity>[] injTile = null;
	private static IWrapperInject<ItemStack>[] injItem = null;
	private static IWrapperInject<Entity>[] injEntity = null;
	private static IWrapperInject<World>[] injWorld = null;
	private static List<MCFluxWrapper> wrappers = new ArrayList<>();

	public static InjectCollector getCollector() {
		return collect;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		injTile = collect.tileInjects.toArray(new IWrapperInject[collect.tileInjects.size()]);
		injItem = collect.itemInjects.toArray(new IWrapperInject[collect.itemInjects.size()]);
		injEntity = collect.entityInjects.toArray(new IWrapperInject[collect.entityInjects.size()]);
		injWorld = collect.worldInjects.toArray(new IWrapperInject[collect.worldInjects.size()]);
		collect = null;
	}

	public static class Registry {
		final Map<String, ICapabilityProvider> resultMap = new HashMap<>();
		private final Map<EnergyType, ICapabilityProvider> energyCapMap;

		private Registry() {
			energyCapMap = new EnumMap<>(EnergyType.class);
		}

		public void add(EnergyType et, ICapabilityProvider icp) {
			energyCapMap.put(et, icp);
		}

		void resolve(EnergyType[] ets) {
			for (EnergyType et : ets) {
				ICapabilityProvider icp = energyCapMap.get(et);
				if (icp != null) {
					resultMap.put(et.loc.toString(), icp);
					return;
				}
			}
		}

		public void register(ResourceLocation rl, ICapabilityProvider icp) {
			resultMap.put(rl.toString(), icp);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void findWrappers(MCFluxWrapper w, IWrapperInject<T>[] iwis) {
		T t = (T) w.mainObject;
		if (t == null)
			return;
		Registry reg = new Registry();
		for (IWrapperInject<T> iwi : iwis)
			iwi.injectWrapper(t, reg);
		reg.resolve(EnergyType.ALL);
		w.addWrappers(reg.resultMap);
	}

	private static void wrap(AttachCapabilitiesEvent<?> att) {
		Object t = att.getObject();
		if (t == null) {
			ErrorReport.addErrMsg(new ErrMsgNullInject((Class<?>) att.getGenericType()));
			return;
		}
		MCFluxWrapper w = new MCFluxWrapper(t);
		att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
		wrappers.add(w);

	}

	@SubscribeEvent
	public void wrapTile(AttachCapabilitiesEvent<TileEntity> att) {
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
			wrap(att);
	}

	@SubscribeEvent
	public void wrapStack(AttachCapabilitiesEvent<ItemStack> att) {
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
			wrap(att);
	}

	@SubscribeEvent
	public void wrapEntity(AttachCapabilitiesEvent<Entity> att) {
		wrap(att);
	}

	@SubscribeEvent
	public void wrapWorld(AttachCapabilitiesEvent<World> att) {
		wrap(att);
	}

	@SubscribeEvent
	public void wrapItem(AttachCapabilitiesEvent.Item ei) {
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
			ItemStack is = ei.getItemStack();
			if (U.isItemEmpty(is)) {
				ErrorReport.addErrMsg(new ErrMsgNullInject(ItemStack.class));
				return;
			}
			MCFluxWrapper w = new MCFluxWrapper(is);
			ei.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
			wrappers.add(w);
		}
	}

	@SubscribeEvent
	public void updateWrappers(TickEvent.WorldTickEvent wte) {
		if (wrappers.isEmpty())
			return;
		if (wte.phase == TickEvent.Phase.START)
			try {
				MCFluxWrapper[] ws = wrappers.toArray(new MCFluxWrapper[wrappers.size()]);
				wrappers.clear();
				for (MCFluxWrapper w : ws) {
					if (w == null)
						ErrorReport.addErrMsg(new ErrMsgNullWrapper(false));
					else if (w.mainObject == null)
						ErrorReport.addErrMsg(new ErrMsgNullWrapper(true));
					else if (w.mainObject instanceof TileEntity)
						findWrappers(w, injTile);
					else if (w.mainObject instanceof ItemStack)
						findWrappers(w, injItem);
					else if (w.mainObject instanceof Entity)
						findWrappers(w, injEntity);
					else if (w.mainObject instanceof World)
						findWrappers(w, injWorld);
				}
			} catch (Exception x) {
				ErrorReport.addErrMsg(new ErrMsgThrownException(x));
			}
	}
}
