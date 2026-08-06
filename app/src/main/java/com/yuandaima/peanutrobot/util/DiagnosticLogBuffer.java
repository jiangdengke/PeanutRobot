package com.yuandaima.peanutrobot.util;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

final class DiagnosticLogBuffer {
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private final int maxBytes;
    private final Deque<LogLine> lines = new ArrayDeque<>();
    private int currentBytes;

    DiagnosticLogBuffer(int maxBytes) {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException("maxBytes must be positive");
        }
        this.maxBytes = maxBytes;
    }

    synchronized void add(String line) {
        String boundedLine = truncateToUtf8Bytes(line == null ? "" : line, maxBytes - 1);
        byte[] encodedLine = boundedLine.getBytes(StandardCharsets.UTF_8);
        int storedBytes = encodedLine.length + 1;
        while (!lines.isEmpty() && currentBytes + storedBytes > maxBytes) {
            currentBytes -= lines.removeFirst().storedBytes;
        }
        if (storedBytes > maxBytes) {
            return;
        }
        lines.addLast(new LogLine(boundedLine, storedBytes));
        currentBytes += storedBytes;
    }

    synchronized String snapshot() {
        StringBuilder snapshot = new StringBuilder(currentBytes);
        for (LogLine line : lines) {
            if (snapshot.length() > 0) {
                snapshot.append('\n');
            }
            snapshot.append(line.value);
        }
        return snapshot.toString();
    }

    synchronized void clear() {
        lines.clear();
        currentBytes = 0;
    }

    static String format(long timestampMillis, String level, String module, String message) {
        String timestamp = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.US)
                .format(new Date(timestampMillis));
        return timestamp
                + " | " + sanitizeField(level, "INFO")
                + " | " + sanitizeField(module, "APP")
                + " | " + sanitizeMessage(message);
    }

    private static String sanitizeField(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim().replace('\r', ' ').replace('\n', ' ');
    }

    private static String sanitizeMessage(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("\r\n", "\\n")
                .replace("\r", "\\n")
                .replace("\n", "\\n");
    }

    static String truncateToUtf8Bytes(String value, int byteLimit) {
        if (value.getBytes(StandardCharsets.UTF_8).length <= byteLimit) {
            return value;
        }
        int low = 0;
        int high = value.length();
        while (low < high) {
            int middle = (low + high + 1) / 2;
            String candidate = value.substring(0, middle);
            if (candidate.getBytes(StandardCharsets.UTF_8).length <= byteLimit) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        if (low > 0
                && low < value.length()
                && Character.isHighSurrogate(value.charAt(low - 1))
                && Character.isLowSurrogate(value.charAt(low))) {
            low--;
        }
        return value.substring(0, low);
    }

    private static final class LogLine {
        private final String value;
        private final int storedBytes;

        private LogLine(String value, int storedBytes) {
            this.value = value;
            this.storedBytes = storedBytes;
        }
    }
}
