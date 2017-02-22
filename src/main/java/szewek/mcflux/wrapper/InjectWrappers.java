package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import szewek.mcflux.L;
import szewek.mcflux.U;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.error.ErrMsgNullInject;
import szewek.mcflux.util.error.ErrMsgNullWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public enum InjectWrappers {
	EVENTS;
	private static final WrapperThread wth = new WrapperThread();
	private static InjectCollector collect = new InjectCollector();
	private static IWrapperInject<TileEntity>[] injTile = null;
	private static IWrapperInject<ItemStack>[] injItem = null;
	private static IWrapperInject<Entity>[] injEntity = null;
	private static final List<MCFluxWrapper> wrappers = Collections.synchronizedList(new ArrayList<>());

	public static InjectCollector getCollector() {
		return collect;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		injTile = collect.tileInjects.toArray(new IWrapperInject[collect.tileInjects.size()]);
		injItem = collect.itemInjects.toArray(new IWrapperInject[collect.itemInjects.size()]);
		injEntity = collect.entityInjects.toArray(new IWrapperInject[collect.entityInjects.size()]);
		collect = null;
		wth.start();
		L.info("Tile[" + injTile.length + "]; Item[" + injItem.length + "]; Entity[" + injEntity.length + "]");
	}

	private static final class WrapperThread extends Thread {

		WrapperThread() {
			super("MCFlux WrapperThread");
		}

		@Override public void run() {
			while (isAlive()) {
				try {
					synchronized (this) {
						wait(0);
					}
					synchronized (wrappers) {
						long tc = MCFluxReport.measureTime("WrapperThread: " + wrappers.size());
						for (MCFluxWrapper w : wrappers)
							if (w == null)
								MCFluxReport.addErrMsg(new ErrMsgNullWrapper(false));
							else if (w.mainObject == null)
								MCFluxReport.addErrMsg(new ErrMsgNullWrapper(true));
							else if (w.mainObject instanceof TileEntity)
								findWrappers(w, injTile);
							else if (w.mainObject instanceof ItemStack)
								findWrappers(w, injItem);
							else if (w.mainObject instanceof Entity)
								findWrappers(w, injEntity);
						wrappers.clear();
						MCFluxReport.stopTimer(tc);
					}
				} catch (Exception e) {
					MCFluxReport.sendException(e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void findWrappers(MCFluxWrapper w, IWrapperInject<T>[] iwis) {
		T t = (T) w.mainObject;
		if (t == null)
			return;
		long tc = MCFluxReport.measureTime("findWrappers(" + t.getClass().getName() + ")");
		WrapperRegistry reg = new WrapperRegistry();
		for (IWrapperInject<T> iwi : iwis)
			iwi.injectWrapper(t, reg);
		reg.resolve(EnergyType.ALL);
		w.addWrappers(reg.resultMap);
		MCFluxReport.stopTimer(tc);
	}

	private static void wrap(AttachCapabilitiesEvent<?> att) {
		long d = System.nanoTime();
		Object t = att.getObject();
		if (t == null) {
			MCFluxReport.addErrMsg(new ErrMsgNullInject((Class<?>) att.getGenericType()));
			return;
		}
		String cn = t.getClass().getName();
		if (cn.startsWith("szewek.") || cn.startsWith("net.minecraft."))
			return;
		long tc = MCFluxReport.measureTime("wrap(" + t.getClass().getName() + ")");
		MCFluxWrapper w = new MCFluxWrapper(t);
		att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
		wrappers.add(w);
		MCFluxReport.stopTimer(tc);
	}

	@SubscribeEvent
	public void wrapTile(AttachCapabilitiesEvent<TileEntity> att) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START))
			wrap(att);
	}

	@SubscribeEvent
	public void wrapStack(AttachCapabilitiesEvent<ItemStack> att) {
		if (!U.isItemEmpty(att.getObject()) && Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START))
			wrap(att);
	}

	@SubscribeEvent
	public void wrapEntity(AttachCapabilitiesEvent<Entity> att) {
		Entity ent = att.getObject();
		if (ent instanceof EntityItem)
			return;
		if (ent.world.getDifficulty() == EnumDifficulty.PEACEFUL && ent instanceof EntityMob)
			return;
		wrap(att);
	}

	@SubscribeEvent
	public void wrapItem(AttachCapabilitiesEvent.Item ei) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
			long tc = MCFluxReport.measureTime("wrapItem");
			ItemStack is = ei.getItemStack();
			if (!U.isItemEmpty(is)) {
				MCFluxWrapper w = new MCFluxWrapper(is);
				ei.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
				wrappers.add(w);
			}
			MCFluxReport.stopTimer(tc);
		}
	}

	@SubscribeEvent
	public void updateWrappers(TickEvent.WorldTickEvent wte) {
		if (wrappers.isEmpty())
			return;
		if (wte.phase == TickEvent.Phase.START && wrappers.size() > 0)
			synchronized (wth) {
				wth.notify();
			}
	}
}
