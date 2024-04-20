package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.AbstractSetting
import dev.slmpc.kappaclient.settings.EnumSetting
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.interfaces.DisplayEnum
import net.minecraft.client.gui.DrawContext

class ModeBox(
    setting: AbstractSetting<*>,
    parent: ModuleButton,
    offset: Float,
    height: Float
): Component(setting, parent, offset, height) {

    private val modeSet: EnumSetting<*> = setting as EnumSetting<*>

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!modeSet.visibility.invoke()) {
            return
        }

        if (isHovered(mouseX.toDouble(), mouseY.toDouble())) Render2DUtils.drawRect(context.matrices,
            parent.parent.x, parent.parent.y + parent.offset + offset,
            parent.parent.width,
            height,
            ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 80))

        val textOffset = (height / 2) - mc.textRenderer.fontHeight / 2

        val modeValueStr = if (modeSet.value is DisplayEnum) {
            (modeSet.value as DisplayEnum).displayString
        } else {
            modeSet.value.name
        }

        TextUtils.drawString(context, "${modeSet.name}: $modeValueStr",
            parent.parent.x + parent.textOffset, parent.parent.y + parent.offset + offset + textOffset,
            ColorRGB(255, 255, 255), ClickGUI.shadow
        )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (!modeSet.visibility.invoke()) return
        if (isHovered(mouseX, mouseY) && button == 0) {
            modeSet.forwardLoop()
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (!modeSet.visibility.invoke()) return
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!modeSet.visibility.invoke()) return
    }

}