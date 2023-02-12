package com.lambert.demo.log;

public class LogManager {

    private static final StandardLogger STANDARD_LOGGER = new StandardLogger();

    public static void log(String content) {
        log(content, true);
    }

    public static void log(String content, boolean changeLine) {
        STANDARD_LOGGER.log(content);
        if (changeLine) {
            STANDARD_LOGGER.log("\n");
        }
    }

}
