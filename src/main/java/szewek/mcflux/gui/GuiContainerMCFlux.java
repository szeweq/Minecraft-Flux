package szewek.mcflux.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import szewek.mcflux.U;

import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class GuiContainerMCFlux extends GuiContainer {
	private static final int BAR_BORDER = 0xFF323232, BAR_BG = 0xFF151515;
	protected int guiX, guiY;

	GuiContainerMCFlux(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		guiX = (width - xSize) / 2;
		guiY = (height - ySize) / 2;
	}

	protected void drawHGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float ya = (float) (startColor >> 24 & 255) / 255.0F;
		float yr = (float) (startColor >> 16 & 255) / 255.0F;
		float yg = (float) (startColor >> 8 & 255) / 255.0F;
		float yb = (float) (startColor & 255) / 255.0F;
		float za = (float) (endColor >> 24 & 255) / 255.0F;
		float zr = (float) (endColor >> 16 & 255) / 255.0F;
		float zg = (float) (endColor >> 8 & 255) / 255.0F;
		float zb = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vb.pos((double) right, (double) top, zLevel).color(yr, yg, yb, ya).endVertex();
		vb.pos((double) left, (double) top, zLevel).color(zr, zg, zb, za).endVertex();
		vb.pos((double) left, (double) bottom, zLevel).color(zr, zg, zb, za).endVertex();
		vb.pos((double) right, (double) bottom, zLevel).color(yr, yg, yb, ya).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	protected void drawGuiBar(GuiRect rect, float fill, int c1, int c2, boolean horiz, boolean reverse) {
		int x = rect.x, y = rect.y, x2 = rect.x2, y2 = rect.y2, f;
		drawRect(x, y, x2, y2, BAR_BORDER);
		x++;
		y++;
		x2--;
		y2--;
		if (fill == 0) {
			drawRect(x, y, x2, y2, BAR_BG);
			return;
		}
		if (horiz) {
			drawHGradientRect(x, y, x2, y2, c1, c2);
			f = MathHelper.ceil((rect.width - 2) * fill);
			if (reverse) drawRect(x + f, y, x2, y2, BAR_BG);
			else drawRect(x, y, x2 - f, y2, BAR_BG);
		} else {
			drawGradientRect(x, y, x2, y2, c1, c2);
			f = MathHelper.ceil((rect.height - 2) * fill);
			if (reverse) drawRect(x, y + f, x2, y2, BAR_BG);
			else drawRect(x, y, x2, y2 - f, BAR_BG);
		}
	}

	protected void drawFluidStackInfo(GuiRect rect, @Nullable FluidStack fs, int cap, int mx, int my) {
		if (fs != null && rect.contains(mx, my))
			drawHoveringText(Arrays.asList(fs.getLocalizedName(), U.formatMB(fs.amount, cap)), mx, my, fontRenderer);
	}

	protected void drawFluidStack(GuiRect rect, @Nullable FluidStack fs, int cap) {
		if (fs == null)
			return;
		Fluid fl = fs.getFluid();
		if (fl == null)
			return;
		TextureAtlasSprite tas = getFluidSprite(fl);
		int flc = fl.getColor(fs);
		int sa = (fs.amount * rect.height) / cap;
		if (sa < 1 && fs.amount > 0)
			sa = 1;
		if (sa > rect.height)
			sa = rect.height;
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		setGLColor(flc);
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		final int xc = rect.width / 16, xr = rect.width % 16, yc = sa / 16, yr = sa % 16, ys = rect.y2;
		for (int xt = 0; xt <= xc; xt++) {
			for (int yt = 0; yt <= yc; yt++) {
				int w = xt == xc ? xr : 16;
				int h = yt == yc ? yr : 16;
				int x = rect.x + xt * 16;
				int y = ys - (yt + 1) * 16;
				if (w > 0 && h > 0) {
					int mt = 16 - h;
					int mr = 16 - w;
					double umin = tas.getMinU();
					double umax = tas.getMaxU();
					double vmin = tas.getMinV();
					double vmax = tas.getMaxV();
					umax -= mr / 16.0 * (umax - umin);
					vmax -= mt / 16.0 * (vmax - vmin);
					vb.begin(7, DefaultVertexFormats.POSITION_TEX);
					vb.pos(x, y + 16, zLevel).tex(umin, vmax).endVertex();
					vb.pos(x + 16 - mr, y + 16, zLevel).tex(umax, vmax).endVertex();
					vb.pos(x + 16 - mr, y + mt, zLevel).tex(umax, vmin).endVertex();
					vb.pos(x, y + mt, zLevel).tex(umin, vmin).endVertex();
					tes.draw();
				}
			}
		}
	}

	private TextureAtlasSprite getFluidSprite(Fluid fl) {
		TextureMap tm = mc.getTextureMapBlocks();
		ResourceLocation rlFluid = fl.getStill();
		TextureAtlasSprite tas = null;
		if (rlFluid != null)
			tas = tm.getTextureExtry(rlFluid.toString());
		if (tas == null)
			tas = tm.getMissingSprite();
		return tas;
	}

	private static void setGLColor(int color) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;

		GlStateManager.color(r, g, b, 1.0F);
	}
}
