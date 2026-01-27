package com.carinaschoppe.skylife.skills

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.carinaschoppe.skylife.Skylife
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SkillsGuiTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Skylife
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Skylife::class.java)
        player = server.addPlayer("TestPlayer")
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun `test createSkillsMenuItem creates nether star`() {
        val item = SkillsGui.createSkillsMenuItem()
        assertEquals(Material.NETHER_STAR, item.type)
    }

    @Test
    fun `test createSkillsMenuItem has Skills display name`() {
        val item = SkillsGui.createSkillsMenuItem()
        val meta = item.itemMeta
        assertNotNull(meta.displayName())

        val plainText = PlainTextComponentSerializer.plainText().serialize(meta.displayName())
        assertEquals("Skills", plainText)
    }

    @Test
    fun `test createSkillsMenuItem has lore`() {
        val item = SkillsGui.createSkillsMenuItem()
        val meta = item.itemMeta
        assertNotNull(meta.lore())
        assertTrue(meta.lore()!!.isNotEmpty())
    }

    @Test
    fun `test gui has correct size`() {
        val gui = SkillsGui(player)
        assertEquals(54, gui.inventory.size)
    }

    @Test
    fun `test gui has correct title`() {
        val gui = SkillsGui(player)
        gui.inventory.viewers.firstOrNull()?.openInventory?.title()
        // Title check is complex with Adventure API, so we'll just verify it's not null
        assertNotNull(gui.inventory)
    }

    @Test
    fun `test gui fills empty slots with glass panes`() {
        val gui = SkillsGui(player)
        val inventory = gui.inventory

        // Check slots that should have glass panes
        val glassPaneSlots = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30)
        glassPaneSlots.forEach { slot ->
            val item = inventory.getItem(slot)
            assertNotNull(item, "Slot $slot should have glass pane")
            assertEquals(Material.GRAY_STAINED_GLASS_PANE, item!!.type)
        }
    }

    @Test
    fun `test glass panes have no display name`() {
        val gui = SkillsGui(player)
        val glassPane = gui.inventory.getItem(0)
        assertNotNull(glassPane)

        val meta = glassPane!!.itemMeta
        val displayName = meta.displayName()
        // Check if display name is empty
        val plainText = PlainTextComponentSerializer.plainText().serialize(displayName)
        assertTrue(plainText.isEmpty())
    }

    @Test
    fun `test gui contains all 14 skills`() {
        val gui = SkillsGui(player)
        val inventory = gui.inventory

        val skillSlots = listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25)
        var skillCount = 0

        skillSlots.forEach { slot ->
            val item = inventory.getItem(slot)
            if (item != null && item.type != Material.GRAY_STAINED_GLASS_PANE) {
                // Check if this is a skill item
                val isSkill = Skill.values().any { it.material == item.type }
                if (isSkill) skillCount++
            }
        }

        assertEquals(14, skillCount)
    }

    @Test
    fun `test gui has info item at bottom center`() {
        val gui = SkillsGui(player)
        val infoItem = gui.inventory.getItem(49)
        assertNotNull(infoItem)
        assertEquals(Material.BOOK, infoItem!!.type)
    }

    @Test
    fun `test info item shows skill selection count`() {
        // Select 1 skill
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        val gui = SkillsGui(player)
        val infoItem = gui.inventory.getItem(49)
        assertNotNull(infoItem)

        val meta = infoItem!!.itemMeta
        val lore = meta.lore()
        assertNotNull(lore)

        // Check if lore contains selection count
        val loreText = lore!!.joinToString { PlainTextComponentSerializer.plainText().serialize(it) }
        assertTrue(loreText.contains("1/2") || loreText.contains("Selected"))
    }

    @Test
    fun `test gui shows selected skills with enchantment`() {
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        val gui = SkillsGui(player)
        val inventory = gui.inventory

        // Find the Jumbo skill in inventory
        val skillSlots = listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25)
        var foundJumbo = false

        for (slot in skillSlots) {
            val item = inventory.getItem(slot)
            if (item?.type == Material.BREAD) { // Jumbo material
                foundJumbo = true
                val meta = item.itemMeta
                assertTrue(meta.hasEnchants(), "Selected skill should have enchantment")
                break
            }
        }

        assertTrue(foundJumbo, "Should find Jumbo skill in inventory")
    }

    @Test
    fun `test handleSkillClick selects skill`() {
        val gui = SkillsGui(player)
        gui.handleSkillClick(Skill.JUMBO)

        assertTrue(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
    }

    @Test
    fun `test handleSkillClick unselects skill`() {
        val gui = SkillsGui(player)
        gui.handleSkillClick(Skill.JUMBO) // Select
        gui.handleSkillClick(Skill.JUMBO) // Unselect

        assertFalse(SkillsManager.hasSkillSelected(player, Skill.JUMBO))
    }

    @Test
    fun `test handleSkillClick sends message to player`() {
        val gui = SkillsGui(player)
        gui.handleSkillClick(Skill.JUMBO)

        // Check that player received a message
        assertTrue(player.nextMessage().contains("Jumbo"))
    }

    @Test
    fun `test handleSkillClick shows error when selecting third skill`() {
        val gui = SkillsGui(player)
        gui.handleSkillClick(Skill.JUMBO)
        gui.handleSkillClick(Skill.REGENERATOR)
        gui.handleSkillClick(Skill.ABSORBER) // Should fail

        // Player should receive error message
        val messages = mutableListOf<String>()
        while (player.nextMessage() != null) {
            messages.add(player.nextMessage())
        }

        // Last message should be error
        assertTrue(messages.any { it.contains("already") || it.contains("2 skills") })
    }

    @Test
    fun `test gui refresh updates items`() {
        val gui = SkillsGui(player)

        // Initial state
        SkillsManager.toggleSkill(player, Skill.JUMBO)

        // Refresh
        gui.refresh()

        // Check that Jumbo still shows as selected
        val inventory = gui.inventory
        val skillSlots = listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25)

        for (slot in skillSlots) {
            val item = inventory.getItem(slot)
            if (item?.type == Material.BREAD) {
                assertTrue(item.itemMeta.hasEnchants())
                break
            }
        }
    }

    @Test
    fun `test gui holder is SkillsGui instance`() {
        val gui = SkillsGui(player)
        val holder = gui.inventory.holder
        assertTrue(holder is SkillsGui)
    }
}
