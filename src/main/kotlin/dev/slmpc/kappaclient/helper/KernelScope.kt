package dev.slmpc.kappaclient.helper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

internal object KernelScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = KernelScheduler + CoroutineName("Kappa-KernelScope")
}

internal object KernelScheduler : CoroutineDispatcher() {
    private val singlePoolExecutor = Executors.newSingleThreadExecutor { Thread(it, "Kappa-Kernel") }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        singlePoolExecutor.submit(block)
    }
}