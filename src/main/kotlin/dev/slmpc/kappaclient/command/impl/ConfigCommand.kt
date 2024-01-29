package dev.slmpc.kappaclient.command.impl

import dev.slmpc.kappaclient.command.AbstractCommand
import dev.slmpc.kappaclient.manager.impl.ConfigManager
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.threads.runSafe

class ConfigCommand: AbstractCommand(arrayOf("config", "save")) {
    override fun run(args: Array<String>) {
        ConfigManager.saveAll()
        runSafe {
            sendMessage("Config saved!")
        }
    }
}