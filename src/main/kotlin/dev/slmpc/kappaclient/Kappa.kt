package dev.slmpc.kappaclient

import dev.slmpc.kappaclient.helper.KernelScope
import dev.slmpc.kappaclient.helper.LoggerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

object Kappa: ClientModInitializer {

    private val MOD_META = FabricLoader.getInstance().getModContainer("kappa-client").orElseThrow().metadata

    @JvmField val NAME = MOD_META.name
    @JvmField val VERSION = MOD_META.version
    @JvmField val LOGGER = LoggerFactory.getLogger("kappa-client")


    override fun onInitializeClient() {

        LoggerHelper.logInfo("$NAME $VERSION")

        KernelScope.launch {
            val asyncLoaderTime = measureTimeMillis {
                withContext(Dispatchers.IO) {
                    KappaAsyncLoader.load()
                }
            }
            LoggerHelper.logInfo("Kappa Loaded Compile!, took ${asyncLoaderTime}ms")
        }


    }

}