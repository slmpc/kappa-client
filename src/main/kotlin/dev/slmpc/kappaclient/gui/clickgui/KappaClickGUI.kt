package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.util.graphics.RenderUtils2D
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.threads.runSafe
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

object KappaClickGUI: Screen(Text.of("Kappa ClickGUI")) {

    private val frames: MutableList<CategoryPanel>

    init {
        frames = arrayListOf()

        var offset = 20
        for (category in Category.entries) {
            frames.add(CategoryPanel(category, offset, 20, 120, 14))
            offset += 125
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        runSafe {
            if (ClickGUI.background) {
                RenderUtils2D.drawRect(context.matrices, 0f, 0f,
                    mc.window.scaledWidth.toFloat(), mc.window.scaledHeight.toFloat(),
                    ColorRGB(0, 0, 0, 120))
            }

            frames.forEach {
                it.render(context, mouseX, mouseY, delta)
                it.updatePosition(mouseX, mouseY)
            }
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