package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.effects.EventEffects;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class KickEvent extends Event {
    public KickEvent(String name, int duration) {
        super(name, duration);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if(MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof KickEvent);
            return;
        }
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.kick"));
        DonationEvent.addEventEffect(EventEffects.KICK, -1, 0);
        Timer taskTimer = new Timer();
        taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getWorld() != null && DonationEvent.activeEvents.isEmpty()) {
                    taskTimer.cancel();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (MinecraftClient.getInstance().player.getWorld() != null) {
                                Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getConnection().disconnect(Text.of("Вы были забанены на этом сервере"));
                            }
                        }
                    }, getDuration() * 1000L);
                }
            }
        }, 0, 1000);
    }
}
