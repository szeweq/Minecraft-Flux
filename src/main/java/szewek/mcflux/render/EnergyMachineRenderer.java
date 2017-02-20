package szewek.mcflux.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import javax.annotation.Nonnull;

public final class EnergyMachineRenderer extends FastTESR<TileEntityEnergyMachine> {
	private BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(@Nonnull TileEntityEnergyMachine te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer vb) {
		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockPos bp = te.getPos();
		IBlockState ibs = te.getCachedState();
		vb.setTranslation(x - bp.getX(), y - bp.getY(), z - bp.getZ());
		blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), blockRenderer.getModelForState(ibs), ibs, bp, vb, false);

	}
}
