package szewek.mcflux.wrapper;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum InjectWrappers {
	INSTANCE;
	
	private Set<IWrapperInject<TileEntity>> tileInjects = new HashSet<>();
	private Set<IWrapperInject<ItemStack>> itemInjects = new HashSet<>();
	private final EventHandler evth = new EventHandler();
	
	public void registerTileWrapperInject(IWrapperInject<TileEntity> iwi) {
		tileInjects.add(iwi);
	}
	public void registerItemWrapperInject(IWrapperInject<ItemStack> iwi) {
		itemInjects.add(iwi);
	}
	
	public EventHandler getEventHandler() {
		return evth;
	}
	
	public class EventHandler {
		private EventHandler() {}
		
		@SubscribeEvent
		public void injectWrappers(AttachCapabilitiesEvent e) {
			if (e instanceof AttachCapabilitiesEvent.TileEntity) {
				AttachCapabilitiesEvent.TileEntity ete = (AttachCapabilitiesEvent.TileEntity) e;
				TileEntity te = ete.getTileEntity();
				for (IWrapperInject<TileEntity> iwi : tileInjects)
					iwi.injectWrapper(te, ete::addCapability);
			} else if (e instanceof AttachCapabilitiesEvent.Item) {
				AttachCapabilitiesEvent.Item ei = (AttachCapabilitiesEvent.Item) e;
				ItemStack is = ei.getItemStack();
				for (IWrapperInject<ItemStack> iwi : itemInjects)
					iwi.injectWrapper(is, ei::addCapability);
			}
		}
	}
}
