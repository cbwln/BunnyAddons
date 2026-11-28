package com.cbwln.bunnyaddons.modules

import com.cbwln.bunnyaddons.modules.misc.AutoSprint
import com.cbwln.bunnyaddons.modules.misc.Fullbright
import com.cbwln.bunnyaddons.modules.skyblock.AutoUndercut

object ModuleManager {
    val modules: List<Module> = listOf(
        AutoSprint, Fullbright, AutoUndercut
    )

    val modulesByCategory: Map<Category, List<Module>> = modules.groupBy { it.category }.toSortedMap()
}