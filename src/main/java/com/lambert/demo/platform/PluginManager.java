package com.lambert.demo.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {

    private static final Map<String, PluginApp> namePlugin = new ConcurrentHashMap<>();
    private static final Map<String, String> nameFile = new ConcurrentHashMap<>();

    public static List<PluginApp> getPlugins() {
        return new ArrayList<>(namePlugin.values());
    }

    public static void registerPlugin(PluginApp pluginApp, File file) {
        if (pluginApp == null || file == null) {
            throw new NullPointerException();
        }
        namePlugin.put(pluginApp.getName(), pluginApp);
        nameFile.put(pluginApp.getName(), fileKey(file));
    }

    public static PluginApp getPlugin(String name) {
        return namePlugin.get(name);
    }

    private static String fileKey(File file) {
        return file.getName() + ":" + file.lastModified();
    }

}
