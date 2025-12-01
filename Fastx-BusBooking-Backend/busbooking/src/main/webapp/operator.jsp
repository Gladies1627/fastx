<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Operator | Manage Bus Routes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body style="background-color: #ffe6e6;">
<div class="container mt-4">
    <h2 class="mb-3">Operator Dashboard</h2>
    <div class="d-flex justify-content-end p-3">
    <form action="/logout" method="post">
        <button type="submit" class="btn btn-danger">Logout</button>
    </form>
</div>
    
    <c:if test="${not empty message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

   
    <form action="/operator/save" method="post" class="mb-4">
        <input type="hidden" name="id" value="${route.id}"/>

        <div class="row">
            <div class="col-md-6 mb-3">
                <input type="text" class="form-control" name="busName" placeholder="Bus Name" value="${route.busName}" required />
            </div>
            <div class="col-md-6 mb-3">
                <input type="text" class="form-control" name="busNumber" placeholder="Bus Number" value="${route.busNumber}" required />
            </div>
            <div class="col-md-6 mb-3">
                <input type="text" class="form-control" name="origin" placeholder="Origin" value="${route.origin}" required />
            </div>
            <div class="col-md-6 mb-3">
                <input type="text" class="form-control" name="destination" placeholder="Destination" value="${route.destination}" required />
            </div>
            <div class="col-md-6 mb-3">
                <input type="datetime-local" class="form-control" name="departureTime" placeholder="Departure Time" value="${route.departureTime}" required />
            </div>
            <div class="col-md-6 mb-3">
                <input type="datetime-local" class="form-control" name="arrivalTime" placeholder="Arrival Time" value="${route.arrivalTime}" required />
            </div>
            <div class="col-md-4 mb-3">
                <input type="number" class="form-control" name="totalSeats" placeholder="Total Seats" value="${route.totalSeats}" required />
            </div>
            <div class="col-md-4 mb-3">
                <input type="number" step="0.01" class="form-control" name="farePerSeat" placeholder="Fare per Seat" value="${route.farePerSeat}" required />
            </div>
            <div class="col-md-4 mb-3">
                <select class="form-select" name="busType" required>
                    <option value="" disabled selected>Select Bus Type</option>
                    <option value="SEATER_AC" ${route.busType == 'SEATER_AC' ? 'selected' : ''}>Seater AC</option>
                    <option value="SEATER_NON_AC" ${route.busType == 'SEATER_NON_AC' ? 'selected' : ''}>Seater Non-AC</option>
                    <option value="SLEEPER_AC" ${route.busType == 'SLEEPER_AC' ? 'selected' : ''}>Sleeper AC</option>
                    <option value="SLEEPER_NON_AC" ${route.busType == 'SLEEPER_NON_AC' ? 'selected' : ''}>Sleeper Non-AC</option>
                </select>
            </div>
            <div class="col-md-12 mb-3">
                <input type="text" class="form-control" name="amenities" placeholder="Amenities (comma-separated)" value="${route.amenities}" />
            </div>
        </div>
        <button type="submit" class="btn btn-primary w-100">Save Route</button>
    </form>

    <!-- Route Table -->
    <h4 class="mb-3">All Routes</h4>
    <table class="table table-striped table-bordered">
        <thead class="table-dark">
            <tr>
                <th>#</th>
                <th>Bus Name</th>
                <th>Bus No</th>
                <th>Origin</th>
                <th>Destination</th>
                <th>Departure</th>
                <th>Arrival</th>
                <th>Fare (₹)</th>
                <th>Seats</th>
                <th>Type</th>
                <th>Amenities</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="r" items="${routes}" varStatus="i">
                <tr>
                    <td>${i.count}</td>
                    <td>${r.busName}</td>
                    <td>${r.busNumber}</td>
                    <td>${r.origin}</td>
                    <td>${r.destination}</td>
                    <td>${r.departureTime}</td>
                    <td>${r.arrivalTime}</td>
                    <td>${r.farePerSeat}</td>
                    <td>${r.totalSeats}</td>
                    <td>${r.busType}</td>
                    <td>${r.amenities}</td>
                    <td>
                        <a href="/operator/edit/${r.id}" class="btn btn-sm btn-warning">Edit</a>
                        <a href="/operator/delete/${r.id}" class="btn btn-sm btn-danger">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
