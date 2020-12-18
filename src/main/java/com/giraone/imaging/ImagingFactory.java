package com.giraone.imaging;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Static wrapper for {@link ImagingProvider} implementations.
 * This class is intended to fetch the configured provider from application settings at the first usage.
 */
public class ImagingFactory {

    private static ImagingFactory factory;

    private final ServiceLoader<ImagingProvider> provider;

    private ImagingFactory() {
        this.provider = ServiceLoader.load(ImagingProvider.class);
    }

    /**
     * Provide one thread safe singleton instance of a factory, that returns the imaging provider.
     * @return the singleton instance of the factory
     */
    public static synchronized ImagingFactory getInstance() {
        if (factory == null) {
            factory = new ImagingFactory();
        }
        return factory;
    }

    /**
     * Return the configured imaging provider instance.
     * @return the configured provided instance if found
     */
    public ImagingProvider getProvider() {
        try {
            return this.provider.iterator().next();
        } catch (NoSuchElementException nse) {
            throw new IllegalStateException("Configuration failure! No provider for " + ImagingProvider.class, nse);
        }
    }
}