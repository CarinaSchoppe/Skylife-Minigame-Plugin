package com.carinaschoppe.skylife.utility.messages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * A Gson TypeAdapter for serializing and deserializing Adventure's Component objects.
 *
 * This adapter is used to convert between Component objects and their MiniMessage string
 * representation when working with JSON. It's primarily used in [MessageLoader] for
 * saving and loading plugin messages with proper formatting.
 *
 * @property miniMessage The MiniMessage instance used for serialization/deserialization
 */
class ComponentTypeAdapter(private val miniMessage: MiniMessage) : TypeAdapter<Component>() {

    /**
     * Serializes a Component to its MiniMessage string representation.
     *
     * @param out The JsonWriter to write the serialized string to
     * @param value The Component to serialize, or null
     */
    override fun write(out: JsonWriter?, value: Component?) {
        if (value != null) {
            out?.value(miniMessage.serialize(value))
        }
    }

    /**
     * Deserializes a MiniMessage string back into a Component.
     *
     * @param input The JsonReader containing the MiniMessage string
     * @return The deserialized Component, or null if input is null
     */
    override fun read(input: JsonReader?): Component? {
        return miniMessage.deserialize(input?.nextString() ?: return null)
    }
}