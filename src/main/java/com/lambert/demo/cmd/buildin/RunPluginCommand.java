package com.lambert.demo.cmd.buildin;

import com.lambert.demo.cmd.Command;
import com.lambert.demo.log.LogManager;
import com.lambert.demo.platform.PluginApp;
import com.lambert.demo.platform.PluginManager;

import java.util.Arrays;

public class RunPluginCommand implements Command {


    public void run(String cmd, String... args) {
        // todo 检测cmd
        if (args.length == 0) {
            LogManager.log("WARN: lack plugin name");
            return;
        }
        // 获取插件
        PluginApp plugin = PluginManager.getPlugin(args[0]);
        if (plugin == null) {
            LogManager.log("WARN: no such plugin");
            return;
        }
        // 运行插件
        String[] pluginArgs = Arrays.copyOfRange(args, 1, args.length);
        plugin.process(pluginArgs);
    }

}
