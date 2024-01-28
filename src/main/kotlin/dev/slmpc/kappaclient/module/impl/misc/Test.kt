package dev.slmpc.kappaclient.module.impl.misc

import dev.slmpc.kappaclient.event.impl.GameLoopEvent
import dev.slmpc.kappaclient.event.safeEventListener
import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.interfaces.DisplayEnum
import org.lwjgl.glfw.GLFW

object Test: Module(
    name = "Test",
    description = "Test",
    category = Category.MISC,
    defaultKeyBind = GLFW.GLFW_KEY_Y
) {

    private val enum by setting("Enum", TestEnum.KAPPA)
    private val slider by setting("Slider", 10f, 0f..20f, 0.5f)

    init {
        safeEventListener<GameLoopEvent.Tick> {
//            sendMessage("Test")
        }
    }

    private enum class TestEnum(override val displayName: CharSequence): DisplayEnum {
        KAPPA("Kappa"),
        MELON("Melon")
    }

}