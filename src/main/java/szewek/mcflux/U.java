package szewek.mcflux;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredMutable;
import szewek.mcflux.api.fe.IFlavorEnergy;
import szewek.mcflux.util.MCFluxReport;
import szewek.mcflux.util.error.ErrMsgThrownException;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Random;

import static net.minecraft.util.EnumFacing.*;

public enum U {
	;
	public static final EnumFacing[] FANCY_FACING = new EnumFacing[] {DOWN, UP, NORTH, SOUTH, WEST, EAST, null};

	public static String formatMF(IEnergy ie) {
		return ie.getEnergy() + " / " + ie.getEnergyCapacity() + " MF";
	}

	public static String formatMB(int n, int c) {
		return n + " / " + c + " mB";
	}

	public static boolean isItemEmpty(ItemStack is) {
		return is == null || is.isEmpty();
	}

	public static void giveItemToPlayer(final ItemStack is, EntityPlayer p) {
		boolean f = p.inventory.addItemStackToInventory(is);
		EntityItem ei;
		if (f) {
			Random r = p.getRNG();
			p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((r.nextFloat() - r.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			p.inventoryContainer.detectAndSendChanges();
		}
		if (!f || !is.isEmpty()) {
			ei = p.dropItem(is, false);
			if (ei != null) {
				ei.setNoPickupDelay();
				ei.setOwner(p.getName());
			}
		}
	}
	
	public static long transferEnergy(@Nonnull IEnergy from, @Nonnull IEnergy to, final long amount) {
		if (from.canOutputEnergy() && to.canInputEnergy()) {
			long r = to.inputEnergy(from.outputEnergy(amount, true), true);
			if (r > 0)
				return to.inputEnergy(from.outputEnergy(r, false), false);
		}
		return 0;
	}

	public static long transferFlavorEnergy(IFlavorEnergy from, IFlavorEnergy to, Flavored fl, final long amount) {
		if (from.canOutputFlavorEnergy(fl) && to.canInputFlavorEnergy(fl)) {
			FlavoredMutable fli = new FlavoredMutable(fl, amount);
			long r = from.outputFlavorEnergy(fli.toImmutable(), true);
			fli.setAmount(r);
			r = to.inputFlavorEnergy(fli.toImmutable(), true);
			if (r > 0) {
				fli.setAmount(r);
				r = from.outputFlavorEnergy(fli.toImmutable(), false);
				fli.setAmount(r);
				return to.inputFlavorEnergy(fli.toImmutable(), false);
			}
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
		net.minecraft.util.ResourceLocation rl = item.getRegistryName();
		final String dom = rl.getResourceDomain();
		for (int i = 0; i < m; i++) {
			ItemStack is = new ItemStack(item, 1, i);
			ModelResourceLocation mrl = new ModelResourceLocation(dom + ':' + item.getUnlocalizedName(is).substring(5), "inventory");
			net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, mrl);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] makeFilledArray(T[] t, @Nonnull T fill) {
		for (int i = 0; i < t.length; i++)
			t[i] = fill;
		return t;
	}

	public static Class<?> getClassSafely(String name) {
		Class<?> c = null;
		try {
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			MCFluxReport.addErrMsg(new ErrMsgThrownException(e));
		}
		return c;
	}

	public static Method getMethodSafely(Class<?> cl, String name, Class<?>... cargs) {
		Method m = null;
		try {
			m = cl.getDeclaredMethod(name, cargs);
		} catch (Exception e) {
			MCFluxReport.addErrMsg(new ErrMsgThrownException(e));
		}
		return m;
	}
}
