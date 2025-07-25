package com.axalotl.donationmod.listeners;

import com.axalotl.donationmod.events.Values;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UseItemListener implements UseItemCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand) {
        if ((player.getMainHandStack().getItem().equals(Items.ENDER_PEARL)
                || player.getMainHandStack().getItem().equals(Items.WATER_BUCKET)
                || player.getMainHandStack().getItem().equals(Items.LAVA_BUCKET))
                && Values.disableEnderPearls) {
            return ActionResult.FAIL;
        }
        if (Values.noBow && (player.getMainHandStack().getItem().equals(Items.BOW)
                || player.getMainHandStack().getItem().equals(Items.CROSSBOW)
                || player.getMainHandStack().getItem().equals(Items.TRIDENT))) {
            return ActionResult.FAIL;
        }
        if (Values.casinoActive) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
