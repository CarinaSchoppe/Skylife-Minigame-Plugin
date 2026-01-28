package com.carinaschoppe.skylife.economy

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Player rank enum with multipliers and kit slots.
 */
enum class PlayerRank(
    val displayName: String,
    val tag: String,
    val coinMultiplier: Double,
    val maxKitSlots: Int,
    val permission: String
) {
    USER("User", "", 1.0, 2, "skylife.rank.user"),
    VIP("VIP", "[VIP] ", 2.0, 3, "skylife.rank.vip"),
    VIP_PLUS("VIP+", "[VIP+] ", 4.0, 4, "skylife.rank.vipplus");

    /**
     * Gets the color for this rank.
     */
    fun getColor(): NamedTextColor {
        return when (this) {
            USER -> NamedTextColor.GRAY
            VIP -> NamedTextColor.GREEN
            VIP_PLUS -> NamedTextColor.AQUA
        }
    }

    companion object {
        /**
         * Gets the rank for a player based on permissions.
         */
        fun getRank(player: org.bukkit.entity.Player): PlayerRank {
            return when {
                player.hasPermission(VIP_PLUS.permission) -> VIP_PLUS
                player.hasPermission(VIP.permission) -> VIP
                else -> USER
            }
        }
    }
}
