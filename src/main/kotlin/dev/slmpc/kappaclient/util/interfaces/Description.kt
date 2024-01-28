package dev.slmpc.kappaclient.util.interfaces

interface Description {
    val description: CharSequence

    fun descriptionAsString() = description.toString()
}