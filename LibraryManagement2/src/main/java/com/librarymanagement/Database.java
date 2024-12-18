package com.librarymanagement;

import com.librarymanagement.model.Book;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Random;

import com.itextpdf.kernel.pdf.PdfDocument;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class Database {
    private static Database database;

    public static Database getInstance() {
        if (database == null)
            database = new Database();
        return database;
    }

    private HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
    public String generateOtp(int otpLength) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10)); // Appends a random digit (0-9)
        }
        return otp.toString();
    }

    public void sendEmail(String recipient, String subject, String body, Book book) {
        final String senderEmail = "Your sender mail id";
        final String senderPassword = "Your sender app password";

        // SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        if (book == null) {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject(subject);
                message.setText(body);


                Transport.send(message);
                System.out.println("Email sent successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // Generate PDF content for the Book object
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(pdfOutputStream);
                PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
                Document document = new Document(pdfDoc);

                // Add book details to the PDF
                document.add(new Paragraph("Book Details").setBold().setFontSize(14));
                document.add(new Paragraph("Title: " + book.getName()));
                document.add(new Paragraph("Author: " + book.getAuthor()));
                document.add(new Paragraph("Publiser: " + book.getPublisher()));
                document.close();

                // Create email message
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject(subject);

                // Create message body part
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);

                // Create attachment part
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setFileName("BookDetails.pdf");
                attachmentPart.setContent(pdfOutputStream.toByteArray(), "application/pdf");

                // Combine parts
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
                message.setContent(multipart);

                // Send email
                Transport.send(message);
                System.out.println("Email with PDF sent successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
