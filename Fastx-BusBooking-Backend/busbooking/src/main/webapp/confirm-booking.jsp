<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Confirm Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body style="background-color: #fef3f3;">
<div class="container mt-4">
    <h2 class="mb-3">Confirm Your Booking</h2>

    <form action="/passenger/finalize-booking" method="post">
    <input type="hidden" name="routeId" value="${route.id}" />
    <c:forEach var="seatId" items="${selectedSeatIds}">
        <input type="hidden" name="selectedSeats" value="${seatId}" />
    </c:forEach>

    <div class="mb-3">
        <label for="paymentMode" class="form-label"><strong>Choose Payment Mode:</strong></label>
        <select name="paymentMode" id="paymentMode" class="form-select" required>
            <option value="">-- Select --</option>
            <option value="UPI">UPI</option>
            <option value="CREDIT_CARD">Credit Card</option>
            <option value="DEBIT_CARD">Debit Card</option>
            <option value="NET_BANKING">Net Banking</option>
        </select>
    </div>

    <div class="text-center">
        <button type="submit" class="btn btn-success">Confirm Booking</button>
        <a href="/passenger/select-seat?routeId=${route.id}" class="btn btn-secondary">Go Back</a>
    </div>
</form>

</div>
</body>
</html>
