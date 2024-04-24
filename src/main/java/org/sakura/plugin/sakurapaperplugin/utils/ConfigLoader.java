package org.sakura.plugin.sakurapaperplugin.utils;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.sakura.plugin.sakurapaperplugin.entity.EnvironmentConfig;

public class ConfigLoader {

    public static EnvironmentConfig loadEnvironmentConfig(String fileName) {
        Gson gson = new Gson();
        InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException("Cannot find resource file: " + fileName);
        }
        Reader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, EnvironmentConfig.class);
    }
}