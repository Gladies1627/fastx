<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body {
            background-color: #ffe6e6;
        }
        .tab-pane input[type="text"] {
            max-width: 300px;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <div class="d-flex justify-content-between mb-3">
        <h2>Admin Dashboard</h2>
        <form action="/logout" method="post">
            <button type="submit" class="btn btn-danger">Logout</button>
        </form>
    </div>

    <!-- Nav Tabs -->
    <ul class="nav nav-tabs" id="adminTabs" role="tablist">
        <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#users">Users</button></li>
        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#routes">Bus Routes</button></li>
        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#bookings">Bookings</button></li>
        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#cancellations">Cancellations</button></li>
    </ul>

    <!-- Tab Content -->
    <div class="tab-content mt-3" id="adminTabsContent">

        <!-- Users Tab -->
        <div class="tab-pane fade show active" id="users">
            <h4>All Users</h4>
            <input type="text" class="form-control mb-2" placeholder="Search users..." onkeyup="filterTable(this, 'userTable')">
            <table class="table table-striped table-bordered" id="userTable">
                <thead class="table-dark">
                <tr><th>ID</th><th>Name</th><th>Email</th><th>Contact</th><th>Gender</th><th>Role</th><th>Action</th></tr>
                </thead>
                <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.id}</td><td>${user.name}</td><td>${user.email}</td>
                        <td>${user.contactNumber}</td><td>${user.gender}</td><td>${user.role}</td>
                        <td>
                            <form method="post" action="/admin/deleteUser/${user.id}">
                                <button class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Routes Tab -->
        <div class="tab-pane fade" id="routes">
            <h4>Bus Routes</h4>
            <input type="text" class="form-control mb-2" placeholder="Search routes..." onkeyup="filterTable(this, 'routeTable')">
            <table class="table table-striped table-bordered" id="routeTable">
                <thead class="table-dark">
                <tr>
                    <th>ID</th><th>Bus Name</th><th>Type</th><th>Origin</th><th>Destination</th>
                    <th>Departure</th><th>Arrival</th><th>Fare</th><th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="route" items="${busRoutes}">
                    <tr>
                        <td>${route.id}</td><td>${route.busName}</td><td>${route.busType}</td>
                        <td>${route.origin}</td><td>${route.destination}</td>
                        <td>${route.departureTime}</td><td>${route.arrivalTime}</td>
                        <td>₹${route.farePerSeat}</td>
                        <td>
                            <a href="/admin/editRoute/${route.id}" class="btn btn-primary btn-sm">Edit</a>
                            <form method="post" action="/admin/deleteRoute/${route.id}" style="display:inline;">
    <button class="btn btn-danger btn-sm">Delete</button>
</form>

                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Bookings Tab -->
        <div class="tab-pane fade" id="bookings">
            <h4>All Bookings</h4>
            <input type="text" class="form-control mb-2" placeholder="Search bookings..." onkeyup="filterTable(this, 'bookingTable')">
            <table class="table table-striped table-bordered" id="bookingTable">
                <thead class="table-dark">
                <tr>
                    <th>ID</th><th>Passenger</th><th>Route</th><th>Seats</th><th>Status</th><th>Amount</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="booking" items="${bookings}">
                    <tr>
                        <td>${booking.id}</td>
                        <td>${booking.passenger.name}</td>
                        <td>${booking.route.origin} → ${booking.route.destination}</td>
                        <td>
    <c:forEach var="seat" items="${booking.seats}">
        ${seat.seatNumber}&nbsp;
    </c:forEach>
</td>
                        

                        <td>${booking.status}</td>
                        <td>₹${booking.totalAmount}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Cancellations Tab -->
        <div class="tab-pane fade" id="cancellations">
            <h4>All Cancellations</h4>
            <input type="text" class="form-control mb-2" placeholder="Search cancellations..." onkeyup="filterTable(this, 'cancelTable')">
            <table class="table table-striped table-bordered" id="cancelTable">
                <thead class="table-dark">
                <tr>
                    <th>ID</th><th>Passenger</th><th>Route</th><th>Reason</th><th>Cancelled At</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cancel" items="${cancellations}">
                    <tr>
                        <td>${cancel.id}</td>
                        <td>${cancel.booking.passenger.name}</td>
                        <td>${cancel.booking.route.origin} → ${cancel.booking.route.destination}</td>
                        <td>${cancel.reason}</td>
                        <td>${cancel.cancelledAt}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function filterTable(input, tableId) {
    const filter = input.value.toLowerCase();
    const rows = document.querySelectorAll(`#${tableId} tbody tr`);
    rows.forEach(row => {
        const text = row.innerText.toLowerCase();
        row.style.display = text.includes(filter) ? '' : 'none';
    });
}
</script>
</body>
</html>
