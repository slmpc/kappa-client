package dev.slmpc.kappaclient.manager.impl

import dev.slmpc.kappaclient.event.SafeClientEvent
import dev.slmpc.kappaclient.event.impl.PacketEvent
import dev.slmpc.kappaclient.event.impl.PlayerMotionEvent
import dev.slmpc.kappaclient.event.safeEventListener
import dev.slmpc.kappaclient.manager.AbstractManager
import dev.slmpc.kappaclient.util.extension.toVec3d
import dev.slmpc.kappaclient.util.player.RotationUtils.calculateAngle
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.concurrent.CopyOnWriteArrayList

object RotationManager: AbstractManager() {

    private val rotateList = CopyOnWriteArrayList<RotateInfo>()
    private var spoofList = CopyOnWriteArrayList<RotateInfo>()
    private var doRotation = true

    override suspend fun load() {
        rotateList.clear()
    }

    init {

        safeEventListener<PlayerMotionEvent>(Int.MAX_VALUE, true) { event ->
            val first = rotateList.firstOrNull() ?: return@safeEventListener
            if (!doRotation) return@safeEventListener
            rotateList.sortedByDescending { it.priority }.forEach {
                event.setRotation(first.yaw, first.pitch)
                spoofList.add(RotateInfo(first.yaw, first.pitch, first.priority))
                rotateList.remove(it)
            }
        }

        safeEventListener<PacketEvent.Send>(Int.MAX_VALUE) { event ->
            if (!doRotation) return@safeEventListener
            spoofList.sortByDescending { it.priority }
            spoofList.forEach {
                when (event.packet) {
                    is PlayerMoveC2SPacket -> {
                        val packet = it
                        event.packet.yaw = packet.yaw
                        event.packet.pitch = packet.pitch
                        spoofList.remove(packet)
                        rotateList.remove(packet)
                    }
                }
            }
        }

    }

    fun SafeClientEvent.addRotation(yaw: Float, pitch: Float, priority: Int = 10) {
        rotateList.add(RotateInfo(yaw, pitch, priority))
    }

    fun SafeClientEvent.addRotation(vec3d: Vec3d, priority: Int = 10) {
        val angle = calculateAngle(vec3d)
        rotateList.add(RotateInfo(angle[0], angle[1], priority))
    }

    fun SafeClientEvent.addRotation(pos: BlockPos, priority: Int = 10) {
        val angle = calculateAngle(pos.toVec3d())
        rotateList.add(RotateInfo(angle[0], angle[1], priority))
    }

    fun startRotation() {
        doRotation = true
    }

    fun stopRotation() {
        doRotation = false
    }


    fun clearList() {
        rotateList.clear()
    }

    data class RotateInfo(val yaw: Float, val pitch: Float, val priority: Int = 10)
}