package com.giraone.imaging.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to run OS terminal commands and read the output.
 */
public class OsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsUtil.class);

    // Hide
    private OsUtil() {
    }

    /**
     * Run OS command with a default maximum time to wait for completion of 30 seconds
     * @param command Array with command and its CLI arguments.
     * @return structured result (exit code, STDOUT exception)
     */
    public static OsCommandResult runCommandAndReadOutput(String[] command) {
        return runCommandAndReadOutput(command, 30);
    }

    /**
     * Run OS command
     * @param command array with command and its CLI arguments.
     * @param maxWaitTimeInSeconds the maximum time to wait for completion of command
     * @return structured result (exit code, STDOUT exception)
     */
    public static OsCommandResult runCommandAndReadOutput(String[] command, int maxWaitTimeInSeconds) {
        final StringBuilder builder = new StringBuilder();
        Process p = null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("OsCommandResult.runCommandAndReadOutput: {}", commandStringFromArray(command));
        }
        final long start = System.currentTimeMillis();
        try {
            p = Runtime.getRuntime().exec(command);
            try {
                if (!p.waitFor(maxWaitTimeInSeconds, TimeUnit.SECONDS)) {
                    return new OsCommandResult(-3, "Command " + commandStringFromArray(command) + " timed out after 30 seconds!");
                }
            } catch (InterruptedException e) {
                return new OsCommandResult(-2, e);
            }

            if (LOGGER.isDebugEnabled()) {
                final long end = System.currentTimeMillis();
                LOGGER.debug("OsCommandResult.runCommandAndReadOutput: ExitCode = {}, Time = {}ms", p.exitValue(), (end - start));
            }

            try (BufferedReader commandResult = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String s;
                while ((s = commandResult.readLine()) != null) {
                    builder.append(s);
                    builder.append("\r\n");
                }
                return new OsCommandResult(p.exitValue(), builder.toString());
            }
        } catch (IOException e) {
            if (p != null)
                return new OsCommandResult(p.exitValue(), e);
            else
                return new OsCommandResult(-1, e);
        }
    }

    private static String commandStringFromArray(String[] command) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < command.length; i++) {
            if (i > 0) ret.append(" ");
            ret.append("\"");
            ret.append(command[i]);
            ret.append("\"");
        }
        return ret.toString();
    }
}
