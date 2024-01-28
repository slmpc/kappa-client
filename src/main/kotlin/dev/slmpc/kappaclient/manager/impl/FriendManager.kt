package dev.slmpc.kappaclient.manager.impl

import dev.slmpc.kappaclient.manager.AbstractManager
import net.minecraft.entity.player.PlayerEntity
import java.util.concurrent.CopyOnWriteArrayList

object FriendManager: AbstractManager() {
    override suspend fun load() {
    }

    val friends = CopyOnWriteArrayList<String>()

    fun isFriend(name: String): Boolean = name in friends

    fun isFriend(player: PlayerEntity): Boolean = isFriend(player.name.string)

}