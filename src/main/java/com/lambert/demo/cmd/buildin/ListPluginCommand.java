package com.lambert.demo.cmd.buildin;

import com.lambert.demo.cmd.Command;
import com.lambert.demo.log.LogManager;
import com.lambert.demo.platform.PluginApp;
import com.lambert.demo.platform.PluginManager;

import java.util.List;

public class ListPluginCommand implements Command {


    public void run(String cmd, String... args) {
        // todo 检测cmd
        List<PluginApp> plugins = PluginManager.getPlugins();
        StringBuilder builder = new StringBuilder();
        builder.append("plugins total: ").append(plugins.size()).append("\n");
        for (PluginApp plugin : plugins) {
            boolean isOfficial = PluginManager.isOfficial(plugin.getName());
            builder.append(" - ").append(plugin.getName()).append(isOfficial ? "*" : "").append("\n");
        }
        LogManager.log(builder.toString());
    }

}
