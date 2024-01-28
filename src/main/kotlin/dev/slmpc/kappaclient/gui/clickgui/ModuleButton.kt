package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.gui.clickgui.impl.BindBox
import dev.slmpc.kappaclient.gui.clickgui.impl.CheckBox
import dev.slmpc.kappaclient.gui.clickgui.impl.ModeBox
import dev.slmpc.kappaclient.gui.clickgui.impl.Slider
import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.BooleanSetting
import dev.slmpc.kappaclient.settings.EnumSetting
import dev.slmpc.kappaclient.settings.KeyBindSetting
import dev.slmpc.kappaclient.settings.NumberSetting
import dev.slmpc.kappaclient.util.graphics.RenderUtils2D
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import net.minecraft.client.gui.DrawContext

class ModuleButton(val module: Module, val parent: CategoryPanel, var offset: Int) {

    val components: MutableList<Component> = arrayListOf()
    var extended: Boolean = false

    init {
        var setOffset = parent.height
        for (setting in module.settings) {
            when (setting) {
                is NumberSetting -> {
                    components.add(Slider(setting, this, setOffset))
                    setOffset += parent.height
                }
                is BooleanSetting -> {
                    components.add(CheckBox(setting, this, setOffset))
                    setOffset += parent.height
                }
                is EnumSetting<*> -> {
                    components.add(ModeBox(setting, this, setOffset))
                    setOffset += parent.height
                }
                is KeyBindSetting -> {
                    components.add(BindBox(setting, this, setOffset))
                    setOffset += parent.height
                }
            }
        }
    }

    fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        RenderUtils2D.renderRoundedQuad(
            context.matrices,
            if (isHovered(mouseX.toDouble(), mouseY.toDouble())) ColorRGB(100, 100, 100, 255)
                    else ColorRGB(80, 80, 80, 255),
            parent.x.toFloat(), parent.y.toFloat() + offset,
            parent.x + parent.width.toFloat(), parent.y + offset + parent.height.toFloat(),
            2.0, 2.0
        )

        val textOffset = (parent.height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context, module.name.toString(),
            parent.x + textOffset.toFloat(), parent.y + offset + textOffset.toFloat(),
            if (module.isEnabled) ColorRGB(130, 180, 210)
            else ColorRGB(255, 255, 255),
            ClickGUI.shadow
        )

        TextUtils.drawString(context, if (extended) "-" else "+",
            parent.x + parent.width - mc.textRenderer.getWidth("+") - ((parent.height / 2.0f) - mc.textRenderer.fontHeight / 2.0f),
            parent.y + offset + textOffset.toFloat(),
            if (module.isEnabled) ColorRGB(130, 180, 210)
            else ColorRGB(255, 255, 255),
            ClickGUI.shadow
        )

        if (extended) {
            refreshComponentsOffset()
            components.forEach { it.render(context, mouseX, mouseY, delta) }
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle()
            } else {
                extended = !extended
                parent.updateButtons()
            }
        }

        if (extended) {
            components.forEach { it.mouseClicked(mouseX, mouseY, button) }
        }
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (extended) {
            components.forEach { it.mouseReleased(mouseX, mouseY, button) }
        }
    }

    private fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > parent.x && mouseX < (parent.x + parent.width)
                && mouseY > parent.y + offset && mouseY < (parent.y + offset + parent.height)
    }

    private fun refreshComponentsOffset() {
        var setOffset = parent.height
        for (comp in components) {
            if (!comp.setting.visibility.invoke()) continue
            comp.offset = setOffset
            setOffset += parent.height
        }
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        components.forEach { it.keyReleased(keyCode, scanCode, modifiers) }
    }

}