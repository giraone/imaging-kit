[![GitHub license](https://img.shields.io/github/license/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.2.0-blue)](https://mvnrepository.com/artifact/com.giraone.imaging/imaging-kit)
[![GitHub issues](https://img.shields.io/github/issues/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/issues)
[![GitHub stars](https://img.shields.io/github/stars/giraone/imaging-kit)](https://github.com/giraone/imaging-kit/stargazers)
[![Platform](https://img.shields.io/badge/platform-jre8%2B-blue)](https://github.com/giraone/imaging-kit/pom.xml)

# Imaging Kit

A Java JAR for creating thumbnails and/or scaled versions of bitmap images (PNG, JPEG) and PDF documents.
The concrete solutions is wrapped behind an interface. For bitmap images the implementation is based on plain Java2D.
See [java2/ProviderJava2D.java](src/main/java/com/giraone/imaging/java2/ProviderJava2D.java).

## Integrating

```xml
<dependency>
    <groupId>com.giraone.imaging</groupId>
    <artifactId>imaging-kit</artifactId>
    <version>1.3.0</version>
</dependency>
```

## Testing

### Test Images

- There are a few test images within `src/test/resources`.

### Tests

- ProviderBitmapImageTest.java - Simple functional tests for bitmap images
- ProviderPdfTest.java - Simple functional tests for PDF documents
- JpegScalePerformanceTest.java - A basic performance comparison on the two implementations for bitmap images

## Release Notes

- V1.3.0 (2024-12-28)
  - Upgrade to a JDK 17 build source and target
  - Upgrade to latest dependencies (e.g. PDFBox 3.0.3)
  - The seconds implementation based on [IMGSCALR](https://github.com/rkalla/imgscalr](https://github.com/rkalla/imgscalr) was removed.
  - The parameter `ConversionCommand.SpeedHint speedHint` was removed
  - The interface supports now `java.nio.file.Path` besides `java.io.File`
- V1.2.0 (2022-10-31)
  - Upgrade to latest dependencies
- V1.1.0 (2021-12-19)
  - Dependencies on log4j2 removed and replaced by slf4j
  - Upgrade to latest dependencies
- V1.0.3 (2020-12-18)
  - Upgrade to latest dependencies
  - Sonar and JavaDoc issues fixed
- V1.0.2 (2019-10-07)
  - Upgrade to latest dependencies
  - Fixed unclosed PDDocument in countPages and getDocumentInformation of PdfProviderPdfBox
  - Old TIFF decoder removed
  - More tests added
- V1.0.1 (2019-08-28)
  - Refactoring / Sonar-Issue fixed
