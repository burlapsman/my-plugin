package com.lambert.demo.platform;

import com.lambert.demo.log.LogManager;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 加载插件
 */
public class PluginLoader {

    private static final String PLUGIN_DIR = "D:\\my-plugins";

    public void loadPlugins() {
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
            doLoadPlugin(pluginImplClassName, file);
        }
    }

    private void doLoadPlugin(String pluginImplClassName, File file) {
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            // 这里不能调用URLClassLoader.close()方法，否则运行过程中将无法加载类
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
            Class<?> pluginImplClass = urlClassLoader.loadClass(pluginImplClassName);
            // 实例化
            @SuppressWarnings("unchecked") Constructor<PluginApp> constructor = (Constructor<PluginApp>) pluginImplClass.getConstructor();
            PluginApp pluginApp = constructor.newInstance();
            // 注册插件
            PluginManager.registerPlugin(pluginApp, file);
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
