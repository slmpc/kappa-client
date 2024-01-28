package dev.slmpc.kappaclient.util.graphics

import com.mojang.blaze3d.systems.RenderSystem
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import org.lwjgl.opengl.GL40C
import kotlin.math.cos
import kotlin.math.sin

object RenderUtils2D {
    fun drawRect(matrices: MatrixStack, x: Float, y: Float, width: Float, height: Float, c: ColorRGB) {
        val matrix = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        setupRender()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x, y + height, 0.0f).color(c.r, c.g, c.b, c.a).next()
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f).color(c.r, c.g, c.b, c.a).next()
        bufferBuilder.vertex(matrix, x + width, y, 0.0f).color(c.r, c.g, c.b, c.a).next()
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(c.r, c.g, c.b, c.a).next()
        Tessellator.getInstance().draw()
        endRender()
    }

    fun drawRectGradient(
        matrices: MatrixStack,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        leftBottomColor: ColorRGB,
        leftTopColor: ColorRGB,
        rightBottomColor: ColorRGB,
        rightTopColor: ColorRGB
    ) {
        val matrix = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        setupRender()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder
            .vertex(matrix, x, y + height, 0.0f)
            .color(rightTopColor.r, rightTopColor.g, rightTopColor.b, rightTopColor.a)
            .next()
        bufferBuilder
            .vertex(matrix, x + width, y + height, 0.0f)
            .color(leftTopColor.r, leftTopColor.g, leftTopColor.b, leftTopColor.a)
            .next()
        bufferBuilder
            .vertex(matrix, x + width, y, 0.0f)
            .color(leftBottomColor.r, leftBottomColor.g, leftBottomColor.b, leftBottomColor.a)
            .next()
        bufferBuilder
            .vertex(matrix, x, y, 0.0f)
            .color(rightBottomColor.r, rightBottomColor.g, rightBottomColor.b, rightBottomColor.a)
            .next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        endRender()
    }

    fun renderRoundedGradientRect(
        matrices: MatrixStack,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        Radius: Float
    ) {
        val matrix = matrices.peek().positionMatrix
        RenderSystem.enableBlend()
        RenderSystem.colorMask(false, false, false, true)
        RenderSystem.clearColor(0.0f, 0.0f, 0.0f, 0.0f)
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false)
        RenderSystem.colorMask(true, true, true, true)
        drawRound(matrices, x, y, width, height, Radius, color1)
        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA)
        setupRender()
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x, y + height, 0.0f)
            .color(color1.r / 255f, color1.g / 255f, color1.b / 255f, color1.a / 255f)
            .next()
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0f)
            .color(color2.r / 255f, color2.g / 255f, color2.b / 255f, color2.a / 255f)
            .next()
        bufferBuilder.vertex(matrix, x + width, y, 0.0f)
            .color(color3.r / 255f, color3.g / 255f, color3.b / 255f, color3.a / 255f)
            .next()
        bufferBuilder.vertex(matrix, x, y, 0.0f)
            .color(color4.r / 255f, color4.g / 255f, color4.b / 255f, color4.a / 255f)
            .next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        endRender()
        RenderSystem.defaultBlendFunc()
    }

    fun drawCircle(matrices: MatrixStack, x: Float, y: Float, radius: Float, color: ColorRGB) =
        drawRound(matrices, x - radius, y - radius, radius * 2, radius * 2, radius, color)

    fun drawRound(
        matrices: MatrixStack,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        radius: Float,
        color: ColorRGB
    ) {
        renderRoundedQuad(
            matrices, color, x, y, width + x, height + y,
            radius.toDouble(), 9.0
        )
    }

    fun renderRoundedQuad(
        matrices: MatrixStack,
        c: ColorRGB,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        radius: Double,
        samples: Double
    ) {
        val matrix = matrices.peek().positionMatrix
        setupRender()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        renderRoundedQuadInternal(
            matrix, c.rFloat, c.gFloat, c.bFloat,
            c.aFloat, fromX, fromY, toX, toY, radius, samples
        )
        endRender()
    }

    fun renderRoundedQuadInternal(
        matrix: Matrix4f,
        cr: Float,
        cg: Float,
        cb: Float,
        ca: Float,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        radius: Double,
        samples: Double
    ) {
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        val map = arrayOf(
            doubleArrayOf(toX - radius, toY - radius, radius),
            doubleArrayOf(toX - radius, fromY + radius, radius),
            doubleArrayOf(fromX + radius, fromY + radius, radius),
            doubleArrayOf(fromX + radius, toY - radius, radius)
        )
        for (i in 0..3) {
            val current = map[i]
            val rad = current[2]
            var r = i * 90.0
            while (r < 360 / 4.0 + i * 90.0) {
                val rad1 = Math.toRadians(r).toFloat()
                val sin = (sin(rad1.toDouble()) * rad).toFloat()
                val cos = (cos(rad1.toDouble()) * rad).toFloat()
                bufferBuilder.vertex(matrix, current[0].toFloat() + sin, current[1].toFloat() + cos, 0.0f)
                    .color(cr, cg, cb, ca).next()
                r += 90 / samples
            }
            val rad1 = Math.toRadians(360 / 4.0 + i * 90.0).toFloat()
            val sin = (sin(rad1.toDouble()) * rad).toFloat()
            val cos = (cos(rad1.toDouble()) * rad).toFloat()
            bufferBuilder.vertex(matrix, current[0].toFloat() + sin, current[1].toFloat() + cos, 0.0f)
                .color(cr, cg, cb, ca).next()
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }

    fun drawCircleOutline(x: Float, y: Float, r: Float, color: ColorRGB) {
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        RenderSystem.lineWidth(100f)
        setupRender()
        for (i in 0..360) {
            val cos = cos(i * Math.PI / 180).toFloat() * r
            val sin = sin(i * Math.PI / 180).toFloat() * r
            bufferBuilder.vertex((x + cos).toDouble(), (y + sin).toDouble(), 0.0)
                .color(color.r, color.g, color.b, color.a).next()
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.lineWidth(1f)
        RenderSystem.disableBlend()
        endRender()
    }

    private fun setupRender() {
        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    }

    private fun endRender() {
        RenderSystem.disableBlend()
    }

}