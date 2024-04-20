package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.AbstractSetting
import dev.slmpc.kappaclient.settings.KeyBindSetting
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import net.minecraft.client.gui.DrawContext
import org.lwjgl.glfw.GLFW

class BindBox(
    setting: AbstractSetting<*>,
    parent: ModuleButton,
    offset: Float,
    height: Float
): Component(setting, parent, offset, height) {

    private val bindSet: KeyBindSetting = setting as KeyBindSetting
    private var binding = false

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!bindSet.visibility.invoke()) {
            return
        }

        if (isHovered(mouseX.toDouble(), mouseY.toDouble())) Render2DUtils.drawRect(context.matrices,
            parent.parent.x, parent.parent.y + parent.offset + offset,
            parent.parent.width,
            height,
            ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 80))

        val textOffset = (height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context,
            if (binding) "${bindSet.name}: ..." else "${bindSet.name}: ${bindSet.value.keyName}",
            parent.parent.x + parent.textOffset, parent.parent.y + parent.offset + offset + textOffset,
            ColorRGB(255, 255, 255), ClickGUI.shadow
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (!bindSet.visibility.invoke()) return
        if (isHovered(mouseX, mouseY) && button == 0) binding = !binding
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (!bindSet.visibility.invoke()) return
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!bindSet.visibility.invoke()) return
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) bindSet.value.keyCode = -1
            else bindSet.value.keyCode = keyCode
            binding = false
        }
    }
}