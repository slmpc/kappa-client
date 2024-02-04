package dev.slmpc.kappaclient.command.impl

import dev.slmpc.kappaclient.command.AbstractCommand
import dev.slmpc.kappaclient.manager.impl.CommandManager
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.threads.runSafe
import net.minecraft.util.Formatting

class PrefixCommand: AbstractCommand(arrayOf("prefix")) {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            runSafe {
                sendMessage("prefix [prefix]")
            }
            return
        }

        if (args.size == 1) {
            CommandManager.setPrefix(args[0][0])
            runSafe {
                sendMessage("Command prefix set to ${Formatting.GOLD}${args[0][0]}")
            }
        }
    }
}