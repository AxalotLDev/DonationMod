package com.axalotl.donationmod.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EventEffects {
    public static RegistryEntry<StatusEffect> STOP_MOVE;
    public static RegistryEntry<StatusEffect> CANCEL_DAMAGE;
    public static RegistryEntry<StatusEffect> NO_BOW;
    public static RegistryEntry<StatusEffect> NO_JUMP;
    public static RegistryEntry<StatusEffect> NO_MOB;
    public static RegistryEntry<StatusEffect> ROTATE_SCREEN;
    public static RegistryEntry<StatusEffect> INVERTED_CONTROL;
    public static RegistryEntry<StatusEffect> KICK;
    public static RegistryEntry<StatusEffect> DISABLE_ELYTRA;
    public static RegistryEntry<StatusEffect> SLOWDOWN;
    public static RegistryEntry<StatusEffect> THIRD_PERSON;
    public static RegistryEntry<StatusEffect> CINEMATIC_CAMERA;
    public static RegistryEntry<StatusEffect> NO_FRIENDS;

    public static RegistryEntry<StatusEffect> registerReference(String name) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("donation_mod", name),
                new DonationEffect(StatusEffectCategory.BENEFICIAL, 31246867));
    }

    public static void registerEffects() {
        STOP_MOVE = registerReference("stop_move");
        CANCEL_DAMAGE = registerReference("cancel_damage");
        NO_BOW = registerReference("no_bow");
        NO_JUMP = registerReference("no_jump");
        NO_MOB = registerReference("no_mob");
        ROTATE_SCREEN = registerReference("rotate_screen");
        INVERTED_CONTROL = registerReference("inverted_control");
        KICK = registerReference("kick");
        DISABLE_ELYTRA = registerReference("disable_elytra");
        SLOWDOWN = registerReference("slowdown");
        THIRD_PERSON = registerReference("third_person");
        CINEMATIC_CAMERA = registerReference("cinematic_camera");
        NO_FRIENDS = registerReference("no_friends");
    }
}
