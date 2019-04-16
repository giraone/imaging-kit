package com.giraone.imaging.pdf;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.util.Calendar;

public class PdfDocumentInformation {

    String title;
    String subject;
    String author;
    String keywords;
    String creator;
    String producer;
    Calendar creationDate;
    Calendar modificationDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Calendar modificationDate) {
        this.modificationDate = modificationDate;
    }

    public PDDocumentInformation build() {

        PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
        if (this.title != null && this.title.trim().length() > 0) {
            pdDocumentInformation.setTitle(this.title.trim());
        }
        if (this.subject != null && this.subject.trim().length() > 0) {
            pdDocumentInformation.setSubject(this.subject.trim());
        }
        if (this.author != null && this.author.trim().length() > 0) {
            pdDocumentInformation.setAuthor(this.author.trim());
        }
        if (this.keywords != null && this.keywords.trim().length() > 0) {
            pdDocumentInformation.setKeywords(this.keywords.trim());
        }
        if (this.creator != null && this.creator.trim().length() > 0) {
            pdDocumentInformation.setCreator(this.creator.trim());
        }
        if (this.producer != null && this.producer.trim().length() > 0) {
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

    public static PdfDocumentInformation build(PDDocumentInformation pdDocumentInformation) {
        PdfDocumentInformation pdfDocumentInformation = new PdfDocumentInformation();
        if (pdDocumentInformation.getTitle() != null && pdDocumentInformation.getTitle().trim().length() > 0) {
            pdfDocumentInformation.setTitle(pdDocumentInformation.getTitle().trim());
        }
        if (pdDocumentInformation.getSubject() != null && pdDocumentInformation.getSubject().trim().length() > 0) {
            pdfDocumentInformation.setSubject(pdDocumentInformation.getSubject().trim());
        }
        if (pdDocumentInformation.getAuthor() != null && pdDocumentInformation.getAuthor().trim().length() > 0) {
            pdfDocumentInformation.setAuthor(pdDocumentInformation.getAuthor().trim());
        }
        if (pdDocumentInformation.getKeywords() != null && pdDocumentInformation.getKeywords().trim().length() > 0) {
            pdfDocumentInformation.setKeywords(pdDocumentInformation.getKeywords().trim());
        }
        if (pdDocumentInformation.getCreator() != null && pdDocumentInformation.getCreator().trim().length() > 0) {
            pdfDocumentInformation.setCreator(pdDocumentInformation.getCreator().trim());
        }
        if (pdDocumentInformation.getProducer() != null && pdDocumentInformation.getProducer().trim().length() > 0) {
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
