package com.giraone.imaging;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Static wrapper for {@link ImagingProvider} implementations.
 * This class is intended to fetch the configured provider from application settings at the first usage.
 */
public class ImagingFactory {

    private static ImagingFactory factory;

    private ServiceLoader<ImagingProvider> provider;

    private ImagingFactory() {
        this.provider = ServiceLoader.load(ImagingProvider.class);
    }

    /**
     * Return singleton instance.
     */
    public static synchronized ImagingFactory getInstance() {
        if (factory == null) {
            factory = new ImagingFactory();
        }
        return factory;
    }

    /**
     * Return the configured imaging provider instance.
     */
    public ImagingProvider getProvider() {
        try {
            return this.provider.iterator().next();
        } catch (NoSuchElementException nse) {
            throw new IllegalStateException("Configuration failure! No provider for " + ImagingProvider.class, nse);
        }
    }
}