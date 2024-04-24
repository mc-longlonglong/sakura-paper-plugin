package org.sakura.plugin.sakurapaperplugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.sakura.plugin.sakurapaperplugin.listeners.ChatListener;
import org.sakura.plugin.sakurapaperplugin.listeners.WholeListener;
import org.sakura.plugin.sakurapaperplugin.websocket.WebSocketService;

import java.util.Objects;

public final class SakuraPaperPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        WebSocketService webSocketService = new WebSocketService();
        getServer().getPluginManager().registerEvents(new ChatListener(webSocketService), this);
        getServer().getPluginManager().registerEvents(new WholeListener(webSocketService), this);

        // 获取tick，每一tick都去检查是否有新的消息
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!webSocketService.messageQueue.isEmpty()) {
                webSocketService.broadcastMessage(Objects.requireNonNull(webSocketService.messageQueue.poll()));
            }
        }, 0L, 1L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
