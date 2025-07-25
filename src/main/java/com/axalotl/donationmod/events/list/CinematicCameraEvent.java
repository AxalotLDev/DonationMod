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

public class CinematicCameraEvent extends Event {
    public CinematicCameraEvent(String name, int duration) {
        super(name, duration);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if(MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof CinematicCameraEvent);
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.smoothCameraEnabled = true;
        Values.forceFov = true;
        Values.fov = 60;
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.cinematic_camera"));
        DonationEvent.addEventEffect(EventEffects.CINEMATIC_CAMERA,getDuration(), 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                client.options.smoothCameraEnabled = false;
                Values.forceFov = false;
                Values.fov = 0;
                DonationEvent.activeEvents.removeIf(event -> event instanceof CinematicCameraEvent);
            }
        }, getDuration() * 1000L);
    }
}
