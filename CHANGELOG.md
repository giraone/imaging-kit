# Release Notes and List of Changes

## V1.4.0 (unreleased)

### New Features
-  **Added PNG output format support** - images can now be converted to PNG format (lossless compression)
-  **Added GIF output format support** - images can now be converted to GIF format
-  Updated `FileTypeDetector.isSupportedImage()` to include GIF format

### API Improvements
-  **Replaced generic `Exception` with specific exception types** in `ImagingProvider` interface methods
   - Methods now throw `IOException`, `FormatNotSupportedException`, and `ImageConversionException` instead of generic `Exception`
   - Created new `ImageConversionException` class for image processing errors
   - **Breaking Change**: Code catching generic `Exception` should be updated to catch specific exceptions
-  Enhanced error messages to include supported format lists when unsupported format is requested
-  Added new `convertAndWriteImage()` method in `ProviderJava2D` to support multiple output formats
-  Marked `convertAndWriteImageAsJpeg()` as `@Deprecated` in favor of the new multi-format method

### Documentation Improvements
-  **Added comprehensive JavaDoc** to public API methods:
   - `FileInfo` getter/setter methods with detailed descriptions
   - `ConversionCommand` methods with parameter explanations
   - `CompressionQuality` enum with important notes about JPEG's inherent lossy compression
-  **Clarified misleading LOSSLESS quality enum** - added JavaDoc warning that LOSSLESS produces maximum quality lossy compression for JPEG (not truly lossless)
-  Improved JavaDoc for image format writers (`ImageToFileWriter` class)

### Bug Fixes
-  Fixed stream closing bug in `FileTypeDetector.getFileType(InputStream)` - now correctly honors the documented contract that the stream will not be closed
-  Fixed AGENTS.md to contain correct imaging-kit project information instead of wrong secrets project references
-  Updated README.md platform badge to reflect correct Java 17 requirement (was incorrectly showing Java 8)

### Dependency Updates
-  Upgrade to latest dependencies (e.g. PDFBox 3.0.6)

## V1.3.0 (2024-12-28)
-  Upgrade to a JDK 17 build source and target
-  Upgrade to latest dependencies (e.g. PDFBox 3.0.3)
-  The seconds implementation based on [IMGSCALR](https://github.com/rkalla/imgscalr](https://github.com/rkalla/imgscalr) was removed.
-  The parameter `ConversionCommand.SpeedHint speedHint` was removed
-  The interface supports now `java.nio.file.Path` besides `java.io.File`

## V1.2.0 (2022-10-31)
-  Upgrade to latest dependencies

## V1.1.0 (2021-12-19)
-  Dependencies on log4j2 removed and replaced by slf4j
-  Upgrade to latest dependencies

## V1.0.3 (2020-12-18)
-  Upgrade to latest dependencies
-  Sonar and JavaDoc issues fixed

## V1.0.2 (2019-10-07)
-  Upgrade to latest dependencies
-  Fixed unclosed PDDocument in countPages and getDocumentInformation of PdfProviderPdfBox
-  Old TIFF decoder removed
-  More tests added

## V1.0.1 (2019-08-28)
-  Refactoring / Sonar-Issue fixed