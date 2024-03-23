package dev.slmpc.kappaclient.gui.clickgui

import com.mojang.blaze3d.systems.RenderSystem
import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.util.ChatUtils.sendMessage
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.threads.runSafe
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object KappaClickGUI: Screen(Text.of("M7thh4ck-ClickGUI")) {

    var func: () -> Unit = {}

    private val frames: MutableList<CategoryPanel> = arrayListOf()

    private val astolfo2 = Identifier("textures/astolfo2.png")
    private val shana = Identifier("textures/037.png")
    private val mahiro = Identifier("textures/mahiro.png")
    private val qianshu = Identifier("textures/039.png")
    private val roxy = Identifier("textures/roxy.png")

    init {

        var offset = 20f
        for (category in Category.entries) {
            frames.add(CategoryPanel(category, offset, 30f, 120f, 16f))
            offset += 125f
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        runSafe {
            if (ClickGUI.background) {
                Render2DUtils.drawRect(context.matrices, 0f, 0f,
                    mc.window.scaledWidth.toFloat(), mc.window.scaledHeight.toFloat(),
                    ColorRGB(0, 0, 0, 160))
            }

            frames.forEach {
                it.render(context, mouseX, mouseY, delta)
                it.updatePosition(mouseX, mouseY)
            }

            func.invoke()
        }

    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        frames.forEach { it.mouseClicked(mouseX, mouseY, button) }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        frames.forEach { it.mouseReleased(mouseX, mouseY, button) }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        frames.forEach { it.mouseScrolled(amount) }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        frames.forEach { it.keyReleased(keyCode, scanCode, modifiers) }
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun close() {
        ClickGUI.disable()
        super.close()
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }

}