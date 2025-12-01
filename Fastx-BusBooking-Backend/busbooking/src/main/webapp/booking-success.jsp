
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Booking Confirmed</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background-color: #eaffea;
            font-size: 14px;
        }
        .info-section {
            padding: 15px;
            border: 1px solid #ccc;
            border-radius: 8px;
            background-color: #ffffff;
            margin-bottom: 12px;
        }
        .info-title {
            font-weight: bold;
            margin-bottom: 8px;
            font-size: 16px;
            color: #007b5e;
        }
    </style>
</head>
<body>
<div class="container mt-3">
    <h4 class="text-success text-center mb-3">🎉 Booking Confirmed!</h4>

    <div class="row">
        <!-- Booking Details -->
        <div class="col-md-6 info-section">
            <div class="info-title">Booking Info</div>
            <p><strong>Booking ID:</strong> ${booking.id}</p>
            <p><strong>Travel Date:</strong> ${booking.travelDate}</p>
            <p><strong>Seats:</strong> 
               <c:forEach var="seat" items="${seats}">${seat.seatNumber} </c:forEach>
    </p>
    <p><strong>Total Paid:</strong> ₹${booking.totalAmount}</p>
    <p><strong>Payment Mode:</strong> ${payment.paymentMode}</p>
    <p><strong>Status:</strong> ${payment.paymentStatus}</p>
</div>

        <!-- Passenger Info -->
        <div class="col-md-6 info-section">
            <div class="info-title">Passenger Info</div>
            <p><strong>Name:</strong> ${booking.passenger.name}</p>
            <p><strong>Gender:</strong> ${booking.passenger.gender}</p>
            <p><strong>Email:</strong> ${booking.passenger.email}</p>
            <p><strong>Contact:</strong> ${booking.passenger.contactNumber}</p>
        </div>
    </div>

    <div class="row">
        <!-- Route Info -->
        <div class="col-md-12 info-section">
            <div class="info-title">Bus & Route Info</div>
            <div class="row">
                <div class="col-md-4">
                    <p><strong>From:</strong> ${route.origin}</p>
                    <p><strong>To:</strong> ${route.destination}</p>
                    <p><strong>Fare/Seat:</strong> ₹${route.farePerSeat}</p>
                </div>
                <div class="col-md-4">
                    <p><strong>Bus:</strong> ${route.busName}</p>
                    <p><strong>Type:</strong> ${route.busType}</p>
                    <p><strong>Amenities:</strong> ${route.amenities}</p>
                </div>
                <div class="col-md-4">
                    <p><strong>Departure:</strong> ${route.departureTime}</p>
                    <p><strong>Arrival:</strong> ${route.arrivalTime}</p>
                </div>
            </div>
        </div>
        
    </div>

    <div class="text-center mt-2">
        <a href="/passenger" class="btn btn-sm btn-primary">Back to Dashboard</a>
        <a href="/passenger/ticket/download?bookingId=${booking.id}" class="btn btn-info mt-3">Download Ticket PDF</a>
        <a href="/passenger/cancel-booking?bookingId=${booking.id}" class="btn btn-danger mt-3">Cancel Booking</a>
        
    </div>
</div>
</body>
</html>
