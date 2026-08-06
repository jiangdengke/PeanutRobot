package com.yuandaima.peanutrobot.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RotatingLogFileStoreTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void retainedLinesCanBeLoadedByNewStoreInstance() throws Exception {
        File logDirectory = new File(temporaryFolder.getRoot(), "logs");
        RotatingLogFileStore writer = new RotatingLogFileStore(logDirectory, "runtime", 128, 3);

        writer.append("first");
        writer.append("second");

        RotatingLogFileStore reader = new RotatingLogFileStore(logDirectory, "runtime", 128, 3);
        assertEquals(Arrays.asList("first", "second"), reader.readLines());
    }

    @Test
    public void rotationKeepsNewestFilesInChronologicalOrder() throws Exception {
        File logDirectory = new File(temporaryFolder.getRoot(), "logs");
        RotatingLogFileStore store = new RotatingLogFileStore(logDirectory, "runtime", 16, 3);

        store.append("0000000000");
        store.append("1111111111");
        store.append("2222222222");
        store.append("3333333333");

        assertEquals(
                Arrays.asList("1111111111", "2222222222", "3333333333"),
                store.readLines()
        );
    }

    @Test
    public void clearDeletesAllRetainedLines() throws Exception {
        File logDirectory = new File(temporaryFolder.getRoot(), "logs");
        RotatingLogFileStore store = new RotatingLogFileStore(logDirectory, "runtime", 16, 3);
        store.append("first-entry");
        store.append("second-entry");

        store.clear();

        assertTrue(store.readLines().isEmpty());
    }

    @Test
    public void oversizedEntryCannotExceedFileLimit() throws Exception {
        File logDirectory = new File(temporaryFolder.getRoot(), "logs");
        RotatingLogFileStore store = new RotatingLogFileStore(logDirectory, "runtime", 16, 3);

        store.append("机器人机器人机器人机器人");

        File[] files = logDirectory.listFiles();
        assertTrue(files != null && files.length == 1);
        assertTrue(files[0].length() <= 16);
    }

    @Test
    public void invalidDirectoryReportsIoFailure() throws Exception {
        File pathThatIsAFile = temporaryFolder.newFile("not-a-directory");
        try (FileOutputStream output = new FileOutputStream(pathThatIsAFile)) {
            output.write(1);
        }
        RotatingLogFileStore store = new RotatingLogFileStore(
                pathThatIsAFile,
                "runtime",
                16,
                3
        );

        try {
            store.append("entry");
            fail("Expected append to reject a non-directory path");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("directory"));
        }
    }
}
