package com.carinaschoppe.skylife.economy

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Player rank enum with coin multipliers and kit slot limits.
 * Ranks are determined by player permissions.
 *
 * @property displayName The friendly name of the rank
 * @property tag The tag shown before player names (e.g., "[VIP] ")
 * @property coinMultiplier Multiplier for coin rewards (1x, 2x, 4x)
 * @property maxKitSlots Maximum number of kits a player can select (2, 3, 4)
 * @property permission Required permission node for this rank
 */
enum class PlayerRank(
    val displayName: String,
    val tag: String,
    val coinMultiplier: Double,
    val maxKitSlots: Int,
    val permission: String
) {
    /** Default rank - 1x coin multiplier, 2 kit slots */
    USER("User", "", 1.0, 2, "skylife.rank.user"),

    /** VIP rank - 2x coin multiplier, 3 kit slots */
    VIP("VIP", "[VIP] ", 2.0, 3, "skylife.rank.vip"),

    /** VIP+ rank - 4x coin multiplier, 4 kit slots */
    VIP_PLUS("VIP+", "[VIP+] ", 4.0, 4, "skylife.rank.vipplus");

    /**
     * Gets the display color for this rank.
     *
     * @return The NamedTextColor for this rank (GRAY for USER, GREEN for VIP, AQUA for VIP+)
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
         * Gets the rank for a player based on their permissions.
         * Checks from highest to lowest rank.
         *
         * @param player The player to check
         * @return The highest rank the player has permission for (defaults to USER)
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
