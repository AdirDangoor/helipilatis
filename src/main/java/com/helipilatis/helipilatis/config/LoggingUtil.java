package com.helipilatis.helipilatis.config;
import java.util.logging.Logger;

public class LoggingUtil {
    public static void logFunctionName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // The 2nd element in the stack trace is the caller method
        String functionName = stackTrace[2].getMethodName();
        Logger logger = Logger.getLogger(stackTrace[2].getClassName());
        logger.info("[Function] " + functionName);
    }
}