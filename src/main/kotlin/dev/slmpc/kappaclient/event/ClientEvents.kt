package dev.slmpc.kappaclient.event

import dev.slmpc.kappaclient.event.impl.DisconnectEvent
import dev.slmpc.kappaclient.event.impl.GameLoopEvent
import dev.slmpc.kappaclient.event.impl.JoinWorldEvent
import dev.slmpc.kappaclient.util.Wrapper
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld

abstract class AbstractClientEvent {
    val mc = Wrapper.mc
    abstract val world: ClientWorld?
    abstract val player: ClientPlayerEntity?
    abstract val connection: ClientPlayNetworkHandler?
}

open class ClientEvent: AbstractClientEvent() {
    final override val world: ClientWorld? = mc.world
    final override val player: ClientPlayerEntity? = mc.player
    final override val connection: ClientPlayNetworkHandler? = mc.networkHandler

    inline operator fun <T> invoke(block: ClientEvent.() -> T) = run(block)
}

open class SafeClientEvent internal constructor(
    override val world: ClientWorld,
    override val player: ClientPlayerEntity,
    override val connection: ClientPlayNetworkHandler
): AbstractClientEvent() {

    inline operator fun <T> invoke(block: SafeClientEvent.() -> T) = run(block)

    companion object {

        var instance: SafeClientEvent? = null; private set

        init {
            eventListener<GameLoopEvent.Tick>(alwaysListening = true) {
                update()
            }

            eventListener<DisconnectEvent>(alwaysListening = true) {
                reset()
            }

            eventListener<JoinWorldEvent>(alwaysListening = true) {
                reset()
            }
        }

        private fun update() {
            val world = Wrapper.world ?: return
            val player = Wrapper.player ?: return
            val connection = Wrapper.mc.networkHandler ?: return

            instance = SafeClientEvent(world, player, connection)
        }

        private fun reset() {
            instance = null
        }

    }

}
