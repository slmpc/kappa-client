package dev.slmpc.kappaclient.util.player

import dev.slmpc.kappaclient.event.SafeClientEvent
import dev.slmpc.kappaclient.util.player.PlayerUtils.getEyesPos
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.*
import net.minecraft.util.math.Vec3d
import java.lang.Math.*
import kotlin.math.atan2
import kotlin.math.sqrt

object RotationUtils {

    fun SafeClientEvent.getStrictDirections(bp: BlockPos): List<Direction> {
        val visibleSides: MutableList<Direction> = ArrayList()
        val positionVector = bp.toCenterPos()
        val westDelta = getEyesPos(player).x - positionVector.add(0.5, 0.0, 0.0).x
        val eastDelta = getEyesPos(player).x - positionVector.add(-0.5, 0.0, 0.0).x
        val northDelta = getEyesPos(player).z - positionVector.add(0.0, 0.0, 0.5).z
        val southDelta = getEyesPos(player).z - positionVector.add(0.0, 0.0, -0.5).z
        val upDelta = getEyesPos(player).y - positionVector.add(0.0, 0.5, 0.0).y
        val downDelta = getEyesPos(player).y - positionVector.add(0.0, -0.5, 0.0).y
        if (westDelta > 0 && !world.getBlockState(bp.west()).isReplaceable) visibleSides.add(Direction.EAST)
        if (westDelta < 0 && !world.getBlockState(bp.east()).isReplaceable) visibleSides.add(Direction.WEST)
        if (eastDelta < 0 && !world.getBlockState(bp.east()).isReplaceable) visibleSides.add(Direction.WEST)
        if (eastDelta > 0 && !world.getBlockState(bp.west()).isReplaceable) visibleSides.add(Direction.EAST)
        if (northDelta > 0 && !world.getBlockState(bp.north()).isReplaceable) visibleSides.add(Direction.SOUTH)
        if (northDelta < 0 && !world.getBlockState(bp.south()).isReplaceable) visibleSides.add(Direction.NORTH)
        if (southDelta < 0 && !world.getBlockState(bp.south()).isReplaceable) visibleSides.add(Direction.NORTH)
        if (southDelta > 0 && !world.getBlockState(bp.north()).isReplaceable) visibleSides.add(Direction.SOUTH)
        if (upDelta > 0 && !world.getBlockState(bp.down()).isReplaceable) visibleSides.add(Direction.UP)
        if (upDelta < 0 && !world.getBlockState(bp.up()).isReplaceable) visibleSides.add(Direction.DOWN)
        if (downDelta < 0 && !world.getBlockState(bp.up()).isReplaceable) visibleSides.add(Direction.DOWN)
        if (downDelta > 0 && !world.getBlockState(bp.down()).isReplaceable) visibleSides.add(Direction.UP)
        return visibleSides
    }

    fun SafeClientEvent.calculateAngle(to: Vec3d): FloatArray {
        return calculateAngle(getEyesPos(player), to)
    }

    fun SafeClientEvent.calculateAngle(from: Vec3d, to: Vec3d): FloatArray {
        val difX = to.x - from.x
        val difY = (to.y - from.y) * -1.0
        val difZ = to.z - from.z
        val dist = sqrt((difX * difX + difZ * difZ).toFloat()).toDouble()
        val yD = wrapDegrees(toDegrees(atan2(difZ, difX)) - 90.0).toFloat()
        val pD = clamp(wrapDegrees(toDegrees(atan2(difY, dist))), -90.0, 90.0).toFloat()
        return floatArrayOf(yD, pD)
    }
    
}