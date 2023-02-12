package com.lambert.demo;

import com.lambert.demo.cmd.Command;
import com.lambert.demo.cmd.DelegateCommand;
import com.lambert.demo.log.LogManager;
import com.lambert.demo.platform.PluginLoader;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 插件平台启动类，完成以下功能：
 * 1. 监听插件变化，完成插件自动装载；
 * 2. 管理插件，控制插件的类访问权限；
 * 3. 本身是一个控制台应用，可以交互；
 * <p>
 * 插件示例：
 * - 自定义插件：插件加载后，可以通过命令行手动运行，命令就是插件名，自定义插件之间相互隔离；
 * - 官方插件：由官方提供的插件，且插件功能对外开放，自定义插件可以引用官方内置插件；
 */
public class PlatformApp {

    /**
     * 是否停止运行
     */
    private static volatile boolean stopped = false;

    public static void main(String[] args) throws Exception {
        // 加载插件
        PluginLoader pluginLoader = new PluginLoader();
        pluginLoader.loadPlugins();

        // 接受命令并执行
        accept();
    }

    public static void stop() {
        stopped = true;
    }

    private static void accept() {
        Command commandHandler = new DelegateCommand();
        Scanner scanner = new Scanner(System.in);
        while (!stopped) {
            LogManager.log("> ", false);
            String line = scanner.nextLine();
            if (line == null || line.length() == 0) {
                continue;
            }
            String[] s = line.split(" ");
            String[] args = Arrays.copyOfRange(s, 1, s.length);
            commandHandler.run(s[0], args);
        }
    }

    private static void testLoadJar() {

    }

}
