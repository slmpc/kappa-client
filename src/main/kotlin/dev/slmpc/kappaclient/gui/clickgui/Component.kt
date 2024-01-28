package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.settings.AbstractSetting
import net.minecraft.client.gui.DrawContext

abstract class Component(
    val setting: AbstractSetting<*>,
    val parent: ModuleButton,
    var offset: Int
) {

    abstract fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)

    abstract fun mouseClicked(mouseX: Double, mouseY: Double, button: Int)

    abstract fun mouseReleased(mouseX: Double, mouseY: Double, button: Int)

    abstract fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int)

    protected fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > parent.parent.x && mouseX < (parent.parent.x + parent.parent.width)
                && mouseY > parent.parent.y + parent.offset + offset
                && mouseY < (parent.parent.y + parent.offset + offset + parent.parent.height)
    }

}