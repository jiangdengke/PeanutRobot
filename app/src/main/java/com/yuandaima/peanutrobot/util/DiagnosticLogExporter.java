package com.yuandaima.peanutrobot.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DiagnosticLogExporter {
    private static final String LOGCAT_TAG = "DiagnosticLogExport";
    private static final String EXPORT_DIRECTORY_NAME = "PeanutRobotLogs";
    private static final String RELATIVE_EXPORT_DIRECTORY =
            Environment.DIRECTORY_DOWNLOADS + "/" + EXPORT_DIRECTORY_NAME;
    private static final int MAX_PENDING_EXPORTS = 4;
    private static final ThreadPoolExecutor EXPORT_EXECUTOR = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(MAX_PENDING_EXPORTS),
            runnable -> {
                Thread thread = new Thread(runnable, "diagnostic-log-exporter");
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

    private DiagnosticLogExporter() {
    }

    public static void export(Context context, String snapshot, Callback callback) {
        if (context == null) {
            notifyCallback(callback, ExportResult.failure("应用上下文不可用"));
            return;
        }
        if (snapshot == null || snapshot.isEmpty()) {
            notifyCallback(callback, ExportResult.failure("没有可导出的运行日志"));
            return;
        }

        Context applicationContext = context.getApplicationContext();
        Context exportContext = applicationContext == null ? context : applicationContext;
        String fileName;
        try {
            fileName = DiagnosticLogExportFile.createFileName(System.currentTimeMillis());
        } catch (RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Unable to create diagnostic export file name", exception);
            notifyCallback(callback, ExportResult.failure("无法生成导出文件名"));
            return;
        }

        try {
            EXPORT_EXECUTOR.execute(() -> notifyCallback(
                    callback,
                    exportSnapshot(exportContext, fileName, snapshot)
            ));
        } catch (RejectedExecutionException exception) {
            Log.w(LOGCAT_TAG, "Diagnostic export queue is full", exception);
            notifyCallback(callback, ExportResult.failure("导出任务繁忙，请稍后重试"));
        } catch (RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Unable to schedule diagnostic export", exception);
            notifyCallback(callback, ExportResult.failure("无法启动导出任务"));
        }
    }

    private static ExportResult exportSnapshot(
            Context context,
            String fileName,
            String snapshot
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return exportToMediaStore(context, fileName, snapshot);
            }
            return exportToLegacyDownloads(fileName, snapshot);
        } catch (SecurityException exception) {
            Log.e(LOGCAT_TAG, "Storage access denied while exporting diagnostic logs", exception);
            return ExportResult.failure("系统拒绝访问下载目录");
        } catch (IOException exception) {
            Log.e(LOGCAT_TAG, "I/O failure while exporting diagnostic logs", exception);
            return ExportResult.failure("写入下载目录失败");
        } catch (RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Unexpected diagnostic export failure", exception);
            return ExportResult.failure("导出运行日志失败");
        }
    }

    private static ExportResult exportToMediaStore(
            Context context,
            String fileName,
            String snapshot
    ) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_EXPORT_DIRECTORY);
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri pendingUri = null;
        try {
            pendingUri = contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
            );
            if (pendingUri == null) {
                throw new IOException("MediaStore did not create an export entry");
            }

            try (OutputStream outputStream = contentResolver.openOutputStream(pendingUri, "w")) {
                if (outputStream == null) {
                    throw new IOException("MediaStore did not provide an output stream");
                }
                DiagnosticLogExportFile.writeUtf8(outputStream, snapshot);
            }

            ContentValues publishValues = new ContentValues();
            publishValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
            int updatedRows = contentResolver.update(pendingUri, publishValues, null, null);
            if (updatedRows != 1) {
                throw new IOException("MediaStore did not publish the export entry");
            }
            return ExportResult.success(RELATIVE_EXPORT_DIRECTORY + "/" + fileName);
        } catch (IOException | RuntimeException exception) {
            deletePendingMediaStoreEntry(contentResolver, pendingUri);
            throw exception;
        }
    }

    private static ExportResult exportToLegacyDownloads(
            String fileName,
            String snapshot
    ) throws IOException {
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        );
        File exportDirectory = new File(downloadsDirectory, EXPORT_DIRECTORY_NAME);
        if (!exportDirectory.isDirectory() && !exportDirectory.mkdirs()) {
            throw new IOException("Unable to create diagnostic export directory");
        }

        File exportFile = new File(exportDirectory, fileName);
        if (!exportFile.createNewFile()) {
            throw new IOException("Diagnostic export file already exists");
        }
        try (FileOutputStream outputStream = new FileOutputStream(exportFile, false)) {
            DiagnosticLogExportFile.writeUtf8(outputStream, snapshot);
        } catch (IOException | RuntimeException exception) {
            deleteLegacyPartialFile(exportFile);
            throw exception;
        }
        return ExportResult.success(exportFile.getAbsolutePath());
    }

    private static void deletePendingMediaStoreEntry(
            ContentResolver contentResolver,
            Uri pendingUri
    ) {
        if (pendingUri == null) {
            return;
        }
        try {
            contentResolver.delete(pendingUri, null, null);
        } catch (RuntimeException cleanupException) {
            Log.e(LOGCAT_TAG, "Unable to remove incomplete MediaStore export", cleanupException);
        }
    }

    private static void deleteLegacyPartialFile(File exportFile) {
        try {
            if (exportFile.exists() && !exportFile.delete()) {
                Log.w(LOGCAT_TAG, "Unable to remove incomplete legacy export " + exportFile);
            }
        } catch (RuntimeException cleanupException) {
            Log.e(LOGCAT_TAG, "Unable to remove incomplete legacy export", cleanupException);
        }
    }

    private static void notifyCallback(Callback callback, ExportResult result) {
        if (callback == null) {
            return;
        }
        try {
            callback.onComplete(result);
        } catch (RuntimeException exception) {
            Log.e(LOGCAT_TAG, "Diagnostic export callback failed", exception);
        }
    }

    public interface Callback {
        void onComplete(ExportResult result);
    }

    public static final class ExportResult {
        private final boolean success;
        private final String displayPath;
        private final String errorMessage;

        private ExportResult(boolean success, String displayPath, String errorMessage) {
            this.success = success;
            this.displayPath = displayPath;
            this.errorMessage = errorMessage;
        }

        static ExportResult success(String displayPath) {
            return new ExportResult(true, displayPath, "");
        }

        static ExportResult failure(String errorMessage) {
            return new ExportResult(false, "", errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getDisplayPath() {
            return displayPath;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
