package dev.slmpc.kappaclient.helper

import dev.slmpc.kappaclient.Kappa

object LoggerHelper {

    fun logInfo(info: String) = Kappa.LOGGER.info(info)
    fun logInfo(info: CharSequence) = logInfo(info.toString())

}