package dev.slmpc.kappaclient.event.impl

import dev.slmpc.kappaclient.event.Event

sealed class GameLoopEvent {

    object Tick: Event()

}