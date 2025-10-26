package com.giraone.imaging.pdf;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Value object holding PDF document metadata information such as title, author, subject, keywords,
 * creator, producer, and timestamps. This class provides a wrapper around Apache PDFBox's
 * PDDocumentInformation for easier manipulation and conversion.
 */
public class PdfDocumentInformation {

    String title;
    String subject;
    String author;
    String keywords;
    String creator;
    String producer;
    Calendar creationDate;
    Calendar modificationDate;

    /**
     * Get the document title.
     * @return the document title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the document title.
     * @param title the document title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the document subject.
     * @return the document subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the document subject.
     * @param subject the document subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the document author.
     * @return the document author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the document author.
     * @param author the document author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get the document keywords.
     * @return the document keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Set the document keywords.
     * @param keywords the document keywords
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Get the document creator (the application that created the original document).
     * @return the document creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Set the document creator.
     * @param creator the document creator (the application that created the original document)
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Get the document producer (the application that converted it to PDF).
     * @return the document producer
     */
    public String getProducer() {
        return producer;
    }

    /**
     * Set the document producer.
     * @param producer the document producer (the application that converted it to PDF)
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * Get the document creation date.
     * @return the creation date
     */
    public Calendar getCreationDate() {
        return creationDate;
    }

    /**
     * Set the document creation date.
     * @param creationDate the creation date
     */
    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the document modification date.
     * @return the modification date
     */
    public Calendar getModificationDate() {
        return modificationDate;
    }

    /**
     * Set the document modification date.
     * @param modificationDate the modification date
     */
    public void setModificationDate(Calendar modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Set both creation and modification dates to the current date and time.
     */
    public void setDefaultDates() {
        Calendar now = new GregorianCalendar();
        this.setCreationDate(now);
        this.setModificationDate(now);
    }

    /**
     * Build and return a PDFBox PDDocumentInformation object from this instance.
     * Only non-null and non-empty fields are transferred to the PDDocumentInformation object.
     * @return a new PDDocumentInformation instance with the values from this object
     */
    public PDDocumentInformation build() {

        PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
        if (this.title != null && !this.title.trim().isEmpty()) {
            pdDocumentInformation.setTitle(this.title.trim());
        }
        if (this.subject != null && !this.subject.trim().isEmpty()) {
            pdDocumentInformation.setSubject(this.subject.trim());
        }
        if (this.author != null && !this.author.trim().isEmpty()) {
            pdDocumentInformation.setAuthor(this.author.trim());
        }
        if (this.keywords != null && !this.keywords.trim().isEmpty()) {
            pdDocumentInformation.setKeywords(this.keywords.trim());
        }
        if (this.creator != null && !this.creator.trim().isEmpty()) {
            pdDocumentInformation.setCreator(this.creator.trim());
        }
        if (this.producer != null && !this.producer.trim().isEmpty()) {
            pdDocumentInformation.setProducer(this.producer.trim());
        }
        if (this.creationDate != null) {
            pdDocumentInformation.setCreationDate(this.creationDate);
        }
        if (this.modificationDate != null) {
            pdDocumentInformation.setModificationDate(this.modificationDate);
        }
        return pdDocumentInformation;
    }

    /**
     * Build and return a PdfDocumentInformation object from a PDFBox PDDocumentInformation instance.
     * Only non-null and non-empty fields are transferred from the PDDocumentInformation object.
     * @param pdDocumentInformation the PDFBox document information to convert
     * @return a new PdfDocumentInformation instance with the values from the PDDocumentInformation object
     */
    public static PdfDocumentInformation build(PDDocumentInformation pdDocumentInformation) {
        PdfDocumentInformation pdfDocumentInformation = new PdfDocumentInformation();
        if (pdDocumentInformation.getTitle() != null && !pdDocumentInformation.getTitle().trim().isEmpty()) {
            pdfDocumentInformation.setTitle(pdDocumentInformation.getTitle().trim());
        }
        if (pdDocumentInformation.getSubject() != null && !pdDocumentInformation.getSubject().trim().isEmpty()) {
            pdfDocumentInformation.setSubject(pdDocumentInformation.getSubject().trim());
        }
        if (pdDocumentInformation.getAuthor() != null && !pdDocumentInformation.getAuthor().trim().isEmpty()) {
            pdfDocumentInformation.setAuthor(pdDocumentInformation.getAuthor().trim());
        }
        if (pdDocumentInformation.getKeywords() != null && !pdDocumentInformation.getKeywords().trim().isEmpty()) {
            pdfDocumentInformation.setKeywords(pdDocumentInformation.getKeywords().trim());
        }
        if (pdDocumentInformation.getCreator() != null && !pdDocumentInformation.getCreator().trim().isEmpty()) {
            pdfDocumentInformation.setCreator(pdDocumentInformation.getCreator().trim());
        }
        if (pdDocumentInformation.getProducer() != null && !pdDocumentInformation.getProducer().trim().isEmpty()) {
            pdfDocumentInformation.setProducer(pdDocumentInformation.getProducer().trim());
        }
        if (pdDocumentInformation.getCreationDate() != null) {
            pdfDocumentInformation.setCreationDate(pdDocumentInformation.getCreationDate());
        }
        if (pdDocumentInformation.getModificationDate() != null) {
            pdfDocumentInformation.setModificationDate(pdDocumentInformation.getModificationDate());
        }
        return pdfDocumentInformation;
    }

    @Override
    public String toString() {
        return "PdfDocumentInformation{" +
            "title='" + title + '\'' +
            ", subject='" + subject + '\'' +
            ", author='" + author + '\'' +
            ", keywords='" + keywords + '\'' +
            ", creator='" + creator + '\'' +
            ", producer='" + producer + '\'' +
            '}';
    }
}
