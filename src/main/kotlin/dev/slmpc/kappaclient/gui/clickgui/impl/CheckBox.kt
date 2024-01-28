package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.AbstractSetting
import dev.slmpc.kappaclient.settings.BooleanSetting
import dev.slmpc.kappaclient.util.graphics.RenderUtils2D
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import net.minecraft.client.gui.DrawContext


class CheckBox(
    setting: AbstractSetting<*>,
    parent: ModuleButton,
    offset: Int
): Component(setting, parent, offset) {

    private val boolSet: BooleanSetting = setting as BooleanSetting

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!boolSet.visibility.invoke()) return

        RenderUtils2D.renderRoundedQuad(
            context.matrices,
            if (isHovered(mouseX.toDouble(), mouseY.toDouble())) ColorRGB(100, 100, 100, 255)
            else ColorRGB(80, 80, 80, 255),
            parent.parent.x.toFloat(), parent.parent.y.toFloat() + parent.offset + offset,
            parent.parent.x + parent.parent.width.toFloat(),
            parent.parent.y + parent.offset + offset + parent.parent.height.toFloat(),
            2.0, 2.0
        )

        val textOffset = (parent.parent.height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context, boolSet.name.toString(),
            parent.parent.x + textOffset.toFloat(), parent.parent.y + parent.offset + offset + textOffset.toFloat(),
            if (boolSet.value) ColorRGB(130, 180, 210) else ColorRGB(255, 255, 255), ClickGUI.shadow
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