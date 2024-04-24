package org.sakura.plugin.sakurapaperplugin.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.bukkit.Bukkit;
import org.sakura.plugin.sakurapaperplugin.entity.EnvironmentConfig;
import org.sakura.plugin.sakurapaperplugin.entity.GEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakura.plugin.sakurapaperplugin.utils.CryptoUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.sakura.plugin.sakurapaperplugin.utils.ConfigLoader.loadEnvironmentConfig;

public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final OkHttpClient client = new OkHttpClient();
    private WebSocket webSocket;
    private final Gson gson = new Gson();
    public ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

    EnvironmentConfig config = loadEnvironmentConfig("env.json");

    CryptoUtils cryptoUtils = new CryptoUtils();

    public WebSocketService() {
        initiateWebSocketConnection();
    }

    private void initiateWebSocketConnection() {
        Request request = connect();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                logger.info("WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    text = cryptoUtils.decrypt(text);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // 解析json
                JsonObject jsonObject = gson.fromJson(text, JsonObject.class);
                String ts = String.valueOf(jsonObject.get("timestamp"));
                // 判断当前时间和timestamp的时间差
                Instant now = Instant.now();
                Instant timestamp = Instant.parse(ts);
                long diff = now.toEpochMilli() - timestamp.toEpochMilli();
                // 如果时间差大于10s，就不处理这个消息
                if(diff > 10000){
                    logger.error("Message too old, ignoring");
                    return;
                }
                String message = String.valueOf(jsonObject.get("message"));
                logger.info("Received message: " + message);
                messageQueue.add(message);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                logger.info("Closing WebSocket: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                logger.error("WebSocket failure: " + t.getMessage());
                scheduleReconnect();
            }
        };
        webSocket = client.newWebSocket(request, listener);
    }

    private void scheduleReconnect() {
        Timer timer = new Timer("ReconnectTimer", true); // Use daemon thread for the timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Reconnecting WebSocket");
                initiateWebSocketConnection();
            }
        }, 3000);
    }

    public void sendMessage(GEvent event) {
        // 加入timestamp
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().getEpochSecond());
        map.put("message", event);
        String jsonMessage = gson.toJson(map);

        try {
            String encryptedMessage = cryptoUtils.encrypt(jsonMessage);
            if(encryptedMessage != null){
                webSocket.send(encryptedMessage);
            }else {
                logger.error("Failed to encrypt message");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastMessage(String msg) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));
    }

    private Request connect() {
        String ip = config.getIp();
        String port = config.getPort();
        return new Request.Builder().url("ws://"+ip+":"+port).build();
    }

}
