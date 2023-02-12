package com.lambert.demo.platform;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {

    private static final Map<String, PluginApp> namePlugin = new ConcurrentHashMap<>();
    private static final Map<String, String> nameFile = new ConcurrentHashMap<>();

    private static final Set<String> officialNames = Collections.synchronizedSet(new HashSet<>());

    public static List<PluginApp> getPlugins() {
        return new ArrayList<>(namePlugin.values());
    }

    public static void registerPlugin(PluginApp pluginApp, File file, boolean official) {
        if (pluginApp == null || file == null) {
            throw new NullPointerException();
        }
        namePlugin.put(pluginApp.getName(), pluginApp);
        nameFile.put(pluginApp.getName(), fileKey(file));
        if (official) {
            officialNames.add(pluginApp.getName());
        }
    }

    public static PluginApp getPlugin(String name) {
        return namePlugin.get(name);
    }

    public static boolean isOfficial(String name) {
        return officialNames.contains(name);
    }

    private static String fileKey(File file) {
        return file.getName() + ":" + file.lastModified();
    }

}
