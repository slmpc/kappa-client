package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.AbstractSetting
import net.minecraft.client.gui.DrawContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class Component(
    val setting: AbstractSetting<*>,
    val parent: ModuleButton,
    var offset: Float,
    var height: Float
) {

    abstract fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float)

    abstract fun mouseClicked(mouseX: Double, mouseY: Double, button: Int)

    abstract fun mouseReleased(mouseX: Double, mouseY: Double, button: Int)

    abstract fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int)

    fun refreshHeight(): Float {
        height = animate(height, parent.parent.height)
        return height
    }

    protected fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > parent.parent.x && mouseX < (parent.parent.x + parent.parent.width)
                && mouseY > parent.parent.y + parent.offset + offset
                && mouseY < (parent.parent.y + parent.offset + offset + parent.parent.height)
    }

    protected fun animate(current: Float, endPoint: Float): Float {
        return animate(
            current,
            endPoint,
            ClickGUI.animationLength
        )
    }

    private fun animate(current: Float, endPoint: Float, speed: Float): Float {
        val shouldContinueAnimation = endPoint > current

        val dif = max(endPoint, current) - min(endPoint, current)
        val factor = dif * speed
        if (abs(factor) <= 0.001f) return endPoint
        return current + (if (shouldContinueAnimation) factor else -factor)
    }

}