package com.yuandaima.peanutrobot.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DiagnosticLogRecorder {
    private static final String LOGCAT_TAG = "DiagnosticLog";
    private static final String LOG_DIRECTORY = "diagnostic_logs";
    private static final String LOG_FILE_PREFIX = "runtime";
    private static final int MAX_MEMORY_BYTES = 256 * 1024;
    private static final int MAX_FILE_BYTES = 256 * 1024;
    private static final int MAX_FILES = 4;
    private static final int MAX_ENTRY_BYTES = 8 * 1024;
    private static final int MAX_PENDING_FILE_OPERATIONS = 512;
    private static final Object LOCK = new Object();

    private static DiagnosticLogBuffer buffer;
    private static RotatingLogFileStore fileStore;
    private static ThreadPoolExecutor fileExecutor;
    private static long rejectedFileOperationCount;

    private DiagnosticLogRecorder() {
    }

    public static void initialize(Context context) {
        if (context == null) {
            return;
        }
        synchronized (LOCK) {
            if (buffer != null) {
                return;
            }
            buffer = new DiagnosticLogBuffer(MAX_MEMORY_BYTES);
            File directory = new File(context.getApplicationContext().getFilesDir(), LOG_DIRECTORY);
            fileStore = new RotatingLogFileStore(
                    directory,
                    LOG_FILE_PREFIX,
                    MAX_FILE_BYTES,
                    MAX_FILES
            );
            try {
                List<String> retainedLines = fileStore.readLines();
                for (String retainedLine : retainedLines) {
                    buffer.add(retainedLine);
                }
            } catch (IOException | RuntimeException exception) {
                Log.e(LOGCAT_TAG, "Unable to load retained diagnostic logs", exception);
            }
            fileExecutor = new ThreadPoolExecutor(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(MAX_PENDING_FILE_OPERATIONS),
                    runnable -> {
                        Thread thread = new Thread(runnable, "diagnostic-log-writer");
                        thread.setPriority(Thread.MIN_PRIORITY);
                        return thread;
                    },
                    new ThreadPoolExecutor.AbortPolicy()
            );
        }
        info("APP", "diagnostic recorder initialized");
    }

    public static void debug(String module, String message) {
        record("DEBUG", module, message);
    }

    public static void info(String module, String message) {
        record("INFO", module, message);
    }

    public static void warn(String module, String message) {
        record("WARN", module, message);
    }

    public static void error(String module, String message) {
        record("ERROR", module, message);
    }

    public static String snapshot() {
        synchronized (LOCK) {
            return buffer == null ? "" : buffer.snapshot();
        }
    }

    public static void clear(ClearCallback callback) {
        synchronized (LOCK) {
            if (buffer == null) {
                notifyClearCallback(callback, false);
                return;
            }
            buffer.clear();
            fileExecutor.getQueue().clear();
            boolean submitted = submitFileOperationLocked(() -> {
                boolean success = true;
                try {
                    fileStore.clear();
                } catch (IOException | RuntimeException exception) {
                    success = false;
                    Log.e(LOGCAT_TAG, "Unable to clear diagnostic logs", exception);
                }
                notifyClearCallback(callback, success);
            });
            if (!submitted) {
                notifyClearCallback(callback, false);
            }
        }
        Log.i(LOGCAT_TAG, "Diagnostic log clear requested");
    }

    private static void record(String level, String module, String message) {
        String line = DiagnosticLogBuffer.truncateToUtf8Bytes(
                DiagnosticLogBuffer.format(
                        System.currentTimeMillis(),
                        level,
                        module,
                        message
                ),
                MAX_ENTRY_BYTES
        );
        synchronized (LOCK) {
            if (buffer != null) {
                buffer.add(line);
                submitFileOperationLocked(() -> appendToFile(line));
            }
        }
        writeLogcat(level, module, message);
    }

    private static void appendToFile(String line) {
        try {
            fileStore.append(line);
        } catch (IOException | RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Unable to persist diagnostic log", exception);
        }
    }

    private static boolean submitFileOperationLocked(Runnable operation) {
        if (fileExecutor == null || fileExecutor.isShutdown()) {
            return false;
        }
        try {
            fileExecutor.execute(operation);
            return true;
        } catch (RejectedExecutionException exception) {
            rejectedFileOperationCount++;
            if (rejectedFileOperationCount == 1 || rejectedFileOperationCount % 100 == 0) {
                Log.w(
                        LOGCAT_TAG,
                        "Diagnostic file queue full; dropped operations="
                                + rejectedFileOperationCount
                );
            }
            return false;
        }
    }

    private static void notifyClearCallback(ClearCallback callback, boolean success) {
        if (callback == null) {
            return;
        }
        try {
            callback.onComplete(success);
        } catch (RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Diagnostic clear callback failed", exception);
        }
    }

    private static void writeLogcat(String level, String module, String message) {
        String logcatMessage = "[" + (module == null ? "APP" : module) + "] "
                + (message == null ? "" : message);
        switch (level) {
            case "ERROR":
                Log.e(LOGCAT_TAG, logcatMessage);
                break;
            case "WARN":
                Log.w(LOGCAT_TAG, logcatMessage);
                break;
            case "DEBUG":
                Log.d(LOGCAT_TAG, logcatMessage);
                break;
            default:
                Log.i(LOGCAT_TAG, logcatMessage);
                break;
        }
    }

    public interface ClearCallback {
        void onComplete(boolean success);
    }
}
