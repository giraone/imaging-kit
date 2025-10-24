package com.giraone.imaging.java2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Primitive ImageObserver, which logs for debugging only.
 * Its debug level should be enabled only in rare cases.
 */
public class LoggerImageObserver implements ImageObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerImageObserver.class);

    private LoggerImageObserver() {
    }

    static LoggerImageObserver getInstance() {
        return new LoggerImageObserver();
    }

    public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("LoggerImageObserver.imageUpdate|infoFlags={}", infoFlags);
        }
        return true;
    }
}