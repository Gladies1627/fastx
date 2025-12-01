<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bus Route Form</title>
    <div class="d-flex justify-content-end p-3">
    <form action="/logout" method="post">
        <button type="submit" class="btn btn-danger">Logout</button>
    </form>
</div>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body style="background-color: #ffe6e6;">
<div class="container mt-4">
    <h2 class="mb-4">${route.id == null ? "Add New Route" : "Edit Route"}</h2>

   <form:form action="/operator/add-route" method="post" modelAttribute="route" class="row g-3">

        <form:hidden path="id"/>

        <div class="col-md-6">
            <label class="form-label">Bus Name</label>
            <form:input path="busName" class="form-control"/>
        </div>

        <div class="col-md-6">
            <label class="form-label">Bus Number</label>
            <form:input path="busNumber" class="form-control"/>
        </div>

        <div class="col-md-6">
            <label class="form-label">Bus Type</label>
            <form:select path="busType" class="form-select">
                <form:option value="" label="-- Select --"/>
                <form:option value="SEATER_AC"/>
                <form:option value="SEATER_NON_AC"/>
                <form:option value="SLEEPER_AC"/>
                <form:option value="SLEEPER_NON_AC"/>
            </form:select>
        </div>

        <div class="col-md-6">
            <label class="form-label">Origin</label>
            <form:input path="origin" class="form-control"/>
        </div>

        <div class="col-md-6">
            <label class="form-label">Destination</label>
            <form:input path="destination" class="form-control"/>
        </div>

        <div class="col-md-6">
            <label class="form-label">Departure Time</label>
            <form:input path="departureTime" type="datetime-local" class="form-control"/>
        </div>

        <div class="col-md-6">
            <label class="form-label">Arrival Time</label>
            <form:input path="arrivalTime" type="datetime-local" class="form-control"/>
        </div>

        <div class="col-md-4">
            <label class="form-label">Fare Per Seat</label>
            <form:input path="farePerSeat" class="form-control"/>
        </div>

        <div class="col-md-4">
            <label class="form-label">Total Seats</label>
            <form:input path="totalSeats" class="form-control"/>
        </div>

        <div class="col-md-4">
            <label class="form-label">Amenities</label>
            <form:input path="amenities" class="form-control"/>
        </div>

        <div class="col-12">
            <button type="submit" class="btn btn-primary">Save Route</button>
            <a href="/operator" class="btn btn-secondary">Cancel</a>
        </div>
    </form:form>
</div>
</body>
</html>
