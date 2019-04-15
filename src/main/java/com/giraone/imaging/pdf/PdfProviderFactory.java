package com.giraone.imaging.pdf;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Static wrapper for {@link PdfProvider} implementations.
 * This class is intended to fetch the configured provider from application settings at the first usage.
 */
public class PdfProviderFactory {

    private static PdfProviderFactory FACTORY;
    private ServiceLoader<PdfProvider> provider;

    private PdfProviderFactory() {
        this.provider = ServiceLoader.load(PdfProvider.class);
    }

    /**
     * Return singleton instance.
     */
    public static synchronized PdfProviderFactory getInstance() {
        if (FACTORY == null) {
            FACTORY = new PdfProviderFactory();
        }
        return FACTORY;
    }

    /**
     * Return the configured imaging provider instance.
     */
    public PdfProvider getProvider() {
        try {
            return this.provider.iterator().next();
        } catch (NoSuchElementException nse) {
            throw new IllegalStateException("Configuration failure! No provider for " + PdfProvider.class, nse);
        }
    }
}