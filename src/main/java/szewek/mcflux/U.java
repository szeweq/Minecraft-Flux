package szewek.mcflux;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.fl.energy.IEnergy;

import static net.minecraft.util.EnumFacing.*;

public final class U {
	public static final EnumFacing[] FANCY_FACING = new EnumFacing[] {DOWN, UP, NORTH, SOUTH, WEST, EAST, null};

	public static String formatMF(IEnergy ie) {
		return ie.getEnergy() + " / " + ie.getEnergyCapacity() + " F";
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels(Item... items) {
			final ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Item i : items) {
			ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
			imm.register(i, 0, mrl);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemMultiModels(Item item, int m) {
		final net.minecraft.util.ResourceLocation rl = item.getRegistryName();
		final String dom = rl.getResourceDomain();
		MCFlux.L.info("MULTI MODELS for " + rl);
		for (int i = 0; i < m; i++) {
			ItemStack is = new ItemStack(item, 1, i);
			ModelResourceLocation mrl = new ModelResourceLocation(dom + ':' + item.getUnlocalizedName(is).substring(5), "inventory");
			net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, mrl);
		}
	}

	private U() {}
}
