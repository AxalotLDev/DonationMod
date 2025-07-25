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

public class StopMoveEvent extends Event {
    public StopMoveEvent(String name, int duration) {
        super(name, duration);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof StopMoveEvent);
            return;
        }
        DonationEvent.addEventEffect(EventEffects.STOP_MOVE, getDuration(), 0);
        DonationEvent.addDonationText(null, I18n.translate( "effect.donation_mod.stop_move"));
        Values.disableEnderPearls = true;
        Values.disableBlockBreak = true;
        Values.disablePlayerMovements = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Values.disableEnderPearls = false;
                Values.disableBlockBreak = false;
                Values.disablePlayerMovements = false;
                DonationEvent.activeEvents.removeIf(event -> event instanceof StopMoveEvent);
                timer.cancel();
            }
        }, getDuration()*1000L);
    }
}
