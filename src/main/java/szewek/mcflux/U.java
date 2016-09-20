package szewek.mcflux;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import java.lang.reflect.Method;

public class U {

	public static String formatMF(long n, long nc) {
		return n + " / " + nc + " MF";
	}

	public static IEnergy getEnergyHolderTile(TileEntity te, EnumFacing f) {
		return te.getCapability(EX.CAP_ENERGY, f);
	}

	public static IEnergy getEnergyHolderEntity(Entity e) {
		return e.getCapability(EX.CAP_ENERGY, null);
	}
	
	public static long transferEnergy(IEnergy from, IEnergy to, final long amount) {
		if (from.canOutputEnergy() && to.canInputEnergy()) {
			long r = to.inputEnergy(from.outputEnergy(amount, true), true);
			if (r > 0)
				return to.inputEnergy(from.outputEnergy(r, false), false);
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels(Item... items) {
			ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Item i : items) {
			ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
			imm.register(i, 0, mrl);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemMultiModels(Item item, int m) {
		ResourceLocation rl = item.getRegistryName();
		final String dom = rl.getResourceDomain();
		for (int i = 0; i < m; i++) {
			ItemStack is = new ItemStack(item, 1, i);
			ModelResourceLocation mrl = new ModelResourceLocation(dom + ':' + item.getUnlocalizedName(is).substring(5), "inventory");
			net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, mrl);
		}
	}

	public static Class<?> getClassSafely(String name) {
		Class<?> c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			L.warn(e);
		}
		return c;
	}
	public static Method getMethodSafely(Class<?> cl, String name, Class<?>... cargs) {
		Method m = null;
		try {
			m = cl.getDeclaredMethod(name, cargs);
		} catch (Exception e) {
			L.warn(e);
		}
		return m;
	}

	private U() {
	}
}
