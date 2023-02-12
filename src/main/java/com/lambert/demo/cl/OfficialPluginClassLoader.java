package com.lambert.demo.cl;

import com.lambert.demo.log.LogManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 官方插件的实现类可以提供给自定义插件使用
 */
public class OfficialPluginClassLoader extends URLClassLoader {


    private static OfficialPluginClassLoader instance = null;

    public static OfficialPluginClassLoader getInstance() {
        return instance;
    }

    private OfficialPluginClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
    }

    // 暂时不考虑线程安全
    // TODO 不够优雅，类加载器需要重新设计
    public static void init(String dir) {
        File dirFile = new File(dir);
        File[] files = dirFile.listFiles();
        List<URL> urls = Arrays.stream(files).filter(f -> f.getName().endsWith(".jar"))
                .map(f -> f.toURI())
                .map(u -> {
                    try {
                        return u.toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("ERROR: fail to init official class loader", e);
                    }
                })
                .collect(Collectors.toList());

        URL[] urlArrays = new URL[urls.size()];
        for (int i = 0; i < urlArrays.length; i++) {
            urlArrays[i] = urls.get(i);
        }
        instance = new OfficialPluginClassLoader(urlArrays);
    }

    private static URL[] getUrls(String path) {
        try {
            URL url = new File(path).toURI().toURL();
            return new URL[]{url};
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("WARN: fail to get url from path '%s'", path));
        }
    }

    /**
     * 创建一个子的类加载器
     */
    public static URLClassLoader getSubUrlClassLoader(String path) {
        return new URLClassLoader(getUrls(path), instance);
    }

}
