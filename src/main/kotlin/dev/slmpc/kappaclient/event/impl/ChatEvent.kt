package dev.slmpc.kappaclient.event.impl

import dev.slmpc.kappaclient.event.CancellableEvent

class ChatEvent(val message: String): CancellableEvent()