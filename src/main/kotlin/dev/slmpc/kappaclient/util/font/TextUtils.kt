package dev.slmpc.kappaclient.util.font

import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import net.minecraft.client.gui.DrawContext
import java.awt.Color

object TextUtils {

    fun drawString(drawContext: DrawContext, text: String, x: Float, y: Float, color: ColorRGB, shadow: Boolean) {
        drawString(drawContext, text, x, y, color.toColor(), shadow)
    }

    fun drawString(drawContext: DrawContext, text: String, x: Float, y: Float, color: Color, shadow: Boolean) {
        val matrixStack = drawContext.matrices
        matrixStack.push()
        drawContext.drawText(mc.textRenderer, text, x.toInt(), y.toInt(), color.rgb, shadow)
        matrixStack.pop()
    }

    fun drawString(drawContext: DrawContext, text: String, x: Double, y: Double, color: Int, shadow: Boolean) {
        val matrixStack = drawContext.matrices
        matrixStack.push()
        drawContext.drawText(mc.textRenderer, text, x.toInt(), y.toInt(), color, shadow)
        matrixStack.pop()
    }

    fun drawStringWithScale(drawContext: DrawContext, text: String, x: Float, y: Float, color: ColorRGB, shadow: Boolean, scale: Float) {
        drawStringWithScale(drawContext, text, x, y, color.toColor(), shadow, scale)
    }

    fun drawStringWithScale(drawContext: DrawContext, text: String, x: Float, y: Float, color: Color, shadow: Boolean, scale: Float) {
        val matrixStack = drawContext.matrices
        matrixStack.push()
        matrixStack.scale(scale, scale, 1.0f)

        matrixStack.translate((x / scale) - x, (y / scale) - y, 0.0f)


        drawContext.drawText(mc.textRenderer, text, x.toInt(), y.toInt(), color.rgb, shadow)
        matrixStack.pop()
    }

    fun drawStringWithScale(drawContext: DrawContext, text: String, x: Float, y: Float, color: Int, shadow: Boolean, scale: Float) {
        val matrixStack = drawContext.matrices
        matrixStack.push()
        matrixStack.scale(scale, scale, 1.0f)


        matrixStack.translate((x / scale) - x, (y / scale) - y, 0.0f)

        drawContext.drawText(mc.textRenderer, text, x.toInt(), y.toInt(), color, shadow)
        matrixStack.pop()
    }

}