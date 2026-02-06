package com.carinaschoppe.skylife.utility.miscellaneous

import org.bukkit.entity.Player

/**
 * Represents the join priority of a player.
 * Higher priority players can kick lower priority players when joining a full game.
 */
enum class PlayerPriority(val level: Int) {
    NORMAL(0),
    VIP(1),
    VIP_PLUS(2),
    STAFF(3); // Admin, Dev, Mod are all equal at staff level

    companion object {
        /**
         * Gets the priority of a player based on their permissions.
         */
        fun getPlayerPriority(player: Player): PlayerPriority {
            return when {
                player.hasPermission("skylife.rank.admin") ||
                        player.hasPermission("skylife.rank.dev") ||
                        player.hasPermission("skylife.rank.mod") -> STAFF

                player.hasPermission("skylife.rank.vipplus") -> VIP_PLUS
                player.hasPermission("skylife.rank.vip") -> VIP
                else -> NORMAL
            }
        }

        /**
         * Checks if player1 has higher priority than player2.
         */
        fun hasHigherPriority(player1: Player, player2: Player): Boolean {
            val priority1 = getPlayerPriority(player1)
            val priority2 = getPlayerPriority(player2)
            return priority1.level > priority2.level
        }

        /**
         * Finds the lowest priority player in a list.
         * Returns null if the list is empty.
         */
        fun findLowestPriorityPlayer(players: List<Player>): Player? {
            if (players.isEmpty()) return null

            return players.minByOrNull { player ->
                getPlayerPriority(player).level
            }
        }

        /**
         * Gets all players with a specific priority level.
         */
        fun getPlayersWithPriority(players: List<Player>, priority: PlayerPriority): List<Player> {
            return players.filter { getPlayerPriority(it) == priority }
        }

        /**
         * Finds a player to kick based on priority rules.
         * Returns the lowest priority player, or null if no suitable player found.
         */
        fun findPlayerToKick(currentPlayers: List<Player>, joiningPlayer: Player): Player? {
            val joiningPriority = getPlayerPriority(joiningPlayer)

            // Find all players with lower priority than the joining player
            val kickablePlayers = currentPlayers.filter {
                getPlayerPriority(it).level < joiningPriority.level
            }

            if (kickablePlayers.isEmpty()) {
                return null
            }

            // Return the player with the lowest priority
            return findLowestPriorityPlayer(kickablePlayers)
        }
    }
}
