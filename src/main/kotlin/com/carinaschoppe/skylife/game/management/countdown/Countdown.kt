package com.carinaschoppe.skylife.game.management.countdown

import com.carinaschoppe.skylife.game.management.Game
import org.bukkit.scheduler.BukkitTask

abstract class Countdown(val game: Game, val defaultDuration: Int) {

    var duration: Int = defaultDuration


    lateinit var countdown: BukkitTask
    abstract fun start()


    abstract fun stop()
}