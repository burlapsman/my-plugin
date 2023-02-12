package com.lambert.demo.platform;

/**
 * 所以插件的入口，插件必须实现该接口
 */
public interface PluginApp {

    void process(String... args);

    String getName();

}
