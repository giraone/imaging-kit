package com.giraone.imaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestFileHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TestFileHelper.class);

    public static Map<String, File> cloneTestFiles(Stream<String> resourceFiles) {

        HashMap<String, File> ret = new HashMap<>();
        resourceFiles.forEach((fileName) -> {

            File clonedFile = cloneTestFile(fileName);
            if (clonedFile != null) {
                ret.put(fileName, clonedFile);
            }
        });
        return ret;
    }

    public static File cloneTestFile(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            File testFile = File.createTempFile("provider-input-", extension);
            testFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(testFile)) {
                try (InputStream in = TestFileHelper.class.getClassLoader().getResourceAsStream(fileName)) {
                    if (in != null) {
                        pipeBlobStream(in, out, 4096);
                    } else {
                        System.err.println("Cannot read test file \"" + fileName + "\"");
                    }
                } catch (IOException ioe) {
                    LOG.error("cloneTestFile failed", ioe);
                    return null;
                }
            }
            return testFile;
        } catch (Exception e) {
            LOG.error("cloneTestFile failed", e);
            return null;
        }
    }

    public static byte[] readTestFile(String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            File testFile = File.createTempFile("provider-input-", extension);
            testFile.deleteOnExit();

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (InputStream in = TestFileHelper.class.getClassLoader().getResourceAsStream(fileName)) {
                    if (in != null) {
                        pipeBlobStream(in, out, 4096);
                    } else {
                        System.err.println("Cannot read test file \"" + fileName + "\"");
                    }
                } catch (IOException ioe) {
                    LOG.error("readTestFile failed", ioe);
                    return null;
                }
                return out.toByteArray();
            }

        } catch (Exception e) {
            LOG.error("readTestFile failed", e);
            return null;
        }
    }

    private static long pipeBlobStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
        long size = 0L;
        byte[] buf = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = in.read(buf)) > 0) {
            out.write(buf, 0, bytesRead);
            size += bytesRead;
        }
        return size;
    }
}
