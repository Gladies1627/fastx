<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<html>
<head>
    <title>Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<div class="container">
    <h2 class="mt-4">User Registration</h2>
    <form action="register" method="post">
        <input type="text" name="name" class="form-control mb-2" placeholder="Name" required>
        <input type="email" name="email" class="form-control mb-2" placeholder="Email" required>
        <input type="password" name="password" class="form-control mb-2" placeholder="Password" required>
        <input type="text" name="contactNumber" class="form-control mb-2" placeholder="Contact Number">
        <div class="mb-3">
    <label class="form-label">Gender:</label><br>
    <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" name="gender" value="Male" required>
        <label class="form-check-label">Male</label>
    </div>
    <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" name="gender" value="Female">
        <label class="form-check-label">Female</label>
    </div>
    <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" name="gender" value="Other">
        <label class="form-check-label">Other</label>
    </div>
</div>

        <input type="text" name="address" class="form-control mb-2" placeholder="Address">
        <select name="role" class="form-control mb-3" required>
            <option value="" disabled selected>Select Role</option>
            <option value="PASSENGER">Passenger</option>
            <option value="OPERATOR">Operator</option>
        </select>

        <button type="submit" class="btn btn-primary">Register</button>
    </form>
</div>
</body>
</html>
