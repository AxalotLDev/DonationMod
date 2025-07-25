package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.effects.EventEffects;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SlowdownEvent extends Event {
    public SlowdownEvent(String name, int duration) {
        super(name, duration);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof SlowdownEvent);
            return;
        }
        EntityAttributeModifier modifier = new EntityAttributeModifier(Identifier.of("hyperSpeed"), -0.8d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.slowdown"));
        DonationEvent.addEventEffect(EventEffects.SLOWDOWN, getDuration(), 0);
        Timer slow = new Timer();
        slow.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MinecraftClient.getInstance().player != null && Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).getModifier(modifier.id()) == null)
                    Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).addTemporaryModifier(modifier);
            }
        }, 0, 50);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                slow.cancel();
                Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).removeModifier(modifier.id());
                DonationEvent.activeEvents.removeIf(event -> event instanceof SlowdownEvent);
            }
        }, getDuration() * 1000L);
    }
}
