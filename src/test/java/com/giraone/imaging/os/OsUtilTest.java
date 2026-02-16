package com.giraone.imaging.os;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OsUtilTest {

    @Test
    void runCommandAndReadOutput() {
        ///  arrange
        String osName = System.getProperty("os.name");
        String cmd = osName.startsWith("Windows") ? "C:/Windows/System32/whoami.exe" : "/usr/bin/ls";
        /// act
        OsCommandResult result = OsUtil.runCommandAndReadOutput(new String[] {cmd});
        /// assert
        assertThat(result).isNotNull();
        assertThat(result.getException()).isNull();
    }
}