package szewek.mcflux.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

public final class EnergyMachineRenderer extends TileEntitySpecialRenderer<TileEntityEnergyMachine> {
	private BlockRendererDispatcher blockRenderer;
	
	@Override
	public void renderTileEntityAt(TileEntityEnergyMachine te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockPos bp = te.getPos();
		IBlockState ibs = te.getCachedState();
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);
		vb.begin(7, DefaultVertexFormats.BLOCK);
		vb.setTranslation(x - bp.getX(), y - bp.getY(), z - bp.getZ());
		blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), blockRenderer.getModelForState(ibs), ibs, bp, vb, false);
		vb.setTranslation(0, 0, 0);
		tes.draw();
	}
}
