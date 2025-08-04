package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.effects.EventEffects;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import com.axalotl.donationmod.events.Values;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

import java.util.Timer;
import java.util.TimerTask;

public class NoBowEvent extends Event {
    public NoBowEvent(String name, int duration, float price) {
        super(name, duration, price);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof NoBowEvent);
            return;
        }
        Values.noBow = true;
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.no_bow"));
        DonationEvent.addEventEffect(EventEffects.NO_BOW, getDuration(), 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Values.noBow = false;
                DonationEvent.activeEvents.removeIf(event -> event instanceof NoBowEvent);
            }
        }, getDuration() * 1000L);
    }
}
