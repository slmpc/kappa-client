package dev.slmpc.kappaclient.util.inventory

import dev.slmpc.kappaclient.event.SafeClientEvent
import net.minecraft.item.Item

object InventoryUtils {

    fun SafeClientEvent.findItemInHotbar(item: Item): Int {
        for (i in 0..8) {
            if (player.inventory.getStack(i).item == item) return  i
        }
        return -1
    }

    fun SafeClientEvent.findItemInInventory(item: Item): Int {
        for (i in 0..36) {
            if (player.inventory.getStack(i).item == item) return  i
        }
        return -1
    }

}