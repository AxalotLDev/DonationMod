package com.axalotl.donationmod.events;

import com.axalotl.donationmod.DonationMod;
import com.axalotl.donationmod.config.ModConfig;
import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.events.list.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.*;

public class DonationEvent {
    public static List<Event> activeEvents = new ArrayList<>();
    private static final ModConfig config = DonationMod.getConfig();
    public static final Event[] bigDonations = {
            new StopMoveEvent(I18n.translate("effect.donation_mod.stop_move"), config.getStopMoveDuration()),
            new NoJumpEvent(I18n.translate("effect.donation_mod.no_jump"), config.getNoJumpDuration()),
            new NoMob(I18n.translate("effect.donation_mod.no_mob"), config.getNoMobDuration()),
            new RotateScreen(I18n.translate("effect.donation_mod.rotate_screen"), config.getRotateScreenDuration()),
            new InvertedControl(I18n.translate("effect.donation_mod.inverted_control"), config.getInvertedControlDuration()),
            new CancelDamage(I18n.translate("effect.donation_mod.cancel_damage"), config.getCancelDamageDuration()),
            new NoBowEvent(I18n.translate("effect.donation_mod.no_bow"), config.getNoBowDuration()),
            new SlowdownEvent(I18n.translate("effect.donation_mod.slowdown"), config.getSlowdownDuration()),
            new ThirdPersonEvent(I18n.translate("effect.donation_mod.third_person"), config.getThirdPersonDuration()),
            new CinematicCameraEvent(I18n.translate("effect.donation_mod.cinematic_camera"), config.getCinematicCameraDuration()),
            new ScarryEvent(I18n.translate("effect.donation_mod.screamer"), 1)
    };

    public static final Event[] veryBigDonationEvents = {
            new KickEvent(I18n.translate("effect.donation_mod.kick"), 5),
            new DisableElytraEvent(I18n.translate("effect.donation_mod.disable_elytra"), config.getDisableElytraDuration()),
            new NoFriendsEvent(I18n.translate("effect.donation_mod.no_friends"), config.getNoFriendsDuration())
    };
    public static final Event[] smallDonationEvents = {
            new EffectEvent(I18n.translate("effect.donation_mod.effect"), config.getFirstEffectDuration()),
            new DropItemEvent(I18n.translate("effect.donation_mod.drop_item"), 0)
    };
    public static final Event[] testEvents = {
            new CasinoEvent(I18n.translate("effect.donation_mod.casino"), 5)
    };

    public static Event getEventByName(Event[] events, String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return event;
            }
        }
        return null;
    }

    public static void launchRandomEvent(DonationAlertsEvent donationAlertsEvent) {
        int amount = (int) donationAlertsEvent.AmountMain;
        int VeryBigDonations = amount / config.getVeryBigDonationAmount();
        int remains = amount % config.getVeryBigDonationAmount();
        int BigDonations = remains / config.getBigDonationAmount();
        remains = remains % config.getBigDonationAmount();
        int SmallDonations = 0;
        if (remains > 0 && (remains / config.getFirstEffectDonationAmount() != 0
                || remains / config.getSecondEffectDonationAmount() != 0
                || remains / config.getThirdEffectDonationAmount() != 0)) {
            SmallDonations++;
        }
        PlayerEntity player = MinecraftClient.getInstance().player;
        List<Event> eventsQueue = new ArrayList<>();
        EventRandomizer big_random = new EventRandomizer(bigDonations);
        EventRandomizer very_big_random = new EventRandomizer(veryBigDonationEvents);
        EventRandomizer small_random = new EventRandomizer(smallDonationEvents);
        if (amount > config.getVeryBigDonationAmount() && config.isEnabledCasino()) {
            testEvents[0].execute(donationAlertsEvent);
        }
        while (SmallDonations > 0) {
            eventsQueue.add(small_random.getRandomEvent());
            SmallDonations--;
        }
        while (BigDonations > 0) {
            eventsQueue.add(big_random.getRandomEvent());
            BigDonations--;
        }
        while (VeryBigDonations > 0) {
            eventsQueue.add(very_big_random.getRandomEvent());
            VeryBigDonations--;
        }
        eventsQueue.forEach(event -> {
            if (!(event instanceof KickEvent)) {
                activeEvents.add(event);
            }
        });
        activeEvents.stream()
                .filter(e -> e instanceof SlowdownEvent)
                .findFirst()
                .ifPresent(activeEvents::remove);
        eventsQueue.stream()
                .filter(e -> e instanceof SlowdownEvent)
                .findFirst()
                .ifPresent(run -> {
                    Event new_event = big_random.getRandomEvent();
                    while (new_event instanceof SlowdownEvent) {
                        new_event = big_random.getRandomEvent();
                    }
                    activeEvents.add(new_event);
                    eventsQueue.add(new_event);
                });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (player != null) {
                    if (!eventsQueue.isEmpty()) {
                        if (!Values.casinoActive) {
                            Event event = eventsQueue.remove(0);
                            event.execute(donationAlertsEvent);
                        }
                    } else {
                        timer.cancel();
                    }
                }
            }
        }, 0, 5000);
    }

    public static void addDonationText(String effectName, String eventName) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player != null) {
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 1f, 1f);
        }
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                if (client.player != null) {
                    if (effectName != null && eventName == null) {
                        Objects.requireNonNull(player).sendMessage(Text.of(I18n.translate("text.donation_mod.message.donation_effect") + " " + effectName), true);
                    } else if (effectName == null && eventName != null) {
                        Objects.requireNonNull(player).sendMessage(Text.literal(I18n.translate("text.donation_mod.message.donation_event") + " " + eventName), true);
                    }
                    count++;
                    if (count == 3) {
                        this.cancel();
                    }
                }
            }
        }, 100, 900);
    }

    public static void addEventEffect(RegistryEntry<StatusEffect> effect, int duration, int level) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int time = duration;

            @Override
            public void run() {
                MinecraftClient client = MinecraftClient.getInstance();
                if (time <= 0) {
                    if (client.player != null) {
                        client.player.removeStatusEffect(effect);
                    }
                    timer.cancel();
                } else {
                    if (client.getNetworkHandler() != null
                            && client.getNetworkHandler().getConnection() != null
                            && client.getNetworkHandler().getConnection().disconnectionInfo != null
                            && client.getNetworkHandler().getConnection().disconnectionInfo.reason() != null
                            && client.getNetworkHandler().getConnection().disconnectionInfo.reason().equals(Text.of("Вы были забанены на этом сервере"))) {
                        timer.cancel();
                    }
                    if (client.player != null) {
                        if (level == 0) {
                            client.player.activeStatusEffects.put(effect, new StatusEffectInstance(effect, time * 20));
                        } else {
                            client.player.activeStatusEffects.put(effect, new StatusEffectInstance(effect, time * 20, level));
                        }
                    }
                    time--;
                }
            }
        }, 0, 1000);
    }
}
