package dev.slmpc.kappaclient.module

import dev.slmpc.kappaclient.util.interfaces.DisplayEnum

enum class Category(override val displayName: CharSequence): DisplayEnum {

    COMBAT("Combat"),
    MISC("Misc"),
    RENDER("Render"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    CLIENT("Client")

}