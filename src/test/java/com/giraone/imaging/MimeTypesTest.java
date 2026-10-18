package com.giraone.imaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MimeTypesTest {

    @ParameterizedTest
    @CsvSource({"""
        test.txt,text/plain
        test.jpg,image/jpeg
        test,application/octet-stream
        test.,application/octet-stream
        """})
    void getMimeTypeForFile(String fileName, String expectedMimeType) {
        assertThat(MimeTypes.getMimeTypeForFile(fileName)).isEqualTo(expectedMimeType);
    }

    @ParameterizedTest
    @CsvSource({"""
        ,bin
        text/plain,txt
        image/jpeg,jpg
        """})
    void getExtension(String mimeType, String expectedExtension) {
        assertThat(MimeTypes.getExtension(mimeType)).isEqualTo(expectedExtension);
    }
}
