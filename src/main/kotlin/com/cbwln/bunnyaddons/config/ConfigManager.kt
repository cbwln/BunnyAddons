package com.cbwln.bunnyaddons.config

import com.cbwln.bunnyaddons.BunnyAddons
import com.cbwln.bunnyaddons.modules.ModuleManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object ConfigManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configPath: Path = FabricLoader.getInstance().configDir.resolve("bunnyaddons.json")

    fun save() {
        try {
            Files.createDirectories(configPath.parent)
            val root = JsonObject()
            for (module in ModuleManager.modules) {
                root.addProperty(module.name, module.enabled)
            }
            Files.writeString(configPath, gson.toJson(root))
        } catch (e: IOException) {
            BunnyAddons.LOGGER.error("Failed to write config", e)
        }
    }

    fun load() {
        if (!Files.exists(configPath)) return
        try {
            val root = JsonParser.parseString(Files.readString(configPath)).asJsonObject
            for (module in ModuleManager.modules) {
                val enabled = root.get(module.name)?.asBoolean ?: continue
                if (enabled) module.toggle()
            }
        } catch (e: IOException) {
            BunnyAddons.LOGGER.error("Failed to read config", e)
        }
    }
}