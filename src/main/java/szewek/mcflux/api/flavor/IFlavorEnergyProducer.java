package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFlavorEnergyProducer extends INBTSerializable<NBTBase> {
	default long extractFlavorEnergy(EnumFacing from, FlavorEnergy fe, boolean simulate) {
		return extractFlavorEnergy(fe, simulate);
	}

	long extractFlavorEnergy(FlavorEnergy fe, boolean simulate);

	default FlavorEnergy extractAnyFlavorEnergy(EnumFacing from, long amount, boolean simulate) {
		return extractAnyFlavorEnergy(amount, simulate);
	}

	FlavorEnergy extractAnyFlavorEnergy(long amount, boolean simulate);
}
