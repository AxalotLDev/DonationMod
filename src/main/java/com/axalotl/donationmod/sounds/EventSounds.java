package com.axalotl.donationmod.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class EventSounds {
    public static SoundEvent SCARY_SOUND;

    private static SoundEvent registerEvent() {
        Identifier id = Identifier.of("donation_mod", "scary_sound");
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerEffects() {
        SCARY_SOUND = EventSounds.registerEvent();
    }
}