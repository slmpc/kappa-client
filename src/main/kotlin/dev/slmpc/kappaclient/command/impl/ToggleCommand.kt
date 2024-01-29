package dev.slmpc.kappaclient.command.impl

import dev.slmpc.kappaclient.command.AbstractCommand
import dev.slmpc.kappaclient.manager.impl.ModuleManager
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.threads.runSafe

class ToggleCommand: AbstractCommand(arrayOf("t", "toggle")) {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            runSafe {
                sendMessage("toggle [module name]")
            }
            return
        }

        if (args.size == 1) {
            ModuleManager.modules().forEach {
                if (it.nameAsString() == args[0]) {
                    it.toggle()
                    return@forEach
                }
            }
        }
    }
}