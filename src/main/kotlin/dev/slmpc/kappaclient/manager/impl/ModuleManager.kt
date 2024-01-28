package dev.slmpc.kappaclient.manager.impl

import dev.slmpc.kappaclient.event.impl.KeyEvent
import dev.slmpc.kappaclient.event.safeEventListener
import dev.slmpc.kappaclient.helper.LoggerHelper
import dev.slmpc.kappaclient.helper.reflections
import dev.slmpc.kappaclient.manager.AbstractManager
import dev.slmpc.kappaclient.util.ClassUtils.instance
import dev.slmpc.kappaclient.module.Module
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.measureTimeMillis

object ModuleManager: AbstractManager() {

    init {
        safeEventListener<KeyEvent> { event ->
            moduleImpls.forEach {
                if (it.keyBind == event.key) {
                    it.toggle()
                }
            }
        }
    }

    private val moduleImpls = CopyOnWriteArrayList<Module>()

    override suspend fun load() {
        val time = measureTimeMillis {
            reflections.getSubTypesOf(Module::class.java)
                .filter { it != Module::class.java }
                .map { it.instance }
                .forEach {
                    moduleImpls.add(it)
                }

            moduleImpls.sortBy { it.name.toString() }
        }

        LoggerHelper.logInfo("Loaded ${moduleImpls.size} module(s), it took time: ${time}ms")
    }

    fun modules(): List<Module> = moduleImpls.toList()

}