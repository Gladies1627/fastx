<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Available Bus Routes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #ffe6e6;
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="d-flex justify-content-between mb-4">
        <h2>Available Bus Routes</h2>
        <form action="/logout" method="post">
            <button type="submit" class="btn btn-danger">Logout</button>
        </form>
    </div>

    <c:choose>
        <c:when test="${not empty routes}">
            <table class="table table-bordered table-striped">
                <thead class="table-dark">
                    <tr>
                        <th>#</th>
                        <th>Bus Name</th>
                        <th>Bus Number</th>
                        <th>Type</th>
                        <th>Origin</th>
                        <th>Destination</th>
                        <th>Departure</th>
                        <th>Arrival</th>
                        <th>Fare</th>
                        <th>Amenities</th>
                        <th>Available Seats</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="bus" items="${routes}" varStatus="status">
                        <tr>
                            <td>${status.count}</td>
                            <td>${bus.busName}</td>
                            <td>${bus.busNumber}</td>
                            <td>${bus.busType}</td>
                            <td>${bus.origin}</td>
                            <td>${bus.destination}</td>
                            <td>${bus.departureTime}</td>
                            <td>${bus.arrivalTime}</td>
                            <td>₹${bus.farePerSeat}</td>
                            <td>${bus.amenities}</td>
                            <td>
                                <c:out value="${seatAvailability[bus.id]}" default="0"/>
                            </td>
                            <td>
                                <a href="/passenger/select-seat?routeId=${bus.id}" class="btn btn-success btn-sm">Book Ticket</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="alert alert-warning text-center">
                No Buses Available for the selected route.
            </div>
        </c:otherwise>
    </c:choose>

    <div class="text-center mt-4">
        <a href="/passenger" class="btn btn-primary">Back to Search</a>
    </div>
</div>
</body>
</html>
