package com.opswat.metadefender.scan;

public class ConsoleLog {
    private static final String LOG_FORMAT = "%1$-2s %2$-2s %3$s";

    private String name;
    public ConsoleLog(String name) throws Exception {
        this.name = name;
    }

    public void logInfo(String msg) {
        System.out.println(String.format(LOG_FORMAT, name, "[INFO]" , msg));
    }

    public void logWarn(String msg) {
    	 System.out.println(String.format(LOG_FORMAT, name, "[WARN]", msg));
    }

    public void logError(String msg) {
    	 System.out.println(String.format(LOG_FORMAT, name, "[ERROR]" , msg));
    }

}
