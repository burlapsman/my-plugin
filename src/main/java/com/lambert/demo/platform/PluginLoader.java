package com.lambert.demo.platform;

import com.lambert.demo.cl.OfficialPluginClassLoader;
import com.lambert.demo.log.LogManager;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 加载插件
 */
public class PluginLoader {

    private static final String PLUGIN_DIR = "D:\\my-plugins\\custom";

    public static final String OFFICIAL_PLUGINS_DIR = "D:\\my-plugins\\official";

    public void loadPlugins() {
        // 加载官方插件
        OfficialPluginClassLoader.init(OFFICIAL_PLUGINS_DIR);
        doLoadOfficial();

        // 加载自定义插件
        File dir = new File(PLUGIN_DIR);
        if (!dir.exists()) {
            LogManager.log(String.format("ERROR: plugin directory '%s' not found", PLUGIN_DIR));
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!isJarFile(file.getName())) {
                continue;
            }
            // 从jar中读取插件：1. 读取插件实现类； 2. 加载插件实现类
            String pluginImplClassName = getPluginImplClassName(file);
            if (pluginImplClassName == null) {
                LogManager.log(String.format("WARN: no plugin implementation class name found in jar '%s'", file.getName()));
                continue;
            }
            doLoadPlugin(pluginImplClassName, file, false);
        }
    }

    private void doLoadOfficial() {
        // 扫描jar包
        File file = new File(OFFICIAL_PLUGINS_DIR);
        File[] subFiles = file.listFiles();
        if (subFiles == null) {
            return;
        }
        for (File subFile : subFiles) {
            if (!isJarFile(subFile.getName())) {
                continue;
            }
            // 从jar中读取插件：1. 读取插件实现类； 2. 加载插件实现类
            String pluginImplClassName = getPluginImplClassName(subFile);
            if (pluginImplClassName == null) {
                LogManager.log(String.format("WARN: no plugin implementation class name found in jar '%s'", file.getName()));
                continue;
            }
            doLoadPlugin(pluginImplClassName, subFile, true);
        }
    }

    private void doLoadPlugin(String pluginImplClassName, File file, boolean official) {
        try {
            // 如果是官方插件，则用官方类加载器加载，否则使用自定义类加载器加载
            URLClassLoader urlClassLoader;
            if (official) {
                urlClassLoader = OfficialPluginClassLoader.getInstance();
            } else {
                urlClassLoader = OfficialPluginClassLoader.getSubUrlClassLoader(file.getAbsolutePath());
            }
            Class<?> pluginImplClass = urlClassLoader.loadClass(pluginImplClassName);
            // 实例化
            @SuppressWarnings("unchecked") Constructor<PluginApp> constructor = (Constructor<PluginApp>) pluginImplClass.getConstructor();
            PluginApp pluginApp = constructor.newInstance();
            // 注册插件
            PluginManager.registerPlugin(pluginApp, file, official);
            // 这里不能调用URLClassLoader.close()方法，否则运行过程中将无法加载类
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            LogManager.log(String.format("ERROR: plugin implementation class '%s' not found", pluginImplClassName));
        } catch (NoSuchMethodException e) {
            LogManager.log(String.format("ERROR: plugin implementation class '%s' does not have an empty-param construct", pluginImplClassName));
        }
    }

    private String getPluginImplClassName(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith("MANIFEST.MF")) {
                    continue;
                }
                // 找到 MANIFEST.MF 文件
                InputStream inputStream = jarFile.getInputStream(entry);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while (bufferedReader.ready()) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("plugin-impl: ")) {
                            return line.substring("plugin-impl: ".length());
                        }
                    }
                }
                bufferedReader.close();
                inputStream.close();
            }
        } catch (IOException e) {
            LogManager.log(String.format("ERROR: corrupt jar file '%s'", file.getAbsolutePath()));
        }
        return null;
    }

    private boolean isJarFile(String fileName) {
        return fileName.endsWith(".jar");
    }

}
