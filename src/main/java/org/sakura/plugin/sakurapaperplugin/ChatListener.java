package org.sakura.plugin.sakurapaperplugin;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener, ChatRenderer {

    ChatListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    private WebSocketService webSocketService;

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this);
        if(event.message() instanceof TextComponent textComponent) {
            webSocketService.sendMessage(new GEvent(event.getPlayer().getName(), "chat", textComponent.content()));
        }else{
            webSocketService.sendMessage(new GEvent(event.getPlayer().getName(), "chat", event.message().toString()));
        }


    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        return sourceDisplayName
                .append(Component.text(": "))
                .append(message);
    }
}