package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
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

	@SuppressWarnings("unused")
	public static class EventHandler {
		private EventHandler() {}

		@SubscribeEvent
		public void tileWrappers(AttachCapabilitiesEvent.TileEntity ete) {
			TileEntity te = ete.getTileEntity();
			for (IWrapperInject<TileEntity> iwi : tileInjects)
				iwi.injectWrapper(te, ete::addCapability);
		}

		@SubscribeEvent
		public void itemWrappers(AttachCapabilitiesEvent.Item ei) {
			ItemStack is = ei.getItemStack();
			for (IWrapperInject<ItemStack> iwi : itemInjects)
				iwi.injectWrapper(is, ei::addCapability);
		}

		@SubscribeEvent
		public void entityWrappers(AttachCapabilitiesEvent.Entity ee) {
			Entity ntt = ee.getEntity();
			for (IWrapperInject<Entity> iwi : entityInjects)
				iwi.injectWrapper(ntt, ee::addCapability);
		}

		@SubscribeEvent
		public void worldWrappers(AttachCapabilitiesEvent.World ew) {
			World w = ew.getWorld();
			for (IWrapperInject<World> iwi : worldInjects)
				iwi.injectWrapper(w, ew::addCapability);
		}
	}
}
