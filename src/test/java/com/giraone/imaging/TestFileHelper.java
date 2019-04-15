package com.giraone.imaging;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestFileHelper {

    public static Map<String, File> cloneTestFiles(Stream<String> resourceFiles) {

        HashMap<String, File> ret = new HashMap<>();

        resourceFiles.forEach((fileName) -> {

            try {
                File testFile = File.createTempFile("provider-input-", ".jpg");
                testFile.deleteOnExit();

                try (FileOutputStream out = new FileOutputStream(testFile)) {
                    try (InputStream in = ProviderPdfTest.class.getClassLoader().getResourceAsStream(fileName)) {
                        if (in != null) {
                            pipeBlobStream(in, out, 4096);
                        } else {
                            System.err.println("Cannot read test file \"" + fileName + "\"");
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                ret.put(fileName, testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return ret;
    }

    private static long pipeBlobStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
        long size = 0L;
        byte[] buf = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = in.read(buf)) > 0) {
            out.write(buf, 0, bytesRead);
            size += (long) bytesRead;
        }
        return size;
    }
}
