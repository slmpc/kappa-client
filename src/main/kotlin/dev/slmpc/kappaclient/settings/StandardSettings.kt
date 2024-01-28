package dev.slmpc.kappaclient.settings

import dev.slmpc.kappaclient.util.graphics.color.ColorRGB
import java.util.concurrent.CopyOnWriteArrayList

class BooleanSetting @JvmOverloads constructor(
    name: CharSequence = "",
    value: Boolean = false,
    description: CharSequence = "",
    visibility: () -> Boolean = { true }
) : AbstractSetting<Boolean>(name, value, description, visibility)

class TextSetting @JvmOverloads constructor(
    name: CharSequence = "",
    value: String = "",
    description: CharSequence = "",
    visibility: () -> Boolean = { true }
) : AbstractSetting<String>(name, value, description, visibility)

class ColorSetting @JvmOverloads constructor(
    name: CharSequence = "",
    value: ColorRGB = ColorRGB.WHITE,
    description: CharSequence = "",
    visibility: () -> Boolean = { true }
) : AbstractSetting<ColorRGB>(name, value, description, visibility)

class EnumSetting<E> @JvmOverloads constructor(
    name: CharSequence = "",
    value: E,
    description: CharSequence = "",
    visibility: () -> Boolean = { true }
) : AbstractSetting<E>(name, value, description, visibility) where E : Enum<E> {
    private var index = 0

    @Throws(NoSuchFieldException::class)
    fun forwardLoop() {
        index = if (index < getModes().size - 1) ++index else 0
        this.value = getModes()[index]
    }

    private fun getModes(): Array<out E> {
        return this.value::class.java.enumConstants
    }

    @Suppress("unchecked_cast")
    @Throws(NoSuchFieldException::class)
    fun setWithName(name: String) {
        value::class
            .java
            .declaredFields
            .find { it.name.equals(name, true) }
            ?.also { it.isAccessible = true }
            ?.also { value(it.get(null) as E) }
    }
}

class KeyBindSetting @JvmOverloads constructor(
    name: CharSequence = "",
    value: KeyBind = KeyBind(KeyBind.Type.KEYBOARD, -1, 1),
    description: CharSequence = "",
    visibility: () -> Boolean = { true }
) : AbstractSetting<KeyBind>(name, value, description, visibility) {
    private val pressConsumer = CopyOnWriteArrayList<() -> Unit>()

    fun onPress(run: () -> Unit) = pressConsumer.add(run)
}