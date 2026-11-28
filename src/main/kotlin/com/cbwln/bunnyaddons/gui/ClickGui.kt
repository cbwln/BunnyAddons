package com.cbwln.bunnyaddons.gui

import com.cbwln.bunnyaddons.config.ConfigManager
import com.cbwln.bunnyaddons.modules.ModuleManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class ClickGui : Screen(Component.literal("")) {
    private companion object {
        const val COLUMN_WIDTH = 96
        const val ROW_HEIGHT = 16
        const val GAP = 4
        const val PADDING = 4
        const val TOP = 16
        const val LEFT = 16
        const val TOOLTIP_OFFSET = 8

        const val HEADER = 0xFF161616.toInt()
        const val DISABLED = 0xFF262626.toInt()
        const val DISABLED_HOVER = 0xFF333333.toInt()
        const val ENABLED = 0xFF0F62FE.toInt()
        const val ENABLED_HOVER = 0xFF0050E6.toInt()
        const val TOOLTIP = 0xFF393939.toInt()
        const val TEXT = 0xFFF4F4F4.toInt()
        const val TEXT_ON_COLOR = 0xFFFFFFFF.toInt()
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)

        var tooltip: String? = null

        ModuleManager.modulesByCategory.entries.forEachIndexed { i, (category, modules) ->
            val x = columnX(i)

            graphics.fill(x, TOP, x + COLUMN_WIDTH, TOP + ROW_HEIGHT, HEADER)
            val title = category.displayName
            graphics.text(font, title, x + (COLUMN_WIDTH - font.width(title)) / 2, TOP + PADDING, TEXT, false)

            modules.forEachIndexed { j, module ->
                val y = rowY(j)
                val hovered = isOver(x, y, mouseX.toDouble(), mouseY.toDouble())
                val color = when {
                    module.enabled && hovered -> ENABLED_HOVER
                    module.enabled -> ENABLED
                    hovered -> DISABLED_HOVER
                    else -> DISABLED
                }
                val textColor = if (module.enabled) TEXT_ON_COLOR else TEXT

                graphics.fill(x, y, x + COLUMN_WIDTH, y + ROW_HEIGHT, color)
                graphics.text(font, module.name, x + PADDING, y + PADDING, textColor, false)

                if (hovered) tooltip = module.description
            }
        }

        tooltip?.let { text ->
            val w = font.width(text) + PADDING * 2
            val x = minOf(mouseX + TOOLTIP_OFFSET, width - w)
            val y = minOf(mouseY + TOOLTIP_OFFSET, height - ROW_HEIGHT)

            graphics.fill(x, y, x + w, y + ROW_HEIGHT, TOOLTIP)
            graphics.text(font, text, x + PADDING, y + PADDING, TEXT, false)
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() == 1) {
            ModuleManager.modulesByCategory.values.forEachIndexed { i, modules ->
                val x = columnX(i)

                modules.forEachIndexed { j, module ->
                    val y = rowY(j)
                    if (isOver(x, y, event.x(), event.y())) {
                        module.toggle()
                        ConfigManager.save()
                        return true
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick)
    }

    private fun isOver(x: Int, y: Int, mouseX: Double, mouseY: Double): Boolean =
        mouseX >= x && mouseX < x + COLUMN_WIDTH && mouseY >= y && mouseY < y + ROW_HEIGHT

    private fun columnX(index: Int): Int = LEFT + index * (COLUMN_WIDTH + GAP)

    private fun rowY(index: Int): Int = TOP + (index + 1) * ROW_HEIGHT
}