module com.giraone.imaging {

    // 1. Export the public packages you want other apps to use
    exports com.giraone.imaging;
    exports com.giraone.imaging.java2;
    exports com.giraone.imaging.os;
    exports com.giraone.imaging.pdf;
    exports com.giraone.imaging.text;
    exports com.giraone.imaging.video;

    provides com.giraone.imaging.ImagingProvider with
        com.giraone.imaging.java2.ProviderJava2D;
    provides com.giraone.imaging.pdf.PdfProvider with
        com.giraone.imaging.pdf.PdfProviderPdfBox;

    // 2. Declare what external modules this library depends on
    requires org.apache.pdfbox;
    requires openhtmltopdf.pdfbox;
    requires flexmark;
    requires flexmark.util.ast;
    requires java.xml;
    requires flying.saucer;
    requires org.slf4j;

    // 3. (Optional) Pass a dependency downstream to consumers of your library
    requires transitive java.desktop;

    uses com.giraone.imaging.ImagingProvider;
    uses com.giraone.imaging.pdf.PdfProvider;

    // Allows JUnit to access test classes via reflection
    //opens com.giraone.imaging to org.junit.platform.commons;
}
