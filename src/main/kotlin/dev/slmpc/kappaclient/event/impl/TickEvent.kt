package dev.slmpc.kappaclient.event.impl

import dev.slmpc.kappaclient.event.Event

sealed class TickEvent {

    object Post: Event()
    object Pre: Event()

}