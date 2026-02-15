package com.giraone.imaging.text;

import com.giraone.imaging.ConversionCommand;
import com.giraone.imaging.FileInfo;
import com.giraone.imaging.ImagingProvider;
import com.giraone.imaging.java2.ProviderJava2D;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static com.giraone.imaging.ConversionCommand.MIME_TYPE_JPEG;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultMarkdownProviderTest {

    private static final ImagingProvider imagingProvider = new ProviderJava2D();
    private static final MarkdownProvider markdownProvider = DefaultMarkdownProvider.getInstance();

    @Test
    void createThumbnail() throws Exception {
        /// arrange
        File inputFile = new File("src/test/resources/document-01.md");
        File outputFile = File.createTempFile("md-to-jpeg-", ".jpg");
        outputFile.deleteOnExit();
        ConversionCommand.CompressionQuality quality = ConversionCommand.CompressionQuality.LOSSY_BEST;
        /// act
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            markdownProvider.createThumbnail(inputFile, out, MIME_TYPE_JPEG, 210 * 400/210, 297 * 400/210, quality);
        }
        /// assert
        assertThat(outputFile.exists());
        FileInfo fileInfo = imagingProvider.fetchFileInfo(outputFile);
        assertThat(fileInfo.getMimeType()).isEqualTo(MIME_TYPE_JPEG);
        assertThat(fileInfo.getWidth()).isEqualTo(400);
        assertThat(fileInfo.getHeight()).isEqualTo(565);

    }
}