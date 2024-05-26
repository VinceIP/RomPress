package org.rompress.utils;

public class FileUtils {
    //Convert a long of bytes into KB/MB/GB with decimal precision
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.3f %s", bytes / Math.pow(unit, exp), pre);
    }
}
