package com.lambert.demo.cmd.buildin;

import com.lambert.demo.PlatformApp;
import com.lambert.demo.cmd.Command;
import com.lambert.demo.log.LogManager;

public class ExitCommand implements Command {

    @Override
    public void run(String cmd, String... args) {
        LogManager.log("good bye");
        PlatformApp.stop();
    }

}
