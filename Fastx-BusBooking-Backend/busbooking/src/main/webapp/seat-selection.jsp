<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Select Seats</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background-color: #fef3f3;
        }
        .seat {
            width: 50px;
            height: 50px;
            margin: 6px;
            border-radius: 5px;
            text-align: center;
            line-height: 45px;
            font-weight: bold;
            cursor: pointer;
            display: inline-block;
        }
        .available {
            background-color: #d4edda;
            border: 2px solid #28a745;
        }
        .booked {
            background-color: #f8d7da;
            border: 2px solid #dc3545;
            cursor: not-allowed;
        }
        .selected {
            background-color: #cce5ff;
            border: 2px solid #007bff;
        }
        .seat-grid {
            display: grid;
            grid-template-columns: repeat(5, 60px);
            justify-content: center;
        }
    </style>

    <script>
        function toggleSeat(seatId, divId) {
            const checkbox = document.getElementById("seatCheckbox" + seatId);
            const seatDiv = document.getElementById(divId);

            if (!checkbox || seatDiv.classList.contains("booked")) return;

            checkbox.checked = !checkbox.checked;
            seatDiv.classList.toggle("selected");
        }
    </script>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between mb-3">
        <h2>Select Seats for → ${route.origin} to ${route.destination}</h2>
        <form action="/logout" method="post">
            <button class="btn btn-danger">Logout</button>
        </form>
    </div>

    <form action="/passenger/booking" method="post">
        <input type="hidden" name="routeId" value="${route.id}" />

        <div class="seat-grid">
            <c:forEach var="seat" items="${seats}">
                <c:set var="seatDivId" value="seatDiv${seat.id}" />
                <div>
                    <c:choose>
                        <c:when test="${seat.booked}">
                            <div id="${seatDivId}" class="seat booked">
                                ${seat.seatNumber}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div id="${seatDivId}" class="seat available"
                                 onclick="toggleSeat('${seat.id}', '${seatDivId}')">
                                ${seat.seatNumber}
                            </div>
                            <input type="checkbox" name="selectedSeats"
                                   id="seatCheckbox${seat.id}" value="${seat.id}" style="display:none;" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </div>

        <div class="text-center mt-4">
            <button type="submit" class="btn btn-primary">Confirm Booking</button>
            <a href="/passenger" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
