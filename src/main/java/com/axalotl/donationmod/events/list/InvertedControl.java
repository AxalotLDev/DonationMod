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

public class InvertedControl extends Event {
    public InvertedControl(String name, int duration) {
        super(name, duration);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if(MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof InvertedControl);
            return;
        }
        Values.invertedControl = true;
        DonationEvent.addDonationText(null, I18n.translate( "effect.donation_mod.inverted_control"));
        DonationEvent.addEventEffect(EventEffects.INVERTED_CONTROL,getDuration(), 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Values.invertedControl = false;
                DonationEvent.activeEvents.removeIf(event -> event instanceof InvertedControl);
            }
        }, getDuration() * 1000L);
    }
}
