package org.sakura.plugin.sakurapaperplugin;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final OkHttpClient client = new OkHttpClient();
    private WebSocket webSocket;
    private final Gson gson = new Gson();
    public ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

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
                logger.info("Received message: " + text);
                messageQueue.add(text);
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
        String jsonMessage = gson.toJson(event);
        if (webSocket != null) {
            webSocket.send(jsonMessage);
        }
    }

    public void broadcastMessage(String msg) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));
    }

    private Request connect() {
        return new Request.Builder().url("ws://222.65.172.72:20112").build();
    }

}
