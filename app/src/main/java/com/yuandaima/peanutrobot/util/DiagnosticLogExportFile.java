package com.yuandaima.peanutrobot.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DiagnosticLogExportFile {
    private static final String FILE_NAME_PREFIX = "robot-runtime-";
    private static final String FILE_NAME_PATTERN = "yyyyMMdd-HHmmss-SSS";

    private DiagnosticLogExportFile() {
    }

    static String createFileName(long timestampMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FILE_NAME_PATTERN, Locale.US);
        return FILE_NAME_PREFIX + dateFormat.format(new Date(timestampMillis)) + ".txt";
    }

    static void writeUtf8(OutputStream outputStream, String snapshot) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("outputStream must not be null");
        }
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot must not be null");
        }
        outputStream.write(snapshot.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
