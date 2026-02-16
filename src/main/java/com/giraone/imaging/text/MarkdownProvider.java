package com.giraone.imaging.text;

import com.giraone.imaging.ThumbnailProvider;

/**
 * Interface for thumbnail generation operations on Markdown documents.
 */
public interface MarkdownProvider extends ThumbnailProvider {

    MarkdownProviderFlexmark _THIS = new MarkdownProviderFlexmark();

    /**
     * Get the singleton instance of the MarkdownProviderFlexmark.
     * @return the singleton instance
     */
    @SuppressWarnings("unused")
    static MarkdownProviderFlexmark getInstance() {
        return _THIS;
    }
}
