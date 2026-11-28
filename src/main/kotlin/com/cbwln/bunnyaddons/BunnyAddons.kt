package com.cbwln.bunnyaddons

import com.cbwln.bunnyaddons.config.ConfigManager
import com.cbwln.bunnyaddons.gui.ClickGui
import com.cbwln.bunnyaddons.modules.skyblock.AutoUndercut
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.minecraft.client.Minecraft
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object BunnyAddons : ClientModInitializer {
	const val MOD_ID: String = "bunnyaddons"

	val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitializeClient() {
		ConfigManager.load()
		AutoUndercut.init()

		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			listOf("bunnyaddons", "ba").forEach { command ->
				dispatcher.register(ClientCommands.literal(command).executes {
					val mc = Minecraft.getInstance()
					mc.schedule { mc.gui.setScreen(ClickGui()) }
					1
				})
			}
		}
	}
}
