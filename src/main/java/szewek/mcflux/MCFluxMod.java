package szewek.mcflux;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.flavor.CapabilityFlavorEnergy;
import szewek.mcflux.wrapper.InjectWrappers;

@Mod(modid = R.MCFLUX_NAME, version = R.MCFLUX_VERSION)
public class MCFluxMod {
	private Logger log;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		CapabilityEnergy.register();
		CapabilityFlavorEnergy.register();
		log = e.getModLog();
		if (R.MCFLUX_VERSION.charAt(0) == '$')
			log.warn("You are running Minecraft-Flux with an unknown version");
		MinecraftForge.EVENT_BUS.register(new InjectWrappers());
	}
	
	public void init(FMLInitializationEvent e) {
	}
	public void postInit(FMLPostInitializationEvent e) {
	}
}
