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

public class RotateScreen extends Event {
    public RotateScreen(String name, int duration, float price) {
        super(name, duration, price);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if(MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof RotateScreen);
            return;
        }
        Values.cameraRotate = 180;
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.rotate_screen"));
        DonationEvent.addEventEffect(EventEffects.ROTATE_SCREEN,getDuration(), 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Values.cameraRotate = 0;
                DonationEvent.activeEvents.removeIf(event -> event instanceof RotateScreen);
            }
        }, getDuration() * 1000L);
    }
}
