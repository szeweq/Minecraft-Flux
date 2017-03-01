package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemBlock;
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
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.MCFluxReport;

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

	private static boolean blacklisted(Class<?> cl) {
		String cn = cl.getName();
		return cn.startsWith("szewek.") || cn.startsWith("net.minecraft.");
	}

	@SuppressWarnings("unchecked")
	private static <T> void findWrappers(MCFluxWrapper w, IWrapperInject<T>[] iwis) {
		T t = (T) w.mainObject;
		if (t == null)
			return;
		long tc = MCFluxReport.measureTime("findWrappers", t.getClass().getName());
		WrapperRegistry reg = new WrapperRegistry();
		for (IWrapperInject<T> iwi : iwis)
			iwi.injectWrapper(t, reg);
		reg.resolve(EnergyType.ALL);
		w.addWrappers(reg.resultMap);
		MCFluxReport.stopTimer(tc);
	}

	private static void wrap(AttachCapabilitiesEvent<?> att) {
		Object t = att.getObject();
		if (t == null) {
			MCFluxReport.addErrMsg(new ErrMsg.NullInject((Class<?>) att.getGenericType()));
			return;
		}
		Class<?> tcl = t.getClass();
		if (blacklisted(tcl))
			return;
		long tc = MCFluxReport.measureTime("wrap", tcl.getName());
		MCFluxWrapper w = new MCFluxWrapper(t);
		att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
		wrappers.add(w);
		MCFluxReport.stopTimer(tc);
	}

	private static void wrapItemStack(AttachCapabilitiesEvent<?> att, ItemStack is) {
		if (!U.isItemEmpty(is)) {
			Class<?> ic = is.getItem().getClass();
			if (blacklisted(ic) || ItemBlock.class.isAssignableFrom(ic))
				return;
			long tc = MCFluxReport.measureTime("wrapItem", ic.getName());
			MCFluxWrapper w = new MCFluxWrapper(is);
			att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
			wrappers.add(w);
			MCFluxReport.stopTimer(tc);
		}
	}

	@SubscribeEvent
	public void wrapTile(AttachCapabilitiesEvent<TileEntity> att) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START))
			wrap(att);
	}

	@SubscribeEvent
	public void wrapStack(AttachCapabilitiesEvent<ItemStack> att) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START))
			wrapItemStack(att, att.getObject());
	}

	@SubscribeEvent
	public void wrapEntity(AttachCapabilitiesEvent<Entity> att) {
		Entity ent = att.getObject();
		if (ent instanceof EntityItem || ent instanceof IProjectile || (ent.world.getDifficulty() == EnumDifficulty.PEACEFUL && ent instanceof EntityMob))
			return;
		wrap(att);
	}

	@SubscribeEvent
	public void wrapItem(AttachCapabilitiesEvent.Item ei) {
		if (Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START))
			wrapItemStack(ei, ei.getItemStack());
	}

	@SubscribeEvent
	public void updateWrappers(TickEvent.WorldTickEvent wte) {
		if (wte.phase == TickEvent.Phase.START && wrappers.size() > 0)
			synchronized (wth) {
				wth.notify();
			}
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
						long tc = MCFluxReport.measureTime("WrapperThread", wrappers.size());
						for (MCFluxWrapper w : wrappers)
							try {
								if (w == null)
									MCFluxReport.addErrMsg(new ErrMsg.NullWrapper(false));
								else if (w.mainObject == null)
									MCFluxReport.addErrMsg(new ErrMsg.NullWrapper(true));
								else if (w.mainObject instanceof TileEntity)
									findWrappers(w, injTile);
								else if (w.mainObject instanceof ItemStack)
									findWrappers(w, injItem);
								else if (w.mainObject instanceof Entity)
									findWrappers(w, injEntity);
							} catch (Exception e) {
								MCFluxReport.sendException(e, "Wrapping an object");
							}
						wrappers.clear();
						MCFluxReport.stopTimer(tc);
					}
				} catch (Exception e) {
					MCFluxReport.sendException(e, "WrapperThread loop");
				}
			}
		}
	}
}
