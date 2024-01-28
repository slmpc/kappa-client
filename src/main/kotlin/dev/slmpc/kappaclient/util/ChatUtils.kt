package dev.slmpc.kappaclient.util

import dev.slmpc.kappaclient.event.SafeClientEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object ChatUtils {

    fun SafeClientEvent.sendMessage(message: CharSequence) {

        mc.inGameHud.chatHud.addMessage(
            Text.of("${Formatting.AQUA}[Kappa] ${Formatting.WHITE}" + message.toString())
        )
    }

}