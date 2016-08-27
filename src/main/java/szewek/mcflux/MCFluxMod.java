package szewek.mcflux;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.flavor.CapabilityFlavorEnergy;
import szewek.mcflux.fluxable.CapabilityFluxable;
import szewek.mcflux.fluxable.InjectFluxable;
import szewek.mcflux.items.ItemMFTool;
import szewek.mcflux.proxy.ProxyCommon;
import szewek.mcflux.wrapper.InjectWrappers;
import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@Mod(modid = R.MCFLUX_NAME, version = R.MCFLUX_VERSION)
public class MCFluxMod {
	private static Logger log;
	private static ItemMFTool MFTOOL;
	private static final CreativeTabs MCFLUX_TAB = new CreativeTabs(R.MCFLUX_NAME) {
		@Override
		public Item getTabIconItem() {
			return MFTOOL;
		}
	};
	@SidedProxy(modId = R.MCFLUX_NAME, serverSide = R.PROXY_SERVER, clientSide = R.PROXY_CLIENT)
	public static ProxyCommon PROXY = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		log = e.getModLog();
		if (R.MCFLUX_VERSION.charAt(0) == '@')
			log.warn("You are running Minecraft-Flux with an unknown version (development maybe?)");
		else
			log.info("Minecraft-Flux version " + R.MCFLUX_VERSION);
		CapabilityEnergy.register();
		CapabilityFlavorEnergy.register();
		CapabilityFluxable.register();
		EVENT_BUS.register(InjectWrappers.INSTANCE);
		EVENT_BUS.register(InjectFluxable.INSTANCE);

		MFTOOL = registerItem("mftool", new ItemMFTool());
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		GameRegistry.addShapedRecipe(new ItemStack(MFTOOL), new String[]{"n n","rir","rrr"}, 'n', Items.GOLD_NUGGET, 'r', Items.REDSTONE, 'i', Items.IRON_INGOT);
		FMLInterModComms.sendMessage("Waila", "register", "szewek.mcflux.compat.waila.MCFluxWailaProvider.callbackRegister");
		PROXY.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}

	@SideOnly(Side.CLIENT)
	public static void renders() {
		registerItemModels(MFTOOL);
	}

	private static <T extends Item> T registerItem(String name, T i) {
		i.setUnlocalizedName(name).setCreativeTab(MCFLUX_TAB);
		return GameRegistry.register(i, new ResourceLocation(R.MCFLUX_NAME, name));
	}

	private static void registerItemModels(Item... items) {
		ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Item i : items) {
			ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
			imm.register(i, 0, mrl);
		}
	}
}
