package dev.slmpc.kappaclient.module.impl.client

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
    val animationLength by setting("AnimationLength", 0.2f, 0.01f..1f, 0.01f)
    val red by setting("GuiRed", 175, 0..255, 1)
    val green by setting("GuiGreen", 175, 0..255, 1)
    val blue by setting("GuiBlue", 255, 0..255, 1)
    val nRed by setting("SliderRed", 120, 0..255, 1)
    val nGreen by setting("SliderGreen", 190, 0..255, 1)
    val nBlue by setting("SliderBlue", 255, 0..255, 1)
    val oRed by setting("OutlineRed", 110, 0..255, 1)
    val oGreen by setting("OutlineGreen", 160, 0..255, 1)
    val oBlue by setting("OutlineBlue", 255, 0..255, 1)
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