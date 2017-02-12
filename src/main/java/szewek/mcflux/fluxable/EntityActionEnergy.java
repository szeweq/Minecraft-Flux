package szewek.mcflux.fluxable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import szewek.mcflux.api.ex.EnergyCapable;

final class EntityActionEnergy extends EnergyCapable {
	private boolean charged = false;
	private final EntityCreature creature;

	EntityActionEnergy(EntityCreature ec) {
		creature = ec;
	}

	@Override
	public long getEnergy() {
		return 0;
	}

	@Override
	public long getEnergyCapacity() {
		return 1;
	}

	@Override public boolean canInputEnergy() {
		return !charged;
	}

	@Override public boolean canOutputEnergy() {
		return false;
	}

	@Override
	public long inputEnergy(long amount, boolean simulate) {
		if (!simulate && !charged) {
			if (creature instanceof EntityPig) {
				EntityPigZombie pigman = new EntityPigZombie(creature.worldObj);
				pigman.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
				pigman.setLocationAndAngles(creature.posX, creature.posY, creature.posZ, creature.rotationYaw, creature.rotationPitch);
				pigman.setNoAI(creature.isAIDisabled());
				if (creature.hasCustomName()) {
					pigman.setCustomNameTag(creature.getCustomNameTag());
					pigman.setAlwaysRenderNameTag(creature.getAlwaysRenderNameTag());
				}
				creature.worldObj.spawnEntityInWorld(pigman);
				creature.setDead();
			} else if (creature instanceof EntityCreeper)
				creature.onStruckByLightning(null);
			charged = true;
			return 1;
		}
		return charged ? 0 : 1;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return 0;
	}
}
