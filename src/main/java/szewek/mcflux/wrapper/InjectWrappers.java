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
import szewek.fl.FL;
import szewek.mcflux.L;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.util.ErrMsg;
import szewek.mcflux.util.MCFluxReport;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public enum InjectWrappers {
	EVENTS;
	private static final WrapperThread wth = new WrapperThread();
	private static InjectCollector collect = new InjectCollector.Impl();
	private static BiConsumer<TileEntity, WrapperRegistry>[] injTile = null;
	private static BiConsumer<ItemStack, WrapperRegistry>[] injItem = null;
	private static BiConsumer<Entity, WrapperRegistry>[] injEntity = null;
	private static final ConcurrentLinkedQueue<MCFluxWrapper> wrq = new ConcurrentLinkedQueue<>();

	@Nonnull public static InjectCollector getCollector() {
		return collect;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		if (collect instanceof InjectCollector.Impl) {
			InjectCollector.Impl cimpl = (InjectCollector.Impl) collect;
			injTile = cimpl.tileInjects.toArray(new BiConsumer[cimpl.tileInjects.size()]);
			injItem = cimpl.itemInjects.toArray(new BiConsumer[cimpl.itemInjects.size()]);
			injEntity = cimpl.entityInjects.toArray(new BiConsumer[cimpl.entityInjects.size()]);
		}
		collect = new InjectCollector.Dummy();
		wth.start();
		L.info("Tile[" + injTile.length + "]; Item[" + injItem.length + "]; Entity[" + injEntity.length + "]");
	}

	private static boolean blacklisted(Class<?> cl) {
		final String cn = cl.getName();
		return cn.startsWith("szewek.") || cn.startsWith("net.minecraft.");
	}

	@SuppressWarnings("unchecked")
	private static <T> void findWrappers(MCFluxWrapper w, BiConsumer<T, WrapperRegistry>[] iwis) {
		final T t = (T) w.mainObject;
		if (t == null)
			return;
		final WrapperRegistry reg = new WrapperRegistry();
		for (BiConsumer<T, WrapperRegistry> iwi : iwis)
			iwi.accept(t, reg);
		reg.resolve(EnergyType.ALL);
		w.addWrappers(reg.resultMap);
	}

	private static void wrap(AttachCapabilitiesEvent<?> att) {
		final Object t = att.getObject();
		if (t == null) {
			MCFluxReport.addErrMsg(new ErrMsg.NullInject((Class<?>) att.getGenericType()));
			return;
		}
		final Class<?> tcl = t.getClass();
		if (blacklisted(tcl))
			return;
		final MCFluxWrapper w = new MCFluxWrapper(t);
		att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
		wrq.offer(w);
	}

	private static void wrapItemStack(AttachCapabilitiesEvent<?> att, ItemStack is) {
		if (MCFluxConfig.WRAP_ITEM_STACKS && !FL.isItemEmpty(is)) {
			final Class<?> ic = is.getItem().getClass();
			if (blacklisted(ic) || ItemBlock.class.isAssignableFrom(ic))
				return;
			final MCFluxWrapper w = new MCFluxWrapper(is);
			att.addCapability(MCFluxWrapper.MCFLUX_WRAPPER, w);
			wrq.offer(w);
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
		if (ent instanceof EntityItem || ent instanceof IProjectile || (ent.world != null && ent.world.getDifficulty() == EnumDifficulty.PEACEFUL && ent instanceof EntityMob))
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
		if (wte.phase == TickEvent.Phase.START && !wrq.isEmpty())
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
					MCFluxWrapper w;
					while((w = wrq.poll()) != null) {
						try {
							if (w.mainObject == null)
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
					}
				} catch (Exception e) {
					MCFluxReport.sendException(e, "WrapperThread loop");
				}
			}
		}
	}
}
