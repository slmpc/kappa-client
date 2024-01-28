package dev.slmpc.kappaclient.event.impl

import dev.slmpc.kappaclient.event.Event
import dev.slmpc.kappaclient.settings.KeyBind

class KeyEvent(val key: KeyBind): Event()