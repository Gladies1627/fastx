<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Passenger Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <style>
        body {
            background-color: #f7f7f7;
        }
        .tab-pane {
            margin-top: 20px;
        }
        .booking-card {
            background-color: #fff;
            border-left: 6px solid #007bff;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 6px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <!-- Header & Logout -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Welcome Passenger</h2>
        <form action="/logout" method="post">
            <button type="submit" class="btn btn-danger">Logout</button>
        </form>
    </div>

    <!-- Success Message -->
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>

    <!-- Bootstrap Tabs -->
    <ul class="nav nav-tabs" id="dashboardTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="search-tab" data-bs-toggle="tab" data-bs-target="#search" type="button" role="tab">Search Buses</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="bookings-tab" data-bs-toggle="tab" data-bs-target="#bookings" type="button" role="tab">My Bookings</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="cancellations-tab" data-bs-toggle="tab" data-bs-target="#cancellations" type="button" role="tab">Cancellations</button>
        </li>
    </ul>

    <!-- Tab Content -->
    <div class="tab-content" id="dashboardTabsContent">

        <!-- 🔍 Search Tab -->
        <div class="tab-pane fade show active" id="search" role="tabpanel">
            <div class="card mt-3 p-4">
                <h4 class="mb-3">Search Available Buses</h4>
                <form action="/passenger/search" method="post">
                    <div class="row mb-3">
                        <div class="col">
                            <input type="text" name="origin" class="form-control" placeholder="From" required />
                        </div>
                        <div class="col">
                            <input type="text" name="destination" class="form-control" placeholder="To" required />
                        </div>
                        <div class="col">
                            <button type="submit" class="btn btn-primary w-100">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- 📦 Bookings Tab -->
        <div class="tab-pane fade" id="bookings" role="tabpanel">
            <h4 class="mt-4 mb-3">Your Bookings</h4>
            <c:choose>
                <c:when test="${empty bookings}">
                    <p class="text-muted">No bookings made yet.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="booking" items="${bookings}">
                        <c:if test="${booking.status eq 'CONFIRMED'}">
                            <div class="booking-card">
                                <p><strong>Booking ID:</strong> ${booking.id}</p>
                                <p><strong>Route:</strong> ${booking.route.origin} → ${booking.route.destination}</p>
                                <p><strong>Seats:</strong>
                                    <c:forEach var="seat" items="${booking.seats}">
                                        ${seat.seatNumber}
                                    </c:forEach>
                                </p>
                                <p><strong>Status:</strong> ${booking.status}</p>
                                <div class="d-flex gap-2">
                                    <form action="/passenger/cancel-booking" method="get">
                                        <input type="hidden" name="bookingId" value="${booking.id}" />
                                        <button type="submit" class="btn btn-warning btn-sm">Cancel</button>
                                    </form>
                                    <a href="/passenger/ticket/download?bookingId=${booking.id}" class="btn btn-success btn-sm">Download Ticket</a>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- ❌ Cancellations Tab -->
        <div class="tab-pane fade" id="cancellations" role="tabpanel">
            <h4 class="mt-4 mb-3">Cancelled Bookings</h4>
            <c:choose>
                <c:when test="${empty bookings}">
                    <p class="text-muted">No cancellations yet.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="booking" items="${bookings}">
                        <c:if test="${booking.status eq 'CANCELLED'}">
                            <div class="booking-card border-left-danger">
                                <p><strong>Booking ID:</strong> ${booking.id}</p>
                                <p><strong>Route:</strong> ${booking.route.origin} → ${booking.route.destination}</p>
                                <p><strong>Status:</strong> <span class="text-danger fw-bold">Cancelled</span></p>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</div>

<!-- Bootstrap JS (required for tabs) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
