package com.lambert.demo.cmd.buildin;

import com.lambert.demo.cmd.Command;

/**
 * 内置命令枚举类
 */
public enum BuildInCommandEnum {
    LIST_PLUGIN("lp", new ListPluginCommand()),
    RUN_PLUGIN("rp", new RunPluginCommand()),
    EXIT("exit", new ExitCommand()),
    ;

    private String name;

    private Command handler;

    BuildInCommandEnum(final String name, final Command handler) {
        this.name = name;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public Command getHandler() {
        return handler;
    }

    public static BuildInCommandEnum fromName(String name) {
        for (com.lambert.demo.cmd.buildin.BuildInCommandEnum value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}
