package org.sakura.plugin.sakurapaperplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sakura.plugin.sakurapaperplugin.websocket.WebSocketService;
import org.sakura.plugin.sakurapaperplugin.entity.GEvent;

import java.util.Objects;

public class WholeListener implements Listener {

    public WholeListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    private WebSocketService webSocketService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        webSocketService.sendMessage(new GEvent(event.getPlayer().getName(), "join",null));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        webSocketService.sendMessage(new GEvent(event.getPlayer().getName(), "leave",null));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        webSocketService.sendMessage(new GEvent(event.getEntity().getName(), "death", event.getDeathMessage()));
    }

}
