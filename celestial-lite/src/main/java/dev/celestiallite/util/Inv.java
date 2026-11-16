package dev.celestiallite.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.function.Predicate;

public class Inv {
    /** first hotbar slot (0-8) matching, or -1 */
    public static int hotbar(ClientPlayerEntity p, Predicate<ItemStack> pred) {
        for (int i = 0; i < 9; i++) if (pred.test(p.inventory.getStack(i))) return i;
        return -1;
    }

    /** first inventory index (0-35, hotbar first) matching, or -1 */
    public static int any(ClientPlayerEntity p, Predicate<ItemStack> pred) {
        for (int i = 0; i < 36; i++) if (pred.test(p.inventory.getStack(i))) return i;
        return -1;
    }

    public static void select(MinecraftClient mc, int slot) {
        if (slot < 0 || slot > 8 || mc.getNetworkHandler() == null) return;
        if (mc.player.inventory.selectedSlot != slot) {
            mc.player.inventory.selectedSlot = slot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private static int container(int invIndex) {
        return invIndex < 9 ? invIndex + 36 : invIndex;
    }

    /** swap inventory index (0-35) with the off hand */
    public static void swapOffhand(MinecraftClient mc, int invIndex) {
        ClientPlayerEntity p = mc.player;
        mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, container(invIndex), 40, SlotActionType.SWAP, p);
    }

    /** swap inventory index (9-35) with hotbar slot (0-8) */
    public static void swapHotbar(MinecraftClient mc, int invIndex, int hotbarSlot) {
        ClientPlayerEntity p = mc.player;
        mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, container(invIndex), hotbarSlot, SlotActionType.SWAP, p);
    }
}
