package com.axalotl.donationmod.events.list;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.events.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class DropItemEvent extends Event {
    public DropItemEvent(String name, int duration, float price) {
        super(name, duration, price);
    }

    @Override
    public void execute(DonationAlertsEvent donationAlertsEvent) {
        if (MinecraftClient.getInstance().player == null) {
            DonationEvent.activeEvents.removeIf(event -> event instanceof DropItemEvent);
            return;
        }
        DonationEvent.addDonationText(null, I18n.translate("effect.donation_mod.drop_item"));
        MinecraftClient.getInstance().player.dropSelectedItem(true);
        DonationEvent.activeEvents.removeIf(event -> event instanceof DropItemEvent);
    }
}
