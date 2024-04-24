package org.sakura.plugin.sakurapaperplugin.entity;

public class GEvent {
    private String eventType;

    private String message;

    private String player;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public GEvent(String player, String eventType, String message) {
        this.player = player;
        this.eventType = eventType;
        this.message = message;
    }

}
