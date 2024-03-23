package dev.slmpc.kappaclient.gui.clickgui

import dev.slmpc.kappaclient.manager.impl.ModuleManager
import dev.slmpc.kappaclient.module.Category
import dev.slmpc.kappaclient.module.impl.client.ClickGUI
import dev.slmpc.kappaclient.util.graphics.Render2DUtils
import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import dev.slmpc.kappaclient.util.Wrapper.mc
import dev.slmpc.kappaclient.util.font.TextUtils
import net.minecraft.client.gui.DrawContext

class CategoryPanel(
    val category: Category,
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {

    private var dragging = false
    private var extended = true
    private var dragX = 0.0f
    private var dragY = 0.0f

    private val moduleButtons: MutableList<ModuleButton> = mutableListOf()

    init {
        var offset = height

        ModuleManager.modules()
            .filter { it.category == category }
            .forEach {
                moduleButtons.add(ModuleButton(it, this, offset))
                offset += height
            }

        ModuleManager
    }

    fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        Render2DUtils.drawRect(context.matrices, x - 2f, y,
            width + 4f, height, ColorRGB(ClickGUI.red, ClickGUI.green, ClickGUI.blue, 100))

        Render2DUtils.drawRectOutline(context.matrices, x - 2f, y,
            width + 4f, height, ColorRGB(ClickGUI.oRed, ClickGUI.oGreen, ClickGUI.oBlue))

        val offset = (height / 2) - mc.textRenderer.fontHeight / 2

        TextUtils.drawString(context, category.displayString,
            x + (width / 2.0f - mc.textRenderer.getWidth(category.displayString) / 2), y + offset,
            ColorRGB(255, 255, 255), ClickGUI.shadow
        )

        TextUtils.drawString(context, if (extended) "-" else "+",
            x + width - (offset) - mc.textRenderer.getWidth("+"), y + offset,
            ColorRGB(255, 255, 255), ClickGUI.shadow
        )

        if (extended) {
            updateButtons()
            moduleButtons.forEach { it.render(context, mouseX, mouseY, delta) }
        }

    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if (isHovered(mouseX, mouseY)) {
            when (button) {
                0 -> {  // 左键
                    dragging = true
                    dragX = (mouseX - x).toFloat()
                    dragY = (mouseY - y).toFloat()
                }
                1 -> {  // 右键
                    extended = !extended
                }
            }

        }
        if (extended) {
            moduleButtons.forEach { it.mouseClicked(mouseX, mouseY, button) }
        }
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0 && dragging) dragging = false
        moduleButtons.forEach { it.mouseReleased(mouseX, mouseY, button) }
    }

    fun mouseScrolled(amount: Double) {
        y += amount.toInt() * 4
    }

    private fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > x - 2f && mouseX < (x + width + 4f) && mouseY > y && mouseY < (y + height)
    }

    fun updatePosition(mouseX: Int, mouseY: Int) {
        if (dragging) {
            x = mouseX - dragX
            y = mouseY - dragY
        }
    }

    fun updateButtons() {
        var offset = height

        moduleButtons.forEach {
            it.offset = offset
            offset += if (it.extended) it.setOffset else height
        }
    }

    fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) {
        moduleButtons.forEach { it.keyReleased(keyCode, scanCode, modifiers) }
    }

}