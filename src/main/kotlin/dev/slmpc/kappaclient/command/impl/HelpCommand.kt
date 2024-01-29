package dev.slmpc.kappaclient.command.impl

import dev.slmpc.kappaclient.command.AbstractCommand
import dev.slmpc.kappaclient.manager.impl.CommandManager
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.threads.runSafe
import net.minecraft.util.Formatting

class HelpCommand: AbstractCommand(arrayOf("help")) {
    override fun run(args: Array<String>) {

        runSafe {
            sendMessage("${Formatting.DARK_AQUA}Kappa Client")
            sendMessage("commands:")

            CommandManager.commandMap.forEach { (t, _) ->
                var str = ""
                t.forEach {
                    str += "$it "
                }

                sendMessage("${Formatting.GRAY}$str")
            }
        }

    }
}