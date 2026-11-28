package com.cbwln.bunnyaddons.modules.skyblock

import com.cbwln.bunnyaddons.modules.Module
import com.google.gson.JsonParser
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.entity.SignText
import net.minecraft.world.level.block.entity.SignTextSlot
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object AutoUndercut : Module("Auto Undercut", "Automatically undercuts the lowest BIN") {
    private val client = HttpClient.newHttpClient()

    @Volatile private var lastId: String? = null
    @Volatile private var lowestBin: Long? = null

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register { mc ->
            if (!enabled) return@register
            val screen = mc.gui.screen() as? AbstractContainerScreen<*> ?: return@register
            if (screen.title.string != "Create BIN Auction") {
                lastId = null
                return@register
            }

            val customData = screen.menu.getSlot(13).item.get(DataComponents.CUSTOM_DATA)
            val id = customData?.copyTag()?.let(::resolveId)
            if (id == null) {
                lastId = null
                return@register
            }
            if (id == lastId) return@register

            lastId = id
            lowestBin = null
            fetchLowestBin(id)
        }
    }

    private fun resolveId(tag: CompoundTag): String? {
        val id = tag.getString("id").orElse(null) ?: return null
        return when (id) {
            "PET" -> resolvePet(tag)
            "RUNE" -> resolveRune(tag)
            else -> id
        }
    }

    private fun resolvePet(tag: CompoundTag): String {
        val json = JsonParser.parseString(tag.getString("petInfo").get()).asJsonObject
        return "PET-${json.get("type").asString}-${json.get("tier").asString}"
    }

    private fun resolveRune(tag: CompoundTag): String {
        val runes = tag.getCompound("runes").get()
        val name = runes.keySet().single()
        return "RUNE-$name-${runes.getInt(name).get()}"
    }

    fun fetchLowestBin(id: String) {
        val request = HttpRequest.newBuilder().uri(URI.create("https://lb.cbwln.com/auction/$id")).build()

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept { response ->
            val price = response.body().toDouble().toLong()
            if (id == lastId) lowestBin = price
        }
    }

    fun fill(sign: SignBlockEntity, slot: SignTextSlot) {
        val lines = sign.getText(slot).getMessages(false).toMutableList()
        if (lines[1].string != "^^^^^^^^^^^^^^^" || lines[2].string != "Your auction" || lines[3].string != "starting bid") return

        val price = lowestBin?.minus(1) ?: return
        lines[0] = Component.literal(price.toString())

        sign.updateText({ text -> SignText(lines, lines, text.color, text.hasGlowingText()) }, slot)
    }
}