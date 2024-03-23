package dev.slmpc.kappaclient.gui.clickgui.impl

import dev.slmpc.kappaclient.gui.clickgui.Component
import dev.slmpc.kappaclient.gui.clickgui.ModuleButton
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.settings.*
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
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
    offset: Float,
    height: Float
): Component(setting, parent, offset, height) {

    private val numSet: NumberSetting<*> = setting as NumberSetting<*>

    private var sliding = false

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!numSet.visibility.invoke()) {
            height = 0.0f
            return
        }

        height = animate(height, parent.parent.height)

        val textOffset = (height / 2) - mc.textRenderer.fontHeight / 2

        var stringValue = ""
        when (numSet) {
            is IntSetting -> {
                stringValue = numSet.value.toString()
            }
            is FloatSetting -> {
                stringValue = roundToPlace(numSet.value.toDouble()).toString()
            }
            is LongSetting -> {
                stringValue = numSet.value.toString()
            }
            is DoubleSetting -> {
                stringValue = roundToPlace(numSet.value).toString()
            }
        }

        val diff = min(parent.parent.width, max(0f, mouseX - parent.parent.x))

        if (sliding) {
            if (diff == 0f) {
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
                        val value = floor(((diff / parent.parent.width) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value.toInt()
                    }
                    is LongSetting -> {
                        val value = floor(((diff / parent.parent.width) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value.toLong()
                    }
                    is FloatSetting -> {
                        val value = floor(((diff / parent.parent.width) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value
                    }
                    is DoubleSetting -> {
                        val value = floor(((diff / parent.parent.width) * (numSet.maxValue - numSet.minValue) + numSet.minValue) / numSet.step) * numSet.step
                        numSet.value = value
                    }
                }
            }
        }


        var renderWidth = (parent.parent.width * (numSet.value.toFloat() - numSet.minValue.toFloat())
                / (numSet.maxValue.toFloat() - numSet.minValue.toFloat()))

        if (numSet.value.toFloat() > numSet.maxValue.toFloat()) renderWidth = parent.parent.width

        Render2DUtils.drawRect(context.matrices,
            parent.parent.x, parent.parent.y + parent.offset + offset,
            parent.parent.width,
            height,
            if (isHovered(mouseX.toDouble(), mouseY.toDouble())) ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 80)
            else ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 60))

        Render2DUtils.drawRectOutline(context.matrices,
            parent.parent.x, parent.parent.y + parent.offset + offset,
            parent.parent.width,
            height,
            ColorRGB(ClickGUI.oRed, ClickGUI.oGreen, ClickGUI.oBlue))

        Render2DUtils.drawRect(
            context.matrices, parent.parent.x, parent.parent.y + parent.offset + offset + height - 2f,
            renderWidth - 1f, 2f, ColorRGB(ClickGUI.nRed, ClickGUI.nGreen, ClickGUI.nBlue)
        )

        TextUtils.drawString(context, "${numSet.name}: $stringValue",
            parent.parent.x + parent.textOffset, parent.parent.y + parent.offset + offset + textOffset,
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

    private fun roundToPlace(value: Double): Double {
        var bd = BigDecimal(value)
        bd = bd.setScale(2, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

}