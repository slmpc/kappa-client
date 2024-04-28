package dev.slmpc.kappaclient.manager.impl

import dev.slmpc.kappaclient.event.impl.PlayerMotionEvent
import dev.slmpc.kappaclient.manager.AbstractManager

object EventAccessManager: AbstractManager() {
    private var playerMotion: PlayerMotionEvent? = null

    fun getData(): PlayerMotionEvent? {
        if (playerMotion != null) {
            return playerMotion
        }
        return null
    }

    fun setData(e: PlayerMotionEvent) {
        playerMotion = e
    }

    override suspend fun load() {
    }
}