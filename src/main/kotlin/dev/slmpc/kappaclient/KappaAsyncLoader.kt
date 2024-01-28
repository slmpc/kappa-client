package dev.slmpc.kappaclient

import dev.slmpc.kappaclient.event.EventBus
import dev.slmpc.kappaclient.helper.reflections
import dev.slmpc.kappaclient.manager.AbstractManager
import dev.slmpc.kappaclient.util.ClassUtils.instance
import dev.slmpc.kappaclient.helper.LoggerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IAsyncLoader {
    val priority: Int
    suspend fun load()
}

object KappaAsyncLoader : IAsyncLoader {
    override val priority: Int = Int.MAX_VALUE
    override suspend fun load(): Unit = withContext(Dispatchers.IO) {
        reflections.getSubTypesOf(AbstractManager::class.java)
            .filter { it != AbstractManager::class.java }
            .also {
                LoggerHelper.logInfo(
                    "Found ${it.size} manager(s),it content: \n${
                        it.joinToString(
                            prefix = "[",
                            postfix = "]"
                        ) { clazz -> clazz.simpleName }
                    }"
                )
            }
            .map { it.instance }
            .sortedBy { it.priority }
            .reversed()
            .forEach {
                it.load()
                EventBus.subscribe(it)
            }
    }
}
