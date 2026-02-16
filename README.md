[![GitHub license](https://img.shields.io/github/license/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-2.0.0-blue)](https://mvnrepository.com/artifact/com.giraone.imaging/imaging-kit)
[![GitHub issues](https://img.shields.io/github/issues/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/issues)
[![GitHub stars](https://img.shields.io/github/stars/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/stargazers)
[![Platform](https://img.shields.io/badge/platform-jre21%2B-blue)](https://github.com/giraone/imaging-kit/pom.xml)

# Imaging Kit

A Java JAR for creating thumbnails and/or scaled versions of bitmap images (PNG, JPEG), videos and documents (PDF and Markdown) documents.
The concrete solutions are wrapped behind an interface. For bitmap images the implementation is based on plain Java2D.
See [java2/ProviderJava2D.java](src/main/java/com/giraone/imaging/java2/ProviderJava2D.java).

## Hint on thumbnails for videos

This feature is based on [ffmpeg](https://www.ffmpeg.org/). The Java code of this lib will only call an existing *ffmpeg* binary
on the machine. The location has to be defined by setting `FFMPEG_BIN`, e.g. `FFMPEG_BIN=/usr/bin/ffmpeg`.

## Integrating

```xml
<dependency>
    <groupId>com.giraone.imaging</groupId>
    <artifactId>imaging-kit</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Testing

### Test Images

- There are a few test images and test documents within `src/test/resources`.

### Tests

- ProviderBitmapImageTest.java - Simple functional tests for bitmap images
- ProviderPdfTest.java - Simple functional tests for PDF documents
- DefaultMarkdownProviderTest.java - Simple functional tests for PDF documents
- JpegScalePerformanceTest.java - A basic performance comparison on the two implementations for bitmap images

## Release Notes / Changes

See [CHANGELOG.md](CHANGELOG.md).