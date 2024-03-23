package dev.slmpc.kappaclient

import dev.slmpc.kappaclient.event.EventBus
import dev.slmpc.kappaclient.manager.AbstractManager
import dev.slmpc.kappaclient.util.ClassUtils.instance
import dev.slmpc.kappaclient.helper.classes
import java.lang.reflect.Modifier

interface IAsyncLoader {
    val priority: Int
    suspend fun load()
}

object KappaAsyncLoader : IAsyncLoader {
    override val priority: Int = Int.MAX_VALUE
    override suspend fun load() {
        classes.asSequence()
            .filter { Modifier.isFinal(it.modifiers) }
            .filter { it.name.startsWith("dev.slmpc.kappaclient.module.impl") }
            .filter { AbstractManager::class.java.isAssignableFrom(it) }
            .map { it.instance as AbstractManager }
            .sortedByDescending { it.priority }
            .forEach {
                it.load()
                EventBus.subscribe(it)
            }
    }
}
