package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.gui.clickgui.impl.BindBox
import dev.slmpc.kappaclient.gui.clickgui.impl.CheckBox
import dev.slmpc.kappaclient.gui.clickgui.impl.ModeBox
import dev.slmpc.kappaclient.gui.clickgui.impl.Slider
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.BooleanSetting
import dev.slmpc.kappaclient.settings.EnumSetting
import dev.slmpc.kappaclient.settings.KeyBindSetting
import dev.slmpc.kappaclient.settings.NumberSetting
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import net.minecraft.client.gui.DrawContext

class ModuleButton(val module: Module, val parent: CategoryPanel, var offset: Float) {

    val components: MutableList<Component> = arrayListOf()
    var extended: Boolean = false
    var textOffset = (parent.height / 2) - mc.textRenderer.fontHeight / 2
    var setOffset = parent.height

    init {
        var setOffset = parent.height
        for (setting in module.settings) {
            when (setting) {
                is NumberSetting -> {
                    components.add(Slider(setting, this, setOffset, parent.height))
                    setOffset += parent.height
                }
                is BooleanSetting -> {
                    components.add(CheckBox(setting, this, setOffset, parent.height))
                    setOffset += parent.height
                }
                is EnumSetting<*> -> {
                    components.add(ModeBox(setting, this, setOffset, parent.height))
                    setOffset += parent.height
                }
                is KeyBindSetting -> {
                    components.add(BindBox(setting, this, setOffset, parent.height))
                    setOffset += parent.height
                }
            }
        }
    }

    fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        Render2DUtils.drawRect(context.matrices,
            parent.x, parent.y + offset,
            parent.width, parent.height,
            if (isHovered(mouseX.toDouble(), mouseY.toDouble())) ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 60)
            else ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 80))

        Render2DUtils.drawRectOutline(context.matrices,
            parent.x, parent.y + offset,
            parent.width, parent.height,
            ColorRGB(ClickGUI.oRed, ClickGUI.oGreen, ClickGUI.oBlue)
        )

        TextUtils.drawString(context, module.name.toString(),
            parent.x + textOffset, parent.y + offset + textOffset,
            if (module.isEnabled) ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue)
            else ColorRGB(255, 255, 255),
            ClickGUI.shadow
        )

        textOffset = (parent.height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context, if (extended) "-" else "+",
            parent.x + parent.width - mc.textRenderer.getWidth("+") - ((parent.height / 2.0f) - mc.textRenderer.fontHeight / 2.0f),
            parent.y + offset + textOffset,
            if (module.isEnabled) ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue)
            else ColorRGB(255, 255, 255),
            ClickGUI.shadow
        )

        if (extended) {
            refreshComponentsOffset()
            components.forEach { it.render(context, mouseX, mouseY, delta) }
        }

        if (isHovered(mouseX.toDouble(), mouseY.toDouble())) {
            KappaClickGUI.func = {
                Render2DUtils.drawRect(
                    context.matrices, 0f, mc.window.scaledHeight - (mc.textRenderer.fontHeight + 2.0f),
                    mc.textRenderer.getWidth(module.description.toString()) + 4.0f, mc.textRenderer.fontHeight + 2.0f,
                    ColorRGB(50, 50, 50, 255)
                )

                TextUtils.drawString(
                    context, module.description.toString(),
                    2.0f, mc.window.scaledHeight - (mc.textRenderer.fontHeight + 2.0f) + 1.0f,
                    ColorRGB(255, 255, 255), ClickGUI.shadow
                )
            }
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle()
            } else {
                extended = !extended
                updateAnimation()
                parent.updateButtons()
            }
        }

        if (extended) {
            components.forEach { it.mouseClicked(mouseX, mouseY, button) }
        }
    }

    private fun updateAnimation() {
        components.forEach {
            it.height = 0f
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
        setOffset = parent.height
        for (comp in components) {
            if (!comp.setting.visibility.invoke()) continue
            comp.offset = setOffset
            setOffset += comp.height
        }
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        components.forEach { it.keyReleased(keyCode, scanCode, modifiers) }
    }

}