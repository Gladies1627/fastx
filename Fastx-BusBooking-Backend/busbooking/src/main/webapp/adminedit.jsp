<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Bus Route</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body style="background-color: #f2f2f2;">
<div class="container mt-4">
    <h2>Edit Bus Route</h2>
    <form:form modelAttribute="route" method="post" action="/admin/updateRoute" class="row g-3">
        <form:hidden path="id"/>
        <div class="col-md-6">
            <form:label path="busName">Bus Name</form:label>
            <form:input path="busName" cssClass="form-control"/>
        </div>
        <div class="col-md-6">
            <form:label path="busNumber">Bus Number</form:label>
            <form:input path="busNumber" cssClass="form-control"/>
        </div>
        <div class="col-md-6">
            <form:label path="busType">Bus Type</form:label>
            <form:select path="busType" cssClass="form-control">
                <form:option value="SEATER_AC" label="Seater AC"/>
                <form:option value="SEATER_NON_AC" label="Seater Non-AC"/>
                <form:option value="SLEEPER_AC" label="Sleeper AC"/>
                <form:option value="SLEEPER_NON_AC" label="Sleeper Non-AC"/>
            </form:select>
        </div>
        <div class="col-md-6">
            <form:label path="origin">Origin</form:label>
            <form:input path="origin" cssClass="form-control"/>
        </div>
        <div class="col-md-6">
            <form:label path="destination">Destination</form:label>
            <form:input path="destination" cssClass="form-control"/>
        </div>
        <div class="col-md-6">
            <form:label path="departureTime">Departure Time</form:label>
            <form:input path="departureTime" cssClass="form-control" type="datetime-local"/>
        </div>
        <div class="col-md-6">
            <form:label path="arrivalTime">Arrival Time</form:label>
            <form:input path="arrivalTime" cssClass="form-control" type="datetime-local"/>
        </div>
        <div class="col-md-6">
            <form:label path="farePerSeat">Fare</form:label>
            <form:input path="farePerSeat" cssClass="form-control" type="number"/>
        </div>
        <div class="col-md-6">
            <form:label path="totalSeats">Total Seats</form:label>
            <form:input path="totalSeats" cssClass="form-control" type="number"/>
        </div>
        <div class="col-md-6">
            <form:label path="amenities">Amenities</form:label>
            <form:input path="amenities" cssClass="form-control"/>
        </div>
        <div class="col-md-6">
            <label>Operator Name</label>
            <input type="text" class="form-control" value="${route.operator.name}" readonly />
        </div>

        <div class="col-12">
            <button type="submit" class="btn btn-primary">Update</button>
            <a href="/admin" class="btn btn-secondary">Cancel</a>
        </div>
    </form:form>
</div>
</body>
</html>
