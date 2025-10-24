# Guidelines for Agents

## Implementation Requirements

- Must run on *Linux*, *Windows*, *macOS*, *Android* and *iOS*.
- Build system is *Maven*.
  - Use installed `mvn`, no Maven wrapper (`mvnw`).
  - Use the latest versions of dependencies and plugins, that are not release candidates or milestones.
  - Use semantic versioning for releases of this project.
  - Use a property for all dependency versions and put it into the properties section of `pom.xml` using a name ending in `.version`.
- Programming language is *Java*.
  - Source and target JVM is *JDK 17*.
  - File encoding is *UTF-8*.
  - Use installed `java` and `javac` located in directory defined by environment variable `$JAVA_HOME`.
  - Use US English for *JavaDoc*.
- Dependencies for source code
  - Preferred library for PDF operations is *PDFBox*.
  - Logging framework is *SLF4J*.
    - Use static Logger from LoggerFactory, e.g. `private static final Logger LOG = LoggerFactory.getLogger(NameOfClass.class);`
- Dependencies for test code
  - Unit test framework is *JUnit5*.
  - Used assertion language in tests is *AssertJ*.
- Code Changes
  - Document summary of code changes in `CHANGELOG.md`. Add latest change on top.
  
## Naming Conventions for the Project

- The root Java package is `com.giraone.secrets`.
- The main library is named `secrets-lib`. Its root package is `com.giraone.secrets.main`.
- The cryptography library is named `secrets-crypto-lib`. Its root package is `com.giraone.secrets.crypto`.
- The storage library is named `secrets-storage-lib`. Its root package is `com.giraone.secrets.storage`.
- Follow consistent naming conventions across all libraries.

## General Code Formatting and Coding Conventions

- Follow Java guidelines for code formatting and coding conventions.
  - Use clear and descriptive names for classes, methods, and variables.
  - Avoid abbreviations unless they are widely recognized.
  - Use camelCase for method and variable names.
  - Use PascalCase for class and interface names.
- Respect indentation and spacing rules defined in [.editorconfig](.editorconfig).
- Write modular and reusable code.
- Include comments to explain complex logic.
- Do not use *var* for variable declarations, use explicit types.
- Do not use *Lombok*.
- Do not use mocking frameworks in tests, use test doubles, if needed.


## Convention for Writing Unit and Integration Tests

- Normal unit test for a class "Clazz.java" are located in the named "ClazzTest.java".
