package com.carinaschoppe.skylife.game.countdown

import org.bukkit.scheduler.BukkitTask

/**
 * An abstract base class for creating cancellable, runnable countdown timers.
 * Provides the basic structure for starting, stopping, and running a countdown.
 */
abstract class Countdown {

    /** The BukkitTask that represents the running task. Can be null if not running. */
    @Volatile
    protected var task: BukkitTask? = null

    /** Indicates whether the countdown is currently running. */
    @Volatile
    var isRunning = false
        protected set

    /**
     * Starts the countdown.
     * Implementations should handle the creation and scheduling of the BukkitRunnable task.
     */
    abstract fun start()

    /**
     * Stops the countdown immediately.
     * Implementations should cancel the task and reset the state.
     */
    open fun stop() {
        if (!isRunning) return
        task?.cancel()
        task = null
        isRunning = false
    }
}