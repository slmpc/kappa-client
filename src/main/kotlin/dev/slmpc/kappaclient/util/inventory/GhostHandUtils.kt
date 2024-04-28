package dev.slmpc.kappaclient.util.inventory

import dev.slmpc.kappaclient.event.SafeClientEvent
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.screen.slot.SlotActionType

object GhostHandUtils {

    fun SafeClientEvent.pickHotbarSpoof(slot: Int, func: () -> Unit) {
        if (slot == player.inventory.selectedSlot || slot !in 0..8) return

        connection.sendPacket(UpdateSelectedSlotC2SPacket(slot))
        func.invoke()
        connection.sendPacket(UpdateSelectedSlotC2SPacket(slot))
    }

    fun SafeClientEvent.swapInventorySpoof(slot: Int, func: () -> Unit) {
        clickSlotSwap(player.inventory.selectedSlot, slot)
        func.invoke()
        clickSlotSwap(player.inventory.selectedSlot, slot)
    }

    fun SafeClientEvent.pickFromInventorySpoof(slot: Int, func: () -> Unit) {
        if (!hotbarIsFull()) return
        if (slot in 0..8) return

        playerController.pickFromInventory(slot)
        func.invoke()
        playerController.pickFromInventory(slot)
    }

    private fun SafeClientEvent.clickSlotSwap(from: Int, to: Int) {
        val handler = player.currentScreenHandler
        val stack = Int2ObjectArrayMap<ItemStack>()
        stack.put(to, handler.getSlot(to).stack)
        connection.sendPacket(
            ClickSlotC2SPacket(
                handler.syncId,
                handler.revision,
                PlayerInventory.MAIN_SIZE + from,
                to,
                SlotActionType.SWAP,
                handler.cursorStack.copy(),
                stack
            )
        )
    }

    private fun SafeClientEvent.hotbarIsFull(): Boolean {
        for (i in 0..8) {
            if (player.inventory.getStack(i).isEmpty) return false
        }
        return true
    }

}