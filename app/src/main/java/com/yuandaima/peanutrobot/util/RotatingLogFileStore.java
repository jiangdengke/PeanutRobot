package com.yuandaima.peanutrobot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class RotatingLogFileStore {
    private final File directory;
    private final String filePrefix;
    private final int maxFileBytes;
    private final int maxFiles;

    RotatingLogFileStore(File directory, String filePrefix, int maxFileBytes, int maxFiles) {
        if (directory == null) {
            throw new IllegalArgumentException("directory must not be null");
        }
        if (filePrefix == null || filePrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("filePrefix must not be empty");
        }
        if (maxFileBytes <= 0 || maxFiles <= 0) {
            throw new IllegalArgumentException("file limits must be positive");
        }
        this.directory = directory;
        this.filePrefix = filePrefix;
        this.maxFileBytes = maxFileBytes;
        this.maxFiles = maxFiles;
    }

    synchronized void append(String line) throws IOException {
        ensureDirectory();
        String boundedLine = DiagnosticLogBuffer.truncateToUtf8Bytes(
                line == null ? "" : line,
                maxFileBytes - 1
        );
        byte[] encodedLine = (boundedLine + "\n")
                .getBytes(StandardCharsets.UTF_8);
        File currentFile = fileAt(0);
        if (currentFile.isFile()
                && currentFile.length() > 0
                && currentFile.length() + encodedLine.length > maxFileBytes) {
            rotate();
        }
        try (FileOutputStream output = new FileOutputStream(fileAt(0), true)) {
            output.write(encodedLine);
            output.flush();
        }
    }

    synchronized List<String> readLines() throws IOException {
        ensureDirectory();
        List<String> result = new ArrayList<>();
        for (int fileIndex = maxFiles - 1; fileIndex >= 0; fileIndex--) {
            File logFile = fileAt(fileIndex);
            if (!logFile.isFile()) {
                continue;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(logFile),
                    StandardCharsets.UTF_8
            ))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    synchronized void clear() throws IOException {
        ensureDirectory();
        for (int fileIndex = 0; fileIndex < maxFiles; fileIndex++) {
            File logFile = fileAt(fileIndex);
            if (logFile.exists() && !logFile.delete()) {
                throw new IOException("Unable to delete diagnostic log " + logFile.getName());
            }
        }
    }

    private void ensureDirectory() throws IOException {
        if (directory.isDirectory()) {
            return;
        }
        if (directory.exists() || !directory.mkdirs()) {
            throw new IOException("Unable to create diagnostic log directory");
        }
    }

    private void rotate() throws IOException {
        File oldestFile = fileAt(maxFiles - 1);
        if (oldestFile.exists() && !oldestFile.delete()) {
            throw new IOException("Unable to remove oldest diagnostic log");
        }
        for (int fileIndex = maxFiles - 1; fileIndex > 0; fileIndex--) {
            File source = fileAt(fileIndex - 1);
            if (source.exists() && !source.renameTo(fileAt(fileIndex))) {
                throw new IOException("Unable to rotate diagnostic log " + source.getName());
            }
        }
    }

    private File fileAt(int fileIndex) {
        return new File(directory, filePrefix + "." + fileIndex + ".log");
    }
}
