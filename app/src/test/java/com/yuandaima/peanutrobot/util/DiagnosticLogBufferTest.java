package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DiagnosticLogBufferTest {
    @Test
    public void formatCreatesSingleStructuredLine() {
        String formatted = DiagnosticLogBuffer.format(
                0L,
                "INFO",
                "NAV",
                "started\nnext"
        );

        assertTrue(formatted.matches(
                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}"
                        + " \\| INFO \\| NAV \\| started\\\\nnext"
        ));
        assertFalse(formatted.contains("started\nnext"));
    }

    @Test
    public void capacityEvictsOldestLinesAndClearRemovesSnapshot() {
        DiagnosticLogBuffer buffer = new DiagnosticLogBuffer(12);

        buffer.add("first");
        buffer.add("second");

        assertEquals("second", buffer.snapshot());

        buffer.clear();

        assertEquals("", buffer.snapshot());
    }

    @Test
    public void multibyteLineIsTruncatedWithinByteLimit() {
        DiagnosticLogBuffer buffer = new DiagnosticLogBuffer(10);

        buffer.add("机器人机器人");

        assertTrue(buffer.snapshot().getBytes(StandardCharsets.UTF_8).length <= 9);
    }

    @Test
    public void truncationDoesNotLeaveUnpairedSurrogate() {
        String truncated = DiagnosticLogBuffer.truncateToUtf8Bytes("A😀B", 4);

        assertEquals("A", truncated);
    }

    @Test
    public void concurrentWritesProduceCompleteSnapshot() throws Exception {
        int threadCount = 8;
        int writesPerThread = 100;
        DiagnosticLogBuffer buffer = new DiagnosticLogBuffer(1024 * 1024);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch completionSignal = new CountDownLatch(threadCount);
        List<Thread> writers = new ArrayList<>();

        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            int writerId = threadIndex;
            Thread writer = new Thread(() -> {
                try {
                    startSignal.await();
                    for (int writeIndex = 0; writeIndex < writesPerThread; writeIndex++) {
                        buffer.add(writerId + "-" + writeIndex);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionSignal.countDown();
                }
            });
            writers.add(writer);
            writer.start();
        }

        startSignal.countDown();
        completionSignal.await();
        for (Thread writer : writers) {
            writer.join();
        }

        assertEquals(threadCount * writesPerThread, buffer.snapshot().split("\\n").length);
    }
}
