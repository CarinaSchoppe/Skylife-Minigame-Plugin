package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.scheduler.BukkitTask

abstract class Countdown(val game: Game) {

    var duration: Int = defaultDuration

    abstract val defaultDuration: Int

    lateinit var countdown: BukkitTask
    abstract fun start()


    abstract fun stop()
}