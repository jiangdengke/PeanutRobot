package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiagnosticLogExportFileTest {
    @Test
    public void unicodeSnapshotIsWrittenAsExactUtf8Bytes() throws Exception {
        String snapshot = "2026-08-07 | INFO | NAV | 机器人到位\nemoji=😀";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DiagnosticLogExportFile.writeUtf8(outputStream, snapshot);

        assertArrayEquals(snapshot.getBytes(StandardCharsets.UTF_8), outputStream.toByteArray());
    }

    @Test
    public void emptySnapshotWritesNoBytes() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DiagnosticLogExportFile.writeUtf8(outputStream, "");

        assertEquals(0, outputStream.size());
    }

    @Test
    public void plainSnapshotAddsNoHeaderOrTrailingNewline() throws Exception {
        String snapshot = "first line\nsecond line";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DiagnosticLogExportFile.writeUtf8(outputStream, snapshot);

        assertEquals(snapshot, outputStream.toString(StandardCharsets.UTF_8.name()));
    }

    @Test
    public void fileNameUsesDocumentedTimestampFormat() {
        String fileName = DiagnosticLogExportFile.createFileName(0L);

        assertTrue(fileName.matches(
                "robot-runtime-\\d{8}-\\d{6}-\\d{3}\\.txt"
        ));
    }
}
