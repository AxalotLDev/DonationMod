package com.axalotl.donationmod.donationalerts;

import com.axalotl.donationmod.DonationMod;
import io.socket.client.IO;
import io.socket.client.Socket;
import net.minecraft.client.resource.language.I18n;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class DonationAlerts {
    private final Socket sock;

    public DonationAlerts(String server) throws URISyntaxException {
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"}; // обязательно, иначе может пытаться использовать polling
        options.reconnection = true;
        options.forceNew = true;

        URI uri = new URI(server); // например: "https://socket.donationalerts.ru:443"
        sock = IO.socket(uri, options);

        // Подключение
        sock.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("✅ Socket connected");
            DonationMod.DonationAlertsInformation(I18n.translate("text.donation_mod.message.connect"));
        });

        // Отключение
        sock.on(Socket.EVENT_DISCONNECT, args -> {
            System.out.println("🔌 Socket disconnected");
            DonationMod.DonationAlertsInformation(I18n.translate("text.donation_mod.message.disconnect"));
        });

        // Ошибка подключения
        sock.on(Socket.EVENT_CONNECT_ERROR, args -> {
            System.err.println("❌ Socket connect error: " + (args.length > 0 ? args[0] : "Unknown"));
            DonationMod.DonationAlertsInformation(I18n.translate("text.donation_mod.message.error"));
        });

        // Ошибка соединения (дополнительно)
        sock.on(Socket.EVENT_CONNECT_ERROR, args -> System.err.println("⚠️ Socket error: " + (args.length > 0 ? args[0] : "Unknown")));

        // Обработка доната
        sock.on("donation", args -> {
            if (args.length > 0 && args[0] instanceof String) {
                try {
                    DonationMod.AddDonation(
                            Objects.requireNonNull(DonationAlertsEvent.getDonationAlertsEvent((String) args[0]))
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Connect(String token) throws JSONException {
        if (!sock.connected()) {
            sock.connect();
        }

        JSONObject auth = new JSONObject()
                .put("token", token)
                .put("type", "minor");

        System.out.println("➡️ Sending add-user with token");
        sock.emit("add-user", auth);
    }

    public void Disconnect() {
        sock.disconnect();
    }

    public boolean getConnected() {
        return sock.connected();
    }
}
