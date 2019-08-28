# Imaging Kit

A Java JAR for creating thumbnails and/or scaled versions of bitmap images (PNG, JPEG) and PDF documents.
The concrete solutions is wrapped behind an interface.  For bitmap images there are currently two versions:

- One based on IMGSCALR [https://github.com/rkalla/imgscalr](https://github.com/rkalla/imgscalr)
- One based on plain Java2D. See [java2/Provider.java](src/de/datev/dms/imaging/java2/Provider.java).

## Testing

### Test Images

- There is a small number of test images within `src/test/resources`.

### Tests

- ProviderBitmapImageTest.java - Simple functional tests for bitmap images
- ProviderPdfTest.java - Simple functional tests for PDF documents
- JpegScalePerformanceTest.java - A basic performance comparison on the two implementations for bitmap images

## Release Notes

- V1.0.1 (2019-08-28)
  - Refactoring / Sonar-Issue fixed

## TODO

- Use slf4j with default configuration file
