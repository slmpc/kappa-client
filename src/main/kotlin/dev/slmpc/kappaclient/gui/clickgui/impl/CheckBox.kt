package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.AbstractSetting
import dev.slmpc.kappaclient.settings.BooleanSetting
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import net.minecraft.client.gui.DrawContext


class CheckBox(
    setting: AbstractSetting<*>,
    parent: ModuleButton,
    offset: Float,
    height: Float
): Component(setting, parent, offset, height) {

    private val boolSet: BooleanSetting = setting as BooleanSetting

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!boolSet.visibility.invoke()) {
            return
        }

        if (isHovered(mouseX.toDouble(), mouseY.toDouble())) Render2DUtils.drawRect(context.matrices,
            parent.parent.x, parent.parent.y + parent.offset + offset,
            parent.parent.width,
            height,
            ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 80))

        val textOffset = (height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context, boolSet.name.toString(),
            parent.parent.x + parent.textOffset, parent.parent.y + parent.offset + offset + textOffset,
            if (boolSet.value) ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue)
            else ColorRGB(255, 255, 255), ClickGUI.shadow
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (!boolSet.visibility.invoke()) return
        if (isHovered(mouseX, mouseY) && button == 0) {
            boolSet.value = !boolSet.value
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (!boolSet.visibility.invoke()) return
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!boolSet.visibility.invoke()) return
    }

}