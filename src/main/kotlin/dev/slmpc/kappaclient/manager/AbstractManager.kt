package dev.slmpc.kappaclient.manager

import dev.slmpc.kappaclient.IAsyncLoader

abstract class AbstractManager
@JvmOverloads constructor(override val priority: Int = 0) : IAsyncLoader