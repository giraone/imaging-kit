[![GitHub license](https://img.shields.io/github/license/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.4.2-blue)](https://mvnrepository.com/artifact/com.giraone.imaging/imaging-kit)
[![GitHub issues](https://img.shields.io/github/issues/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/issues)
[![GitHub stars](https://img.shields.io/github/stars/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/stargazers)
[![Platform](https://img.shields.io/badge/platform-jre17%2B-blue)](https://github.com/giraone/imaging-kit/pom.xml)

# Imaging Kit

A Java JAR for creating thumbnails and/or scaled versions of bitmap images (PNG, JPEG) and PDF documents.
The concrete solutions is wrapped behind an interface. For bitmap images the implementation is based on plain Java2D.
See [java2/ProviderJava2D.java](src/main/java/com/giraone/imaging/java2/ProviderJava2D.java).

## Integrating

```xml
<dependency>
    <groupId>com.giraone.imaging</groupId>
    <artifactId>imaging-kit</artifactId>
    <version>1.4.0</version>
</dependency>
```

## Testing

### Test Images

- There are a few test images within `src/test/resources`.

### Tests

- ProviderBitmapImageTest.java - Simple functional tests for bitmap images
- ProviderPdfTest.java - Simple functional tests for PDF documents
- JpegScalePerformanceTest.java - A basic performance comparison on the two implementations for bitmap images

## Release Notes / Changes

See [CHANGELOG.md](CHANGELOG.md).