
package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.Seat;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Paragraph;


import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendBookingConfirmation(Booking booking) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(booking.getPassenger().getEmail());
        helper.setSubject("FastX Booking Confirmation");
        helper.setText("Dear " + booking.getPassenger().getName() + ",\n\nYour booking is confirmed. Ticket is attached as PDF.");

        // Generate PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("FastX - Ticket Confirmation").setBold());
        document.add(new Paragraph("Booking ID: " + booking.getId()));
        document.add(new Paragraph("Passenger: " + booking.getPassenger().getName()));
        document.add(new Paragraph("Email: " + booking.getPassenger().getEmail()));
        document.add(new Paragraph("Seats: " +
                booking.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.joining(", "))));
        document.add(new Paragraph("Route: " +
                booking.getRoute().getOrigin() + " to " + booking.getRoute().getDestination()));
        document.add(new Paragraph("Date: " + booking.getTravelDate()));
        document.add(new Paragraph("Amount Paid: ₹" + booking.getTotalAmount()));
        document.close();

        InputStreamSource attachment = new ByteArrayResource(out.toByteArray());

        helper.addAttachment("FastX-Ticket.pdf", attachment);

        javaMailSender.send(message);
    }
    public void sendCancellationMail(Booking booking, String reason, double refundAmount) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(booking.getPassenger().getEmail());
        helper.setSubject("FastX Booking Cancelled");

        String content = "Dear " + booking.getPassenger().getName() + ",\n\n" +
                "Your booking (ID: " + booking.getId() + ") has been cancelled.\n\n" +
                "Reason: " + reason + "\n" +
                "Refund Amount: ₹" + refundAmount + "\n\n" +
                "Route: " + booking.getRoute().getOrigin() + " → " + booking.getRoute().getDestination() + "\n" +
                "Date: " + booking.getTravelDate() + "\n\n" +
                "Thank you,\nFastX Team";

        helper.setText(content);
        javaMailSender.send(message);
    }
    public void sendCancellationNotice(Booking booking) throws MessagingException {
        String to = booking.getPassenger().getEmail();
        String subject = "Bus Route Cancellation - Booking ID " + booking.getId();
        String body = "Dear " + booking.getPassenger().getName() + ",\n\n" +
                      "We regret to inform you that your bus route from " + 
                      booking.getRoute().getOrigin() + " to " + booking.getRoute().getDestination() +
                      " has been cancelled. Your booking ID " + booking.getId() + " is now marked as CANCELLED.\n\n" +
                      "Thank you,\nFastX Support Team";
        sendEmail(to, subject, body);  
    }
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        javaMailSender.send(message);
    }




}
