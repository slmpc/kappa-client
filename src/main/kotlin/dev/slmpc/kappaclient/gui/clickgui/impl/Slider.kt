package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.*
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import dev.slmpc.kappaclient.util.graphics.RenderUtils2D
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import net.minecraft.client.gui.DrawContext
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Slider(
    setting: AbstractSetting<*>,
    parent: ModuleButton,
    offset: Int
): Component(setting, parent, offset) {

    private val numSet: NumberSetting<*> = setting as NumberSetting<*>

    private var sliding = false

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!numSet.visibility.invoke()) return

        val textOffset = (parent.parent.height / 2) - mc.textRenderer.fontHeight / 2

        var stringValue = ""
        when (numSet) {
            is IntSetting -> {
                stringValue = numSet.value.toString()
            }
            is FloatSetting -> {
                stringValue = roundToPlace(numSet.value.toDouble(), 2).toString()
            }
        }

        val diff = min(parent.parent.width, max(0, mouseX - parent.parent.x))

        if (sliding) {
            if (diff == 0) {
                when (numSet) {
                    is IntSetting -> {
                        numSet.value = numSet.minValue
                    }
                    is FloatSetting -> {
                        numSet.value = numSet.minValue
                    }
                    is DoubleSetting -> {
                        numSet.value = numSet.minValue
                    }
                    is LongSetting -> {
                        numSet.value = numSet.minValue
                    }
                }
            } else {
                when (numSet) {
                    is IntSetting -> {
                        val value = floor(((diff.toFloat() / parent.parent.width.toFloat()) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value.toInt()
                    }
                    is LongSetting -> {
                        val value = floor(((diff.toFloat() / parent.parent.width.toFloat()) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value.toLong()
                    }
                    is FloatSetting -> {
                        val value = floor(((diff.toFloat() / parent.parent.width.toFloat()) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value
                    }
                    is DoubleSetting -> {
                        val value = floor(((diff.toFloat() / parent.parent.width.toFloat()) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value
                    }
                }
            }
        }


        var renderWidth = (parent.parent.width * (numSet.value.toFloat() - numSet.minValue.toFloat())
                / (numSet.maxValue.toFloat() - numSet.minValue.toFloat()))

        if (numSet.value.toFloat() > numSet.maxValue.toFloat()) renderWidth = parent.parent.width.toFloat()

        RenderUtils2D.renderRoundedQuad(
            context.matrices,
            if (isHovered(mouseX.toDouble(), mouseY.toDouble())) ColorRGB(100, 100, 100, 255)
            else ColorRGB(80, 80, 80, 255),
            parent.parent.x.toFloat(), parent.parent.y.toFloat() + parent.offset + offset,
            parent.parent.x + parent.parent.width.toFloat(),
            parent.parent.y + parent.offset + offset + parent.parent.height.toFloat(),
            2.0, 2.0
        )

        RenderUtils2D.drawRect(
            context.matrices, parent.parent.x.toFloat() + 1, parent.parent.y.toFloat() + parent.offset + offset + 2,
            renderWidth - 2, parent.parent.height.toFloat() - 4, ColorRGB(120, 120, 160, 255)
        )

        TextUtils.drawString(context, "${numSet.name}: $stringValue",
            parent.parent.x + textOffset.toFloat(), parent.parent.y + parent.offset + offset + textOffset.toFloat(),
            ColorRGB(255, 255, 255), ClickGUI.shadow
        )

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (!numSet.visibility.invoke()) return
        if (isHovered(mouseX, mouseY)) sliding = true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (!numSet.visibility.invoke()) return
        sliding = false
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (!numSet.visibility.invoke()) return
    }

    private fun roundToPlace(value: Double, place: Int): Double {
        if (place < 0) return value

        var bd = BigDecimal(value)
        bd = bd.setScale(place, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

}