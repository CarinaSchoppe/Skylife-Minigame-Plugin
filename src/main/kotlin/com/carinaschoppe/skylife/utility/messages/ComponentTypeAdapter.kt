package com.carinaschoppe.skylife.utility.messages

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class ComponentTypeAdapter(val miniMessage: MiniMessage) : TypeAdapter<Component>() {
    override fun write(out: JsonWriter?, value: Component?) {
        if (value != null) {
            out?.value(miniMessage.serialize(value))
        }

    }

    override fun read(input: JsonReader?): Component? {
        return miniMessage.deserialize(input?.nextString() ?: return null)
    }


}