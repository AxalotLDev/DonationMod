package com.axalotl.donationmod;

import com.axalotl.donationmod.commands.Commands;
import com.axalotl.donationmod.config.ModConfig;
import com.axalotl.donationmod.keys.KeyHandler;
import com.axalotl.donationmod.donationalerts.AlertType;
import com.axalotl.donationmod.donationalerts.DonationAlerts;
import com.axalotl.donationmod.donationalerts.DonationAlertsEvent;
import com.axalotl.donationmod.effects.EventEffects;
import com.axalotl.donationmod.events.DonationEvent;
import com.axalotl.donationmod.listeners.AttackBlockListener;
import com.axalotl.donationmod.listeners.AttackEntityListener;
import com.axalotl.donationmod.listeners.UseItemListener;
import com.axalotl.donationmod.sounds.EventSounds;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.net.URISyntaxException;

public class DonationMod implements ModInitializer {
    private static ConfigHolder<ModConfig> configHolder;
    public static DonationAlerts da;

    @Override
    public void onInitialize() {
        configHolder = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        KeyHandler.registerBindings();
        Commands.registerCommands();
        try {
            String DASERVER = "https://socket.donationalerts.ru:443";
            da = new DonationAlerts(DASERVER);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        EventEffects.registerEffects();
        EventSounds.registerEffects();
        UseItemCallback.EVENT.register(new UseItemListener());
        AttackEntityCallback.EVENT.register(new AttackEntityListener());
        AttackBlockCallback.EVENT.register(new AttackBlockListener());
    }

    public static void DonationAlertsInformation(String message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(I18n.translate("text.donation_mod.message.prefix") + " " + message));
    }

    public static void AddDonation(DonationAlertsEvent event) {
        if (event.Type == AlertType.Donate) {
            System.out.println("work");
            DonationEvent.launchRandomEvent(event);
        }
    }

    public static void save() {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        holder.save();
    }

    public static ModConfig getConfig() {
        return configHolder.getConfig();
    }

}
