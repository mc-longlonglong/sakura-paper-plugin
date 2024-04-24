package org.sakura.plugin.sakurapaperplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WholeListener implements Listener {

    WholeListener(WebSocketService webSocketService) {
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

}
