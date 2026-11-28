package com.cbwln.bunnyaddons.modules

abstract class Module(val name: String, val description: String) {
    val category: Category = resolveCategory()

    var enabled: Boolean = false
        private set

    fun toggle() {
        enabled = !enabled
    }

    private fun resolveCategory(): Category {
        val categoryName = this::class.java.name
            .removePrefix(Module::class.java.`package`.name + ".")
            .substringBefore(".")

        return Category.entries.firstOrNull { it.name.equals(categoryName, ignoreCase = true) }
            ?: error("Module ${this::class.simpleName} must be in a subpackage matching a Category, but got '$categoryName'")
    }
}