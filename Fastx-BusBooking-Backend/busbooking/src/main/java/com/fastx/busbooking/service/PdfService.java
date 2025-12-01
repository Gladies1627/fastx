
package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.Seat;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public ByteArrayInputStream generateTicketPdf(Booking booking) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            Paragraph title = new Paragraph("🎫 FastX Ticket Confirmation")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

         
            document.add(new Paragraph("Booking ID: " + booking.getId()));
            document.add(new Paragraph("Passenger: " + booking.getPassenger().getName()));
            document.add(new Paragraph("Gender: " + booking.getPassenger().getGender()));
            document.add(new Paragraph("Contact: " + booking.getPassenger().getContactNumber()));
            document.add(new Paragraph("Email: " + booking.getPassenger().getEmail()));
            document.add(new Paragraph(" "));

    
            document.add(new Paragraph("Route: " + booking.getRoute().getOrigin() + " → " + booking.getRoute().getDestination()));
            document.add(new Paragraph("Bus: " + booking.getRoute().getBusName()));
            document.add(new Paragraph("Type: " + booking.getRoute().getBusType()));
            document.add(new Paragraph("Departure: " + booking.getRoute().getDepartureTime()));
            document.add(new Paragraph("Arrival: " + booking.getRoute().getArrivalTime()));
            document.add(new Paragraph("Fare per seat: ₹" + booking.getRoute().getFarePerSeat()));
            document.add(new Paragraph(" "));

        
            String seatNumbers = booking.getSeats().stream()
                    .map(Seat::getSeatNumber)
                    .collect(Collectors.joining(", "));
            document.add(new Paragraph("Seats: " + seatNumbers));
            document.add(new Paragraph("Total Paid: ₹" + booking.getTotalAmount()));
            document.add(new Paragraph("Payment Mode: " + booking.getPayment().getPaymentMode()));
            document.add(new Paragraph("Status: " + booking.getPayment().getPaymentStatus()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Thank you for booking with FastX!").setTextAlignment(TextAlignment.CENTER));
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
