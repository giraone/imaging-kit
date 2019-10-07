package com.giraone.imaging.java2;

import com.giraone.imaging.ImagingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Primitive ImageObserver, which logs for debugging only.
 * Its debug level should be enabled only in rare cases.
 */
public class LoggerImageObserver implements ImageObserver {

    private static final Logger LOGGER = LogManager.getLogger(ImagingProvider.class);

    private LoggerImageObserver() {
    }

    static LoggerImageObserver getInstance() {
        return new LoggerImageObserver();
    }

    public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("LoggerImageObserver.imageUpdate|infoFlags=" + infoFlags);
        }
        return true;
    }
}