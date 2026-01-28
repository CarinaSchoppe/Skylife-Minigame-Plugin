package com.carinaschoppe.skylife.economy

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Player rank enum with coin multipliers.
 * Ranks are determined by player permissions.
 *
 * @property displayName The friendly name of the rank
 * @property tag The tag shown before player names (e.g., "[VIP] ", "[ADMIN] ")
 * @property coinMultiplier Multiplier for coin rewards (1x, 2x, 4x)
 * @property permission Required permission node for this rank
 */
enum class PlayerRank(
    val displayName: String,
    val tag: String,
    val coinMultiplier: Double,
    val permission: String
) {
    /** Default rank - 1x coin multiplier */
    USER("User", "", 1.0, "skylife.rank.user"),

    /** VIP rank - 2x coin multiplier */
    VIP("VIP", "[VIP] ", 2.0, "skylife.rank.vip"),

    /** VIP+ rank - 4x coin multiplier */
    VIP_PLUS("VIP+", "[VIP+] ", 4.0, "skylife.rank.vipplus"),

    /** Moderator rank - 4x coin multiplier, staff rank */
    MOD("Mod", "[MOD] ", 4.0, "skylife.rank.mod"),

    /** Developer rank - 4x coin multiplier, staff rank */
    DEV("Dev", "[DEV] ", 4.0, "skylife.rank.dev"),

    /** Admin rank - 4x coin multiplier, highest staff rank */
    ADMIN("Admin", "[ADMIN] ", 4.0, "skylife.rank.admin");

    /**
     * Gets the display color for this rank.
     *
     * @return The NamedTextColor for this rank
     */
    fun getColor(): NamedTextColor {
        return when (this) {
            USER -> NamedTextColor.GRAY
            VIP -> NamedTextColor.GREEN
            VIP_PLUS -> NamedTextColor.AQUA
            MOD -> NamedTextColor.GREEN
            DEV -> NamedTextColor.AQUA
            ADMIN -> NamedTextColor.RED
        }
    }

    companion object {
        /**
         * Gets the rank for a player based on their permissions.
         * Checks from highest to lowest rank.
         * Staff ranks (ADMIN, DEV, MOD) take priority over VIP ranks.
         * OP players automatically get ADMIN rank.
         *
         * @param player The player to check
         * @return The highest rank the player has permission for (defaults to USER)
         */
        fun getRank(player: org.bukkit.entity.Player): PlayerRank {
            return when {
                // Check for OP or admin permissions first
                player.isOp || player.hasPermission(ADMIN.permission) || player.hasPermission("skylife.*") || player.hasPermission("*") -> ADMIN
                player.hasPermission(DEV.permission) -> DEV
                player.hasPermission(MOD.permission) -> MOD
                // Then check VIP ranks
                player.hasPermission(VIP_PLUS.permission) -> VIP_PLUS
                player.hasPermission(VIP.permission) -> VIP
                else -> USER
            }
        }
    }
}
