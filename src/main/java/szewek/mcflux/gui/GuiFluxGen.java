package szewek.mcflux.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import szewek.mcflux.U;
import szewek.mcflux.containers.ContainerFluxGen;
import szewek.mcflux.tileentities.TileEntityFluxGen;
import szewek.mcflux.util.MCFluxLocation;

import java.util.Arrays;

public final class GuiFluxGen extends GuiContainerMCFlux {
	private static final MCFluxLocation tex = new MCFluxLocation("textures/gui/fluxgen.png");
	private static final GuiRect
			workRect = new GuiRect(86, 34,4,18),
			energyRect = new GuiRect(68, 63, 40, 8),
			hotFluidRect = new GuiRect(47, 15, 16, 56),
			cleanFluidRect = new GuiRect(113, 15, 16, 56);
	private final TileEntityFluxGen fluxGen;
	private final String fgName, ipName;
	private int nameL = -1, nameX = -1;
	private IFluidTankProperties[] tanks;
	private FluidStack fsHot, fsClean;

	public GuiFluxGen(EntityPlayer p, TileEntityFluxGen tefg) {
		super(new ContainerFluxGen(p, tefg));
		fluxGen = tefg;
		fgName = fluxGen.getDisplayName().getUnformattedText();
		ipName = p.inventory.getDisplayName().getUnformattedText();
		tanks = fluxGen.getTankProperties();
	}

	@Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(tex);
		drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);
		fsHot = tanks[0].getContents();
		fsClean = tanks[1].getContents();
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiX, guiY, 0);
		drawGuiBar(workRect, fluxGen.getWorkFill(), 0xFFA8A8A8, 0xFFEFEFEF, false, false);
		drawGuiBar(energyRect, fluxGen.getEnergyFill(), 0xFFA82121, 0xFFEF4242, true, true);
		drawFluidStack(hotFluidRect, fsHot, TileEntityFluxGen.fluidCap);
		drawFluidStack(cleanFluidRect, fsClean, TileEntityFluxGen.fluidCap);
		mc.renderEngine.bindTexture(tex);
		drawTexturedModalRect(47, 21, 176, 0, 16, 43);
		drawTexturedModalRect(113, 21, 176, 0, 16, 43);
		GlStateManager.popMatrix();
	}

	@Override protected void drawGuiContainerForegroundLayer(int mx, int my) {
		mx -= guiX;
		my -= guiY;
		if (nameL == -1) {
			nameL = fontRenderer.getStringWidth(fgName);
			nameX = (xSize - nameL) / 2;
		}
		fontRenderer.drawString(fgName, nameX, 4, 0x404040);
		fontRenderer.drawString(ipName, 8, ySize - 92, 0x404040);
		if (energyRect.contains(mx, my)) {
			drawHoveringText(Arrays.asList(U.formatMF(fluxGen), I18n.format("mcflux.gen", fluxGen.getField(3))), mx, my, fontRenderer);
		}
		drawFluidStackInfo(hotFluidRect, fsHot, TileEntityFluxGen.fluidCap, mx, my);
		drawFluidStackInfo(cleanFluidRect, fsClean, TileEntityFluxGen.fluidCap, mx, my);
	}
}
