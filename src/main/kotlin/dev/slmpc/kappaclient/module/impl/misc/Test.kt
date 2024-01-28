package dev.slmpc.kappaclient.module.impl.misc

import dev.slmpc.kappaclient.event.impl.GameLoopEvent
import dev.slmpc.kappaclient.event.safeEventListener
import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import org.lwjgl.glfw.GLFW

object Test: Module(
    name = "Test",
    description = "Test",
    category = Category.MISC,
    defaultKeyBind = GLFW.GLFW_KEY_Y
) {

    init {
        safeEventListener<GameLoopEvent.Tick> {
            sendMessage("Test")
        }
    }

}