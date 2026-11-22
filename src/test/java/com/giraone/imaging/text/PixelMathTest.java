package com.giraone.imaging.text;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PixelMathTest {

    @Test
    void mmToPixels() {
        // act
        int actual = PixelMath.mmToPixels(210.0f, 300);
        // assert
        assertThat(actual).isEqualTo(Math.round(300 * 210.0f / 25.4f)); // 1 inch = 25.4 mm
    }

    @Test
    void pixelToMm() {
        // act
        float actual = PixelMath.pixelToMm(2480, 300);
        // assert
        assertThat(actual).isEqualTo(Math.round(2480 * 25.4f / 300)); // 1 inch = 25.4 mm
    }

    @Test
    void ptToPixels() {
        // act
        int actual = PixelMath.ptToPixels(18, 300);
        // assert
        assertThat(actual).isEqualTo(75);
    }
}