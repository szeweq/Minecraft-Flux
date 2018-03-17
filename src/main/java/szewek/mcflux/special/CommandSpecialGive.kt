package szewek.mcflux.special

import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagInt
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemHandlerHelper
import szewek.mcflux.MCFlux

class CommandSpecialGive : CommandBase() {
	override fun getName() = "sgive"

	override fun getUsage(sender: ICommandSender) = "mcflux.cmd.sgive.usage"

	@Throws(CommandException::class)
	override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
		if (args.size < 2)
			throw WrongUsageException("mcflux.cmd.sgive.usage")
		else {
			val player = CommandBase.getEntity(server, sender, args[0], EntityPlayer::class.java)
			val l = CommandBase.parseInt(args[1])
			SpecialEventHandler.getEvent(l) ?: throw CommandException("mcflux.cmd.sgive.noEvent", l)
			val stk = ItemStack(MCFlux.Resources.SPECIAL)
			stk.setTagInfo("seid", NBTTagInt(l))
			ItemHandlerHelper.giveItemToPlayer(player, stk, -1)
		}
	}

	override fun getTabCompletions(server: MinecraftServer, sender: ICommandSender, args: Array<String>, pos: BlockPos?): List<String> {
		return if (args.size == 1) CommandBase.getListOfStringsMatchingLastWord(args, *server.onlinePlayerNames) else emptyList()
	}
}
