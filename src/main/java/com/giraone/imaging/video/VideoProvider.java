package com.giraone.imaging.video;

import com.giraone.imaging.ThumbnailProvider;

/**
 *  Interface for thumbnail generation operations on videos (MP4).
 */
public interface VideoProvider extends ThumbnailProvider {

    VideoProvider _THIS = new VideoProviderFfmpeg();

    /**
     * Get the singleton instance of the VideoProviderFfmpeg.
     * @return the singleton instance
     */
    @SuppressWarnings("unused")
    static VideoProvider getInstance() {
        return _THIS;
    }
}
