package com.giraone.imaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ThumbnailProviderTest {

    @ParameterizedTest
    @CsvSource({
        "image/jpeg,ProviderJava2D",
        "application/pdf,PdfProviderPdfBox",
        "text/markdown,MarkdownProviderFlexmark",
        "video/mp4,VideoProviderFfmpeg"
    })
    void getThumbnailProvider(String mimeType, String expectedName) {
        // act
        ThumbnailProvider actual = ThumbnailProvider.getThumbnailProvider(mimeType);
        // assert
        assertThat(actual).isNotNull();
        assertThat(actual.getClass().getSimpleName()).isEqualTo(expectedName);
    }
}