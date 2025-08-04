package com.axalotl.donationmod.events;

import com.axalotl.donationmod.DonationMod;
import com.axalotl.donationmod.config.ModConfig;
import com.axalotl.donationmod.donationalerts.AlertType;
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
import java.util.stream.Stream;

public class DonationEvent {
    public static List<Event> activeEvents = new ArrayList<>();
    private static final ModConfig config = DonationMod.getConfig();
    private static final Random random = new Random();

    public static final Event[] bigDonations = {
            new StopMoveEvent(I18n.translate("effect.donation_mod.stop_move"), config.getStopMoveDuration(), config.getBigDonationAmount()),
            new NoJumpEvent(I18n.translate("effect.donation_mod.no_jump"), config.getNoJumpDuration(), config.getBigDonationAmount()),
            new NoMob(I18n.translate("effect.donation_mod.no_mob"), config.getNoMobDuration(), config.getBigDonationAmount()),
            new RotateScreen(I18n.translate("effect.donation_mod.rotate_screen"), config.getRotateScreenDuration(), config.getBigDonationAmount()),
            new InvertedControl(I18n.translate("effect.donation_mod.inverted_control"), config.getInvertedControlDuration(), config.getBigDonationAmount()),
            new CancelDamage(I18n.translate("effect.donation_mod.cancel_damage"), config.getCancelDamageDuration(), config.getBigDonationAmount()),
            new NoBowEvent(I18n.translate("effect.donation_mod.no_bow"), config.getNoBowDuration(), config.getBigDonationAmount()),
            new SlowdownEvent(I18n.translate("effect.donation_mod.slowdown"), config.getSlowdownDuration(), config.getBigDonationAmount()),
            new ThirdPersonEvent(I18n.translate("effect.donation_mod.third_person"), config.getThirdPersonDuration(), config.getBigDonationAmount()),
            new CinematicCameraEvent(I18n.translate("effect.donation_mod.cinematic_camera"), config.getCinematicCameraDuration(), config.getBigDonationAmount()),
            new ScarryEvent(I18n.translate("effect.donation_mod.screamer"), 1, config.getBigDonationAmount())
    };

    public static final Event[] veryBigDonationEvents = {
            new KickEvent(I18n.translate("effect.donation_mod.kick"), 5, config.getVeryBigDonationAmount()),
            new DisableElytraEvent(I18n.translate("effect.donation_mod.disable_elytra"), config.getDisableElytraDuration(), config.getVeryBigDonationAmount()),
            new NoFriendsEvent(I18n.translate("effect.donation_mod.no_friends"), config.getNoFriendsDuration(), config.getVeryBigDonationAmount())
    };

    public static final Event[] smallDonationEvents = {
            new EffectEvent(I18n.translate("effect.donation_mod.effect"), config.getFirstEffectDuration(), config.getFirstEffectDonationAmount()),
            new DropItemEvent(I18n.translate("effect.donation_mod.drop_item"), 0, config.getSecondEffectDonationAmount()),
            new EffectEvent(I18n.translate("effect.donation_mod.effect"), config.getSecondEffectDuration(), config.getSecondEffectDonationAmount()),
            new EffectEvent(I18n.translate("effect.donation_mod.effect"), config.getThirdEffectDuration(), config.getThirdEffectDonationAmount())
    };

    public static final Event[] testEvents = {
            new CasinoEvent(I18n.translate("effect.donation_mod.casino"), 5, 0)
    };

    private static final Event[] allEvents = Stream.of(
            veryBigDonationEvents,
            bigDonations,
            smallDonationEvents
    ).flatMap(Stream::of).toArray(Event[]::new);

    public static Event getEventByName(Event[] events, String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return event;
            }
        }
        return null;
    }

    public static void runEvent(DonationAlertsEvent event) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && event.Type == AlertType.Donate) {
            List<Event> donationsQueue = createQueue(event);
            if (donationsQueue.isEmpty()) return;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (donationsQueue.isEmpty()) {
                        this.cancel();
                        return;
                    }
                    Event next = donationsQueue.remove(0);
                    next.execute(event);
                }
            }, 0, 5000);
        }
    }

    private static List<Event> createQueue(DonationAlertsEvent donate) {
        float amount = donate.AmountMain;
        List<Event> eventsQueue = new ArrayList<>();
        List<Event> allEventsSorted = new ArrayList<>(Arrays.stream(allEvents).sorted(Comparator.comparingDouble(Event::getPrice)).toList());
        Collections.reverse(allEventsSorted);
        List<Float> prices = new ArrayList<>();
        for (Event event : allEventsSorted) {
            prices.add(event.getPrice());
        }
        for (float price : prices.stream().distinct().toList()) {
            while (price <= amount) {
                List<Event> eventsWithTargetPrice = allEventsSorted.stream()
                        .filter(event -> event.getPrice() == price)
                        .toList();
                int randomIndex = random.nextInt(eventsWithTargetPrice.size());
                eventsQueue.add(eventsWithTargetPrice.get(randomIndex));
                amount -= price;
            }
        }
        return eventsQueue;
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
                        if (player != null) {
                            player.sendMessage(Text.of(I18n.translate("text.donation_mod.message.donation_effect") + " " + effectName), true);
                        }
                    } else if (effectName == null && eventName != null) {
                        if (player != null) {
                            player.sendMessage(Text.literal(I18n.translate("text.donation_mod.message.donation_event") + " " + eventName), true);
                        }
                    }
                    if (++count == 3) this.cancel();
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
                    if (client.player != null) client.player.removeStatusEffect(effect);
                    timer.cancel();
                } else {
                    if (client.getNetworkHandler() != null
                            && client.getNetworkHandler().getConnection().disconnectionInfo != null
                            && Text.of(I18n.translate("effect.donation_mod.kick")).equals(client.getNetworkHandler().getConnection().disconnectionInfo.reason())) {
                        timer.cancel();
                        return;
                    }
                    if (client.player != null) {
                        StatusEffectInstance instance = level == 0 ?
                                new StatusEffectInstance(effect, time * 20) :
                                new StatusEffectInstance(effect, time * 20, level);
                        client.player.activeStatusEffects.put(effect, instance);
                    }
                    time--;
                }
            }
        }, 0, 1000);
    }
}