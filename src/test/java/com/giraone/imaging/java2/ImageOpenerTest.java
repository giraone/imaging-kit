package com.giraone.imaging.java2;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ImageOpenerTest {

    @ParameterizedTest
    @CsvSource({
        "image-01.jpg, image/jpeg, 1024, 768, 2",
        "image-01.png, image/png, 800, 600, 4",
        "image-01.gif, image/gif, 100, 75, 0",
        "image-01.tif, image/tiff, 1419, 1001, 3",
        "image-03.tif, image/tiff, 2464, 3248, 6",
        "image-exif-01.jpg, image/jpeg, 6000, 4000, 2"
    })
    void openImage(String fileName, String expectedMimeType,
                   Integer expectedWidth, Integer expectedHeight,
                   Integer expectedCompressionFormat) throws IOException {

        // act
        ImagePlusInfo result1 = ImageOpener.openImage(new File("src/test/resources/" + fileName));
        // assert
        assertThat(result1).isNotNull();
        assertThat(result1.getImage()).isNotNull();
        assertThat(result1.getFileInfo()).isNotNull();
        assertThat(result1.getFileInfo().getMimeType()).isEqualTo(expectedMimeType);
        assertThat(result1.getFileInfo().getWidth()).isEqualTo(expectedWidth);
        assertThat(result1.getFileInfo().getHeight()).isEqualTo(expectedHeight);
        if (expectedCompressionFormat != null) {
            assertThat(result1.getFileInfo().getCompressionFormat()).isEqualTo(expectedCompressionFormat);
        }

        // act
        ImagePlusInfo result2 = ImageOpener.openImage(Path.of("src/test/resources", fileName));
        // assert
        assertThat(result2).isNotNull();
        assertThat(result2.getImage()).isNotNull();
        assertThat(result2.getFileInfo()).isNotNull();
        assertThat(result2.getFileInfo().getMimeType()).isEqualTo(expectedMimeType);
        assertThat(result2.getFileInfo().getWidth()).isEqualTo(expectedWidth);
        assertThat(result2.getFileInfo().getHeight()).isEqualTo(expectedHeight);
    }
}
