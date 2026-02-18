package com.giraone.imaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to handle different image, video and document MIME types using together with imaging-kit.
 */
public class MimeTypes {
    /**
     * The default MIME type "application/octet-stream" as a string constant
     */
    public static final String DEFAULT = "application/octet-stream";
    /**
     * The default MIME type's extension
     */
    public static final String EXT_DEFAULT = "bin";
    /**
     * Useful MIME type "text/html" as a string constant
     */
    public static final String TEXT_HTML = "text/html";
    /**
     * Useful MIME type "text/plain" as a string constant
     */
    public static final String TEXT_PLAIN = "text/plain";
    /**
     * Useful MIME type "text/markdown" as a string constant
     */
    public static final String TEXT_MARKDOWN = "text/markdown";
    /**
     * Useful MIME type "application/pdf" as a string constant
     */
    public static final String APPLICATION_PDF = "application/pdf";
    /**
     * Useful MIME type "image/jpeg" as a string constant
     */
    public static final String IMAGE_JPEG = "image/jpeg";
    /**
     * Useful MIME type "image/png" as a string constant
     */
    public static final String IMAGE_PNG = "image/png";
    /**
     * Useful MIME type "image/gif" as a string constant
     */
    public static final String IMAGE_GIF = "image/gif";
    /**
     * Useful MIME type "video/mp4" as a string constant
     */
    public static final String VIDEO_MP4 = "video/mp4";

    /**
     * The constant "image/".
     */
    public static final String PREFIX_IMAGE = "image/";
    /**
     * The constant "video/".
     */
    public static final String PREFIX_VIDEO = "video/";
    /**
     * The constant "text/".
     */
    public static final String PREFIX_TEXT = "text/";

    private static final Map<String, String> MAP_EXT2MIME = new HashMap<>();
    private static final Map<String, String> MAP_MIME2EXT = new HashMap<>();

    static {
        MAP_EXT2MIME.put("jpeg", IMAGE_JPEG);
        MAP_EXT2MIME.put("jpg", IMAGE_JPEG);
        MAP_EXT2MIME.put("jp2", "image/jp2");
        MAP_EXT2MIME.put("tif", "image/tiff");
        MAP_EXT2MIME.put("tiff", "image/tiff");
        MAP_EXT2MIME.put("png", IMAGE_PNG);
        MAP_EXT2MIME.put("bmp", "image/bmp");
        MAP_EXT2MIME.put("gif", IMAGE_GIF);

        MAP_EXT2MIME.put("pdf", APPLICATION_PDF);

        MAP_EXT2MIME.put("txt", TEXT_PLAIN);
        MAP_EXT2MIME.put("md", TEXT_MARKDOWN);
        MAP_EXT2MIME.put("html", TEXT_HTML);
        MAP_EXT2MIME.put("htm", TEXT_HTML);
        MAP_EXT2MIME.put("xml", "text/xml");

        MAP_EXT2MIME.put("mpg", "video/mpeg");
        MAP_EXT2MIME.put("mp4", VIDEO_MP4);
        MAP_EXT2MIME.put("mpeg", "video/mpeg");
        MAP_EXT2MIME.put("avi", "video/x-msvideo");

        MAP_MIME2EXT.put(IMAGE_JPEG, "jpg");
        MAP_MIME2EXT.put("image/jp2", "jp2");
        MAP_MIME2EXT.put("image/tiff", "tif");
        MAP_MIME2EXT.put(IMAGE_PNG, "png");
        MAP_MIME2EXT.put("image/bmp", "bmp");
        MAP_MIME2EXT.put(IMAGE_GIF, "gif");

        MAP_MIME2EXT.put(APPLICATION_PDF, "pdf");

        MAP_MIME2EXT.put(TEXT_HTML, "html");
        MAP_MIME2EXT.put(TEXT_PLAIN, "txt");
        MAP_MIME2EXT.put(TEXT_MARKDOWN, "md");
        MAP_MIME2EXT.put("text/xml", "xml");

        MAP_MIME2EXT.put(VIDEO_MP4, "mp4");
        MAP_MIME2EXT.put("video/mpeg", "mpg");
        MAP_MIME2EXT.put("video/x-msvideo", "avi");
    }

    /**
     * Return MIME type for a file name or path
     * @param fileName file name
     * @return A MIME type (DEFAULT is "application/octet-stream";)
     */
    public static String getMimeTypeForFile(String fileName) {
        final int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1)
            return DEFAULT;
        else if (lastDot == (fileName.length() - 1))
            return DEFAULT;
        else
            return getMimeType(fileName.substring(lastDot + 1));
    }

    /**
     * Return MIME type for a given extension.
     * @param extension extension (without leading dot)
     * @return A MIME type (DEFAULT is "application/octet-stream";)
     */
    public static String getMimeType(String extension) {
        if (extension == null) {
            return DEFAULT;
        }
        final String ret = MAP_EXT2MIME.get(extension.toLowerCase());
        return Objects.requireNonNullElse(ret, DEFAULT);
    }

    /**
     * Return filename extension for a given MIME type.
     * @param mimeType the MIME type
     * @return A MIME type (DEFAULT is "bin";)
     */
    public static String getExtension(String mimeType) {
        if (mimeType == null) {
            return EXT_DEFAULT;
        }
        final String ret = MAP_MIME2EXT.get(mimeType.toLowerCase());
        return Objects.requireNonNullElse(ret, EXT_DEFAULT);
    }

    /**
     * Return true, if the given MIME type is an image.
     * @param mimeType input
     * @return true, when input is not null and starts with "image/"
     */
    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image");
    }
}
