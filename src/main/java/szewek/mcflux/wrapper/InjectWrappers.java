package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InjectWrappers {
	private static Set<IWrapperInject<TileEntity>> tileInjects = new HashSet<>();
	private static Set<IWrapperInject<ItemStack>> itemInjects = new HashSet<>();
	private static Set<IWrapperInject<Entity>> entityInjects = new HashSet<>();
	private static Set<IWrapperInject<World>> worldInjects = new HashSet<>();
	public static final EventHandler events = new EventHandler();

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

		private Registry(AttachCapabilitiesEvent ace) {
			capMap = ace.getCapabilities();
			event = ace;
		}

		public void add(ResourceLocation rl, ICapabilityProvider icp) {
			event.addCapability(rl, icp);
		}
	}

	private static <T> void wrap(T t, Registry reg, Iterable<IWrapperInject<T>> iwis) {
		for (IWrapperInject<T> iwi : iwis)
			iwi.injectWrapper(t, reg);
	}

	@SuppressWarnings("unused")
	public static class EventHandler {
		private EventHandler() {}

		@SubscribeEvent(priority = EventPriority.LOW)
		public void tileWrappers(AttachCapabilitiesEvent.TileEntity ete) {
			wrap(ete.getTileEntity(), new Registry(ete), tileInjects);
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public void itemWrappers(AttachCapabilitiesEvent.Item ei) {
			wrap(ei.getItemStack(), new Registry(ei), itemInjects);
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public void entityWrappers(AttachCapabilitiesEvent.Entity ee) {
			wrap(ee.getEntity(), new Registry(ee), entityInjects);
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public void worldWrappers(AttachCapabilitiesEvent.World ew) {
			wrap(ew.getWorld(), new Registry(ew), worldInjects);
		}
	}
}
