package dev.slmpc.kappaclient.module.impl.client

import dev.slmpc.kappaclient.event.impl.GameLoopEvent
import dev.slmpc.kappaclient.event.safeEventListener
import dev.slmpc.kappaclient.gui.clickgui.KappaClickGUI
import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.util.threads.runSafe
import org.lwjgl.glfw.GLFW

object ClickGUI: Module(
    name = "ClickGUI",
    category = Category.CLIENT,
    description = "",
    defaultKeyBind = GLFW.GLFW_KEY_RIGHT_SHIFT
) {

    val background by setting("Background", false)
    val shadow by setting("Shadow", true)

    init {

        onEnable {
            runSafe {
                mc.setScreen(KappaClickGUI)
            }
        }

        onDisable {
            runSafe {
                mc.setScreen(null)
            }
        }

    }

}