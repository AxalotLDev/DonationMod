package com.axalotl.donationmod.events;

import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;

public abstract class Event {
    private final String name;
    private int duration;
    private final float price;

    public Event(String name, int duration, float price) {
        this.name = name;
        this.duration = duration;
        this.price = price;
    }
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public abstract void execute(DonationAlertsEvent donationAlertsEvent);
}
