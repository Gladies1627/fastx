

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cancel Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body style="background-color: #fff3f3;">
<div class="container mt-4">
    <h2 class="text-danger">Cancel Your Booking</h2>
    <p><strong>Booking ID:</strong> ${booking.id}</p>
    <p><strong>Passenger:</strong> ${booking.passenger.name}</p>
    <p><strong>Route:</strong> ${booking.route.origin} → ${booking.route.destination}</p>
    <p><strong>Seats:</strong>
        <c:forEach var="seat" items="${booking.seats}">${seat.seatNumber} </c:forEach>
    </p>
    <p><strong>Total Paid:</strong> ₹${booking.totalAmount}</p>

    <form action="/passenger/cancel" method="post">
        <input type="hidden" name="bookingId" value="${booking.id}" />
        <div class="mb-3">
            <label for="reason" class="form-label">Reason for cancellation:</label>
            <textarea class="form-control" name="reason" required></textarea>
        </div>
        <div class="text-center">
            <button class="btn btn-danger" type="submit">Confirm Cancellation</button>
            <a href="/passenger" class="btn btn-secondary">Go Back</a>
        </div>
    </form>
</div>
</body>
</html>
