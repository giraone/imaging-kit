# Imaging Kit

A Java JAR for creating thumbnails and/or scaled versions of bitmap images (PNG, JPEG) and PDF documents.
The concrete solutions is wrapped behind an interface. For bitmap images there are currently two versions:

- One based on IMGSCALR [https://github.com/rkalla/imgscalr](https://github.com/rkalla/imgscalr)
- One based on plain Java2D. See [java2/ProviderJava2D.java](src/main/java/com/giraone/imaging/java2/ProviderJava2D.java).

## Testing

### Test Images

- There are a few test images within `src/test/resources`.

### Tests

- ProviderBitmapImageTest.java - Simple functional tests for bitmap images
- ProviderPdfTest.java - Simple functional tests for PDF documents
- JpegScalePerformanceTest.java - A basic performance comparison on the two implementations for bitmap images

## Release Notes

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

## TODO

- Use slf4j with default configuration file
