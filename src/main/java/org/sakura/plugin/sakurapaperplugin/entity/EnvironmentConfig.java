package org.sakura.plugin.sakurapaperplugin.entity;

public class EnvironmentConfig {
    private String ip;
    private String port;
    private String AES_KEY;
    private String AES_BASE64_KEY;

    // Getters and Setters
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getAES_KEY() { return AES_KEY; }
    public void setAES_KEY(String AES_KEY) { this.AES_KEY = AES_KEY; }

    public String getAES_BASE64_KEY() { return AES_BASE64_KEY; }
    public void setAES_BASE64_KEY(String AES_BASE64_KEY) { this.AES_BASE64_KEY = AES_BASE64_KEY; }
}