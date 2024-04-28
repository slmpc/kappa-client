package dev.slmpc.kappaclient.util.player

import dev.slmpc.kappaclient.event.SafeClientEvent
import dev.slmpc.kappaclient.util.player.PlayerUtils.getEyesPos
import dev.slmpc.kappaclient.util.player.PlayerUtils.getWorldActionId
import dev.slmpc.kappaclient.util.player.RotationUtils.getStrictDirections
import dev.slmpc.kappaclient.util.threads.runSafe
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.Entity
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext

object InteractionUtils {

    private val awaiting: MutableMap<BlockPos, Long> = HashMap()

    fun ClientPlayerInteractionManager.updateController() {
        runSafe {
            runCatching {
                val i = player.inventory.selectedSlot
                if (i != lastSelectedSlot) {
                    lastSelectedSlot = i
                    player.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(lastSelectedSlot))
                }
            }
        }
    }

    fun SafeClientEvent.findBestItem(pos: BlockPos, inventory: Boolean): Item? {
        var slot = -1
        var item: Item? = null
        for (i in 0 until if (inventory) 46 else 9) {
            if (slot == -1 || player.inventory.getStack(i)
                    .getMiningSpeedMultiplier(world.getBlockState(pos)) > player.inventory.getStack(
                    slot
                ).getMiningSpeedMultiplier(
                    world.getBlockState(pos)
                )
            ) {
                slot = i
                item = player.inventory.getStack(i).item
            }
        }
        return item
    }

    fun SafeClientEvent.findBestSlot(pos: BlockPos, inventory: Boolean): Int? {
        var slot = -1
        for (i in 0 until if (inventory) 46 else 9) {
            if (slot == -1 || player.inventory.getStack(i)
                    .getMiningSpeedMultiplier(world.getBlockState(pos)) > player.inventory.getStack(
                    slot
                ).getMiningSpeedMultiplier(
                    world.getBlockState(pos)
                )
            ) {
                slot = i
            }
        }
        if (slot != -1) return slot
        return null
    }

    fun SafeClientEvent.placeBlock(
        bp: BlockPos,
        interact: Interact,
        mode: PlaceMode,
        ignoreEntities: Boolean
    ): Boolean {
        val result = getPlaceResult(bp, interact, ignoreEntities) ?: return false
        val sprint = player.isSprinting
        val sneak = needSneak(world.getBlockState(result.blockPos).block) && !player.isSneaking
        if (sprint) player.networkHandler.sendPacket(
            ClientCommandC2SPacket(
                player,
                ClientCommandC2SPacket.Mode.STOP_SPRINTING
            )
        )
        if (sneak) player.networkHandler.sendPacket(
            ClientCommandC2SPacket(
                player,
                ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
            )
        )
        if (mode == PlaceMode.Normal) mc.interactionManager?.interactBlock(player, Hand.MAIN_HAND, result)
        if (mode == PlaceMode.Packet) connection.sendPacket(
            PlayerInteractBlockC2SPacket(
                Hand.MAIN_HAND,
                result,
                getWorldActionId(world)
            )
        )
        awaiting[bp] = System.currentTimeMillis()
        if (sneak) player.networkHandler.sendPacket(
            ClientCommandC2SPacket(
                player,
                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY
            )
        )
        if (sprint) player.networkHandler.sendPacket(
            ClientCommandC2SPacket(
                player,
                ClientCommandC2SPacket.Mode.START_SPRINTING
            )
        )
        return true
    }

    fun SafeClientEvent.canPlaceBlock(bp: BlockPos, interact: Interact, ignoreEntities: Boolean): Boolean {
        return if (awaiting.containsKey(bp)) false else getPlaceResult(bp, interact, ignoreEntities) != null
    }

    fun SafeClientEvent.getPlaceResult(bp: BlockPos, interact: Interact, ignoreEntities: Boolean): BlockHitResult? {
        if (!ignoreEntities) {
            val cache: ArrayList<Any?> = ArrayList(
                world.getNonSpectatingEntities(
                    Entity::class.java, Box(bp)
                )
            )
            for (entity in cache) if (entity !is ItemEntity && entity !is ExperienceOrbEntity) return null
        }
        if (!world.getBlockState(bp).isReplaceable) return null
        val supports = getSupportBlocks(bp)
        for (support in supports) {
            if (interact != Interact.Vanilla) {
                if (getStrictDirections(bp).isEmpty()) return null
                if (!getStrictDirections(bp).contains(support.facing)) continue
            }
            var result: BlockHitResult? = null
            if (interact != Interact.Legit) {
                val directionVec = Vec3d(
                    support.position.x + 0.5 + support.facing.vector.x * 0.5,
                    support.position.y + 0.5 + support.facing.vector.y * 0.5,
                    support.position.z + 0.5 + support.facing.vector.z * 0.5
                )
                result = BlockHitResult(directionVec, support.facing, support.position, false)
            } else {
                val p = getVisibleDirectionPoint(support.facing, support.position)
                if (p != null) {
                    return BlockHitResult(p, support.facing, support.position, false)
                }
            }
            return result
        }
        return null
    }

    @Suppress("DEPRECATION")
    private fun SafeClientEvent.getSupportBlocks(bp: BlockPos): ArrayList<BlockPosWithFacing> {
        val list = ArrayList<BlockPosWithFacing>()
        if (world.getBlockState(bp.add(0, -1, 0)).isSolid || awaiting.containsKey(bp.add(0, -1, 0))) list.add(
            BlockPosWithFacing(bp.add(0, -1, 0), Direction.UP)
        )
        if (world.getBlockState(bp.add(0, 1, 0)).isSolid || awaiting.containsKey(bp.add(0, 1, 0))) list.add(
            BlockPosWithFacing(bp.add(0, 1, 0), Direction.DOWN)
        )
        if (world.getBlockState(bp.add(-1, 0, 0)).isSolid || awaiting.containsKey(bp.add(-1, 0, 0))) list.add(
            BlockPosWithFacing(bp.add(-1, 0, 0), Direction.EAST)
        )
        if (world.getBlockState(bp.add(1, 0, 0)).isSolid || awaiting.containsKey(bp.add(1, 0, 0))) list.add(
            BlockPosWithFacing(bp.add(1, 0, 0), Direction.WEST)
        )
        if (world.getBlockState(bp.add(0, 0, 1)).isSolid || awaiting.containsKey(bp.add(0, 0, 1))) list.add(
            BlockPosWithFacing(bp.add(0, 0, 1), Direction.NORTH)
        )
        if (world.getBlockState(bp.add(0, 0, -1)).isSolid || awaiting.containsKey(bp.add(0, 0, -1))) list.add(
            BlockPosWithFacing(bp.add(0, 0, -1), Direction.SOUTH)
        )
        return list
    }


    @Suppress("DEPRECATION")
    private fun SafeClientEvent.checkNearBlocks(blockPos: BlockPos): BlockPosWithFacing? {
        if (world.getBlockState(blockPos.add(0, -1, 0)).isSolid) return BlockPosWithFacing(
            blockPos.add(0, -1, 0), Direction.UP
        ) else if (world.getBlockState(blockPos.add(-1, 0, 0)).isSolid) return BlockPosWithFacing(
            blockPos.add(-1, 0, 0), Direction.EAST
        ) else if (world.getBlockState(blockPos.add(1, 0, 0)).isSolid) return BlockPosWithFacing(
            blockPos.add(1, 0, 0), Direction.WEST
        ) else if (world.getBlockState(blockPos.add(0, 0, 1)).isSolid) return BlockPosWithFacing(
            blockPos.add(0, 0, 1), Direction.NORTH
        ) else if (world.getBlockState(blockPos.add(0, 0, -1)).isSolid) return BlockPosWithFacing(
            blockPos.add(0, 0, -1), Direction.SOUTH
        )
        return null
    }

    class BlockPosWithFacing(val position: BlockPos, facing: Direction) {
        val facing: Direction

        init {
            this.facing = facing
        }
    }

    val shiftBlocks: List<Block> = listOf(
        Blocks.ENDER_CHEST,
        Blocks.CHEST,
        Blocks.TRAPPED_CHEST,
        Blocks.CRAFTING_TABLE,
        Blocks.BIRCH_TRAPDOOR,
        Blocks.BAMBOO_TRAPDOOR,
        Blocks.DARK_OAK_TRAPDOOR,
        Blocks.CHERRY_TRAPDOOR,
        Blocks.ANVIL,
        Blocks.BREWING_STAND,
        Blocks.HOPPER,
        Blocks.DROPPER,
        Blocks.DISPENSER,
        Blocks.ACACIA_TRAPDOOR,
        Blocks.ENCHANTING_TABLE,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX
    )

    @Suppress("UNREACHABLE_CODE")
    private fun SafeClientEvent.getVisibleDirectionPoint(dir: Direction, bp: BlockPos): Vec3d? {
        val brutBox: Box = when (dir) {
            Direction.DOWN -> Box(Vec3d(0.1, 0.0, 0.1), Vec3d(0.9, 0.0, 0.9))
            Direction.NORTH -> Box(Vec3d(0.1, 0.1, 0.0), Vec3d(0.9, 0.9, 0.0))
            Direction.EAST -> Box(Vec3d(1.0, 0.1, 0.1), Vec3d(1.0, 0.9, 0.9))
            Direction.SOUTH -> Box(Vec3d(0.1, 0.1, 1.0), Vec3d(0.9, 0.9, 1.0))
            Direction.WEST -> Box(Vec3d(0.0, 0.1, 0.1), Vec3d(0.0, 0.9, 0.9))
            Direction.UP -> Box(Vec3d(0.1, 1.0, 0.1), Vec3d(0.9, 1.0, 0.9))
        }

        if (brutBox.maxX - brutBox.minX == 0.0) {
            var y: Double = brutBox.minY
            while (y < brutBox.maxY) {
                var z: Double = brutBox.minZ
                while (z < brutBox.maxZ) {
                    val point = Vec3d(bp.x + brutBox.minX, bp.y + y, bp.z + z)
                    val wallCheck = world.raycast(
                        RaycastContext(
                            getEyesPos(player),
                            point,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            player
                        )
                    )
                    if (wallCheck != null && wallCheck.type == HitResult.Type.BLOCK && wallCheck.blockPos !== bp) {
                        z += 0.1
                        continue
                    }
                    return point
                    z += 0.1
                }
                y += 0.1
            }
        }
        if (brutBox.maxY - brutBox.minY == 0.0) {
            var x: Double = brutBox.minX
            while (x < brutBox.maxX) {
                var z: Double = brutBox.minZ
                while (z < brutBox.maxZ) {
                    val point = Vec3d(bp.x + x, bp.y + brutBox.minY, bp.z + z)
                    val wallCheck = world.raycast(
                        RaycastContext(
                            getEyesPos(player),
                            point,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            player
                        )
                    )
                    if (wallCheck != null && wallCheck.type == HitResult.Type.BLOCK && wallCheck.blockPos !== bp) {
                        z += 0.1
                        continue
                    }
                    return point
                    z += 0.1
                }
                x += 0.1
            }
        }
        if (brutBox.maxZ - brutBox.minZ == 0.0) {
            var x: Double = brutBox.minX
            while (x < brutBox.maxX) {
                var y: Double = brutBox.minY
                while (y < brutBox.maxY) {
                    val point = Vec3d(bp.x + x, bp.y + y, bp.z + brutBox.minZ)
                    val wallCheck = world.raycast(
                        RaycastContext(
                            getEyesPos(player),
                            point,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            player
                        )
                    )
                    if (wallCheck != null && wallCheck.type == HitResult.Type.BLOCK && wallCheck.blockPos !== bp) {
                        y += 0.1
                        continue
                    }
                    return point
                    y += 0.1
                }
                x += 0.1
            }
        }
        return null
    }

    private fun SafeClientEvent.needSneak(b: Block): Boolean {
        return shiftBlocks.contains(b)
    }


    enum class PlaceMode {
        Packet,
        Normal
    }

    enum class Interact {
        Vanilla,
        Legit
    }

}