package szewek.mcflux.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import javax.annotation.Nonnull;

public final class EnergyMachineRenderer extends FastTESR<TileEntityEnergyMachine> {
	private BlockRendererDispatcher renderBlock;

	@Override
	public void renderTileEntityFast(@Nonnull TileEntityEnergyMachine te, double x, double y, double z, float partialTicks, int destroyStage, float partial, @Nonnull BufferBuilder bb) {
		if (renderBlock == null) renderBlock = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final BlockPos bp = te.getPos();
		final IBlockState ibs = te.getCachedState();
		bb.setTranslation(x - bp.getX(), y - bp.getY(), z - bp.getZ());
		renderBlock.getBlockModelRenderer().renderModel(te.getWorld(), renderBlock.getModelForState(ibs), ibs, bp, bb, false);
	}
}
