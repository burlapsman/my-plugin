package com.lambert.demo.cmd;

import com.lambert.demo.cmd.buildin.BuildInCommandEnum;
import com.lambert.demo.log.LogManager;

/**
 * 委派命令
 */
public class DelegateCommand implements Command {

    public void run(String cmd, String... args) {
        BuildInCommandEnum commandEnum = BuildInCommandEnum.fromName(cmd);
        if (commandEnum == null) {
            LogManager.log("WARN: command not found");
            return;
        }
        Command handler = commandEnum.getHandler();
        handler.run(cmd, args);
    }

}
