package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import com.axalotl.donationmod.sounds.EventSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;

public class ScarryEvent extends Event {
    public ScarryEvent(String name, int duration, float price) {
        super(name, duration, price);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof ScarryEvent);
            return;
        }
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.screamer"));
        DonationEvent.addEventEffect(StatusEffects.BLINDNESS, 3, 0);
        MinecraftClient.getInstance().player.playSound(EventSounds.SCARY_SOUND, 1f, 1f);
        MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.ELDER_GUARDIAN, MinecraftClient.getInstance().player.getX() + 0.5, MinecraftClient.getInstance().player.getY() + 1, MinecraftClient.getInstance().player.getZ() + 0.5, 0, 0, 0);
        DonationEvent.activeEvents.removeIf(event -> event instanceof ScarryEvent);
    }
}
