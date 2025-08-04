package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import com.axalotl.donationmod.events.Values;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CasinoEvent extends Event {
    public CasinoEvent(String name, int duration, float price) {
        super(name, duration, price);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof CasinoEvent);
            return;
        }
        HashMap<BlockPos, BlockState> map = new HashMap<>();
        final BlockPos firstBlock = MinecraftClient.getInstance().player.getBlockPos().add(1, 2, 2);
        for (int i = 0; i < 3; i++) {
            if (MinecraftClient.getInstance().world != null) {
                map.put(firstBlock.add(-i, 0, 0), MinecraftClient.getInstance().world.getBlockState(firstBlock.add(-i, 0, 0)));
            }
        }
//        map.forEach(((blockPos, blockState) -> MinecraftClient.getInstance().world.setBlockState(blockPos, Blocks.GOLD_BLOCK.getDefaultState())));
//        if (MinecraftClient.getInstance().world != null) {
//            MinecraftClient.getInstance().world.setBlockState(firstBlock, Blocks.DIAMOND_BLOCK.getDefaultState());
//        }
        Values.casinoActive = true;
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.casino"));
        Timer casino_timer = new Timer();
        casino_timer.schedule(new TimerTask() {
            final List<BlockState> states = new ArrayList<>(Arrays.asList(Blocks.IRON_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.NETHERITE_BLOCK.getDefaultState()));
            int count = 5;
            int[] slots;

            public void run() {
                if (count <= 0) {
                    map.forEach((blockPos, blockState) -> MinecraftClient.getInstance().world.setBlockState(blockPos, blockState));
                    if (slots[0] == 4 && slots[1] == 4 && slots[2] == 4) {
                        Timer win = new Timer();
                        win.schedule(new TimerTask() {
                            int win_count = 3;

                            @Override
                            public void run() {
                                if (win_count <= 0) {
                                    MinecraftClient.getInstance().inGameHud.setTitle(Text.of("§a" + donationAlertsEvent.UserName));
                                    win.cancel();
                                } else {
                                    MinecraftClient.getInstance().inGameHud.setTitle(Text.of("§a" + donationAlertsEvent.UserName));
                                    win_count--;
                                }
                            }
                        }, 0, 1000);
                        MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, 1F, 1F);
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§7[§bКазино§7] " + "§rПобедитель:§a " + donationAlertsEvent.UserName), false);
                    }
                    casino_timer.cancel();
                } else {
                    if (MinecraftClient.getInstance().getCameraEntity() != null) {
                        double x = MinecraftClient.getInstance().player.getX();
                        double y = MinecraftClient.getInstance().player.getY();
                        double z = MinecraftClient.getInstance().player.getZ();
                        MinecraftClient.getInstance().getCameraEntity().updatePosition(Math.floor(x) + 0.5, Math.floor(y) + 0.5, Math.floor(z) + 0.5);
                    }
                    slots = new int[3];
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    for (int i = 0; i < slots.length; i++) {
                        slots[i] = random.nextInt(4) + 1;
                    }
                    BlockState firstSlot = states.get(slots[0] - 1);
                    BlockState secondSlot = states.get(slots[1] - 1);
                    BlockState thirdSlot = states.get(slots[2] - 1);

                    MinecraftClient.getInstance().world.setBlockState(firstBlock, firstSlot);
                    MinecraftClient.getInstance().world.setBlockState(firstBlock.add(-1, 0, 0), secondSlot);
                    MinecraftClient.getInstance().world.setBlockState(firstBlock.add(-2, 0, 0), thirdSlot);

                    MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 1F, 1F);
                    count--;
                }
            }
        }, 0, 1000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Values.casinoActive = false;
                DonationEvent.activeEvents.removeIf(event -> event instanceof CasinoEvent);
                timer.cancel();
            }
        }, getDuration() * 1000L);
    }
}
