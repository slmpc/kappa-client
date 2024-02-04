package dev.slmpc.kappaclient.util

import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.function.Predicate
import java.util.jar.JarInputStream

object ClassUtils {
    @Suppress("UNCHECKED_CAST")
    val <T> Class<out T>.instance
        get() = this.getDeclaredField("INSTANCE")[null] as T
}