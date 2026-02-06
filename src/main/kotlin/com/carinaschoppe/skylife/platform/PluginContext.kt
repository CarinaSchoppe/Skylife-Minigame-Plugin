package com.carinaschoppe.skylife.platform

import org.bukkit.plugin.java.JavaPlugin

object PluginContext {
    lateinit var plugin: JavaPlugin
        private set

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
    }
}
