package com.giraone.imaging.pdf;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Static wrapper for {@link PdfProvider} implementations.
 * This class is intended to fetch the configured provider from application settings at the first usage.
 */
public class PdfProviderFactory {

    private static PdfProviderFactory FACTORY;
    private final ServiceLoader<PdfProvider> provider;

    private PdfProviderFactory() {
        this.provider = ServiceLoader.load(PdfProvider.class);
    }

    /**
     * Provide one thread safe singleton instance of a factory, that returns the PDF provider.
     * @return the singleton instance of the factory
     */
    public static synchronized PdfProviderFactory getInstance() {
        if (FACTORY == null) {
            FACTORY = new PdfProviderFactory();
        }
        return FACTORY;
    }

    /**
     * Return the configured PDF provider instance.
     * @return the configured provided instance if found
     */
    public PdfProvider getProvider() {
        try {
            return this.provider.iterator().next();
        } catch (NoSuchElementException nse) {
            throw new IllegalStateException("Configuration failure! No provider for " + PdfProvider.class, nse);
        }
    }
}