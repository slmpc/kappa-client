package dev.slmpc.kappaclient.util.interfaces

interface Nameable {
    val name: CharSequence

    fun nameAsString() = name.toString()
}