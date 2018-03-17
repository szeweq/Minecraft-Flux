package szewek.mcflux.fluxable

import net.minecraft.entity.EntityCreature
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.monster.EntityPigZombie
import net.minecraft.entity.passive.EntityPig
import net.minecraft.init.Items
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import szewek.mcflux.util.EnergyCapable

class EntityActionEnergy(private val creature: EntityCreature) : EnergyCapable() {
	private var charged = false

	override fun getEnergy() = 0L
	override fun getEnergyCapacity() = 1L
	override fun canInputEnergy() = !charged
	override fun canOutputEnergy() = false

	override fun inputEnergy(amount: Long, simulate: Boolean): Long {
		if (!simulate && !charged) {
			if (creature is EntityPig) {
				val pigman = EntityPigZombie(creature.world)
				pigman.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack(Items.GOLDEN_AXE))
				pigman.setLocationAndAngles(creature.posX, creature.posY, creature.posZ, creature.rotationYaw, creature.rotationPitch)
				pigman.setNoAI(creature.isAIDisabled())
				if (creature.hasCustomName()) {
					pigman.customNameTag = creature.getCustomNameTag()
					pigman.alwaysRenderNameTag = creature.getAlwaysRenderNameTag()
				}
				creature.world.spawnEntity(pigman)
				creature.setDead()
			} else if (creature is EntityCreeper)
				creature.onStruckByLightning(EntityLightningBolt(creature.world, creature.posX, creature.posY, creature.posZ, true))
			charged = true
			return 1
		}
		return if (charged) 0L else 1L
	}

	override fun outputEnergy(amount: Long, sim: Boolean) = 0L
}
